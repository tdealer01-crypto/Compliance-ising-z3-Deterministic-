# DSG QUBO & Ising Policy Solver

Android QUBO/Ising policy optimization client with a deployable backend for **real server-side Z3 verification**, proof generation, audit persistence, and MCP access.

## What is real in this repository

| Capability | Implementation |
|---|---|
| Deterministic QUBO heuristic | Native Kotlin simulated annealing in the Android app |
| Local constraint check | Native Kotlin Boolean/cost validation; this is **not Z3** |
| Formal verification | Python backend executes the submitted candidate with `z3-solver` |
| Proof | Backend hashes the canonical request and Z3 result with SHA-256; optional HMAC signing |
| Audit | Backend persists a tamper-evident SQLite hash chain |
| MCP | Official Python MCP SDK, Streamable HTTP endpoint at `/mcp` |
| Android/backend connection | OkHttp `POST /v1/verify`; success is shown only from the actual HTTP/Z3 response |

The previous simulated provider responses and hard-coded `200 OK / VERIFIED_100_PERCENT` results have been removed. The application does not claim that it pushes directly to undocumented OpenAI, Anthropic, Stripe, Zapier, or AWS endpoints. Compatible MCP clients connect to the DSG backend's own `/mcp` endpoint.

## Architecture

```text
Android policy preset
  -> deterministic local QUBO candidate
  -> POST /v1/verify
  -> backend validates the exact rules, constraints, and 0/1 configuration
  -> Z3 SAT / UNSAT
  -> canonical input hash + proof hash + optional HMAC signature
  -> persistent audit-chain event
  -> actual response displayed in Android

MCP client
  -> Streamable HTTP /mcp
  -> verify_policy_solution or validate_policy_payload
  -> same Z3 verification service
```

## Run the backend

### Docker Compose

```bash
cp .env.example .env
# Replace PROOF_SIGNING_SECRET before a non-local deployment.
docker compose up --build
```

Endpoints:

- Health: `GET http://localhost:8000/health`
- Capabilities: `GET http://localhost:8000/v1/capabilities`
- Verification: `POST http://localhost:8000/v1/verify`
- Audit lookup: `GET http://localhost:8000/v1/audit/{event_hash}`
- MCP Streamable HTTP: `http://localhost:8000/mcp`

### Python development

```bash
cd backend
python -m venv .venv
. .venv/bin/activate
pip install -e '.[dev]'
export AUDIT_DB_PATH="$PWD/.data/audit.sqlite3"
pytest
uvicorn app.main:app --reload
```

## Connect the Android app

The default debug URL is `http://10.0.2.2:8000/`, which points from the Android emulator to the host computer.

Set a hosted HTTPS backend through a Gradle property or environment variable:

```bash
export ANDROID_DSG_BACKEND_BASE_URL=https://your-backend.example/
export ANDROID_DSG_BACKEND_API_KEY=development-only-key
```

Then build from Android Studio or a compatible Gradle installation.

> Do not ship a long-lived production API key inside an APK. Use an identity-aware gateway or short-lived user token for production.

## Verification request

The Android client sends:

- the complete rule definitions;
- typed constraints (`implication`, `equivalence`, `mutual_exclusion`, `at_least_one_of`, `min_active`, `max_cost`);
- the exact candidate configuration;
- local solver seed, iteration count, energy, and client solution hash.

The backend does not trust the Android `allConstraintsSatisfied` flag. It rebuilds the constraint system with Z3 and binds every server-side Boolean variable to the submitted candidate before returning `SAT` or `UNSAT`.

## MCP tools

The backend exposes:

- `verify_policy_solution`: Z3 verification plus durable proof/audit event;
- `validate_policy_payload`: Z3 validation without audit persistence;
- resource `dsg://capabilities`.

Example client registration:

```bash
claude mcp add --transport http dsg-policy http://localhost:8000/mcp
```

## Presets

The Android application includes illustrative policy models for:

- Thai PDPA;
- EU GDPR / EU AI governance controls;
- FinTech security controls;
- simplified Thai criminal-law decision examples.

These presets are technical demonstrations, not legal advice or legal determinations.

## Security and evidence boundaries

- SQLite chaining is tamper-evident, not independently immutable storage. For production, export or anchor chain heads to append-only external storage.
- HMAC signing proves possession of the server secret; asymmetric signatures and managed keys are preferable for third-party verification.
- The local QUBO solver is heuristic. Server-side Z3 verifies the submitted candidate; it does not prove global QUBO optimality.
- A deployed URL, production identity, rate limiting, monitoring, backups, and key management are operational requirements outside the source code itself.

## Tests

Backend CI installs the real `z3-solver` and MCP SDK, runs the verifier/audit/service tests, and imports the ASGI/MCP application. Android tests still require a working Android/Gradle toolchain.
