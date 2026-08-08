# Verification record

This file separates checks actually run while assembling the bundle from checks that require CI/cloud credentials.

## Run in the assembly environment

- Source archive was extracted and inspected.
- Python source syntax was checked with `python -m compileall -q backend/app backend/tests`: **PASS**.
- Final dependency-light tests were run with `PYTHONPATH=backend pytest -q backend/tests/test_audit.py backend/tests/test_auth.py`: **3 passed**.
- Input validation checks from the supplied backend accepted binary configurations and rejected non-binary values as expected.
- Runtime source scan found no earlier hard-coded provider `ACCEPTED_200` / `VERIFIED_100_PERCENT` simulation and no OpenAI/Anthropic/LangChain/CrewAI runtime imports: **PASS**.
- Obvious API-token pattern scan over source/config (excluding `.env.example` placeholders): **PASS**.
- `backend/pyproject.toml` was parsed with Python `tomllib`: **PASS**.

## Not verified in the assembly environment

The local package mirror did not provide the complete external dependencies (`z3-solver`, MCP SDK, Google ADK) required to execute the complete suite here. Therefore this document does **not** claim:

- full `pytest` PASS for the final bundle;
- successful Gemini/Vertex AI runtime calls;
- successful Grafana Cloud MCP runtime calls;
- successful Firestore runtime transactions;
- successful Cloud Run deployment.

The GitHub Actions workflow is intended to provide dependency-installed CI evidence after the repository exists. External service checks still require real credentials and a deployment.
