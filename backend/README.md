# DSG Compliance Backend

This service performs the work that must not be simulated in the Android client:

- server-side Z3 verification of the exact candidate configuration;
- SHA-256 proof generation over a canonical request/result payload;
- optional HMAC proof signing;
- SQLite-backed tamper-evident audit chaining;
- an MCP Streamable HTTP server at `/mcp`.

## Run locally

```bash
cd backend
python -m venv .venv
. .venv/bin/activate
pip install -e '.[dev]'
export AUDIT_DB_PATH="$PWD/.data/audit.sqlite3"
uvicorn app.main:app --reload
```

The REST verification endpoint is `POST http://localhost:8000/v1/verify`.
The MCP endpoint is `http://localhost:8000/mcp`.

Optional environment variables:

- `DSG_API_KEY`: protects REST endpoints with `X-API-Key` or `Authorization: Bearer`.
- `PROOF_SIGNING_SECRET`: enables HMAC-SHA256 signatures over proof hashes.
- `AUDIT_DB_PATH`: persistent SQLite audit database path.

Do not embed a long-lived production API key in an Android APK. Put production authentication at an identity-aware gateway or issue short-lived user tokens.
