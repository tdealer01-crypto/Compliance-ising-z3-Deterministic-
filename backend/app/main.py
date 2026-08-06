from __future__ import annotations

import contextlib
import hmac
import json
from typing import Any

from fastapi import Depends, FastAPI, Header, HTTPException, status
from mcp.server.fastmcp import FastMCP

from .audit import AuditStore
from .config import load_settings
from .models import VerificationRequest, VerificationResponse
from .service import VerificationService
from .verifier import verify_candidate

settings = load_settings()
audit_store = AuditStore(settings.audit_db_path)
verification_service = VerificationService(audit_store, settings.proof_signing_secret)

mcp = FastMCP(
    "DSG QUBO Z3 Verification",
    instructions=(
        "Verify a candidate QUBO policy configuration with server-side Z3 and return "
        "a tamper-evident proof and audit-chain event."
    ),
    stateless_http=True,
    json_response=True,
    streamable_http_path="/",
)


@mcp.tool()
def verify_policy_solution(payload: dict[str, Any]) -> dict[str, Any]:
    """Verify one candidate policy solution with Z3 and persist its audit proof."""
    request = VerificationRequest.model_validate(payload)
    return verification_service.verify(request).model_dump(mode="json")


@mcp.tool()
def validate_policy_payload(payload: dict[str, Any]) -> dict[str, Any]:
    """Validate a policy payload and report Z3 status without writing an audit event."""
    request = VerificationRequest.model_validate(payload)
    outcome = verify_candidate(request)
    return {
        "request_id": request.request_id,
        "z3_status": outcome.status,
        "accepted": outcome.accepted,
        "unsat_core": outcome.unsat_core,
        "constraint_results": [
            item.model_dump(mode="json") for item in outcome.constraint_results
        ],
        "solver_version": outcome.solver_version,
    }


@mcp.resource("dsg://capabilities")
def capabilities_resource() -> str:
    """Describe the verification service's supported capabilities."""
    return json.dumps(
        {
            "service": "DSG QUBO Z3 Verification",
            "transport": "MCP Streamable HTTP",
            "tools": ["verify_policy_solution", "validate_policy_payload"],
            "audit": "SQLite tamper-evident hash chain",
        },
        sort_keys=True,
    )


@contextlib.asynccontextmanager
async def lifespan(_: FastAPI):
    audit_store.initialize()
    async with mcp.session_manager.run():
        yield


app = FastAPI(
    title="DSG Compliance Verification Backend",
    version="1.0.0",
    lifespan=lifespan,
)
app.mount("/mcp", mcp.streamable_http_app())


def require_api_key(
    x_api_key: str | None = Header(default=None),
    authorization: str | None = Header(default=None),
) -> None:
    expected = settings.api_key
    if expected is None:
        return
    bearer = authorization.removeprefix("Bearer ").strip() if authorization else None
    provided = x_api_key or bearer
    if provided is None or not hmac.compare_digest(provided, expected):
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Missing or invalid API key",
        )


@app.get("/health")
def health() -> dict[str, Any]:
    return {
        "status": "ok",
        "service": "dsg-compliance-backend",
        "mcp_endpoint": "/mcp",
    }


@app.get("/v1/capabilities", dependencies=[Depends(require_api_key)])
def capabilities() -> dict[str, Any]:
    return {
        "server_side_z3": True,
        "tamper_evident_audit_chain": True,
        "proof_signature_enabled": settings.proof_signing_secret is not None,
        "mcp_transport": "streamable-http",
        "mcp_endpoint": "/mcp",
    }


@app.post(
    "/v1/verify",
    response_model=VerificationResponse,
    dependencies=[Depends(require_api_key)],
)
def verify(request: VerificationRequest) -> VerificationResponse:
    return verification_service.verify(request)


@app.get("/v1/audit/{event_hash}", dependencies=[Depends(require_api_key)])
def audit_event(event_hash: str) -> dict[str, Any]:
    event = audit_store.get(event_hash)
    if event is None:
        raise HTTPException(status_code=404, detail="Audit event not found")
    return event
