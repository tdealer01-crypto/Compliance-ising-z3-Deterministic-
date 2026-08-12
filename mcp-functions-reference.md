# MCP Functions Reference

> Source: GitHub PR #1088, head `11ec564133f12e625a55018ce6cca75a3199114b` (12 Aug 2026). This published reference intentionally uses environment-variable placeholders for credentials. It does not claim that every example endpoint is live unless separately verified.

## Overview

The DSG ecosystem exposes Model Context Protocol (MCP) capabilities across the control plane and related runtimes. The reference groups the documented tools into core execution, Supabase, governance/spine, deployment, payments, Z3 verification, AIMO optimization, and incident-response capabilities.

### Tool index

**Core**

1. `get_proof` — retrieve audit proof/evidence for an execution
2. `list_app_builder_jobs` — list app-builder jobs
3. `create_app_builder_job` — create a governed app-builder job
4. `create_job_plan` — create an execution plan
5. `route_agent_command` — route a natural-language command
6. `get_autonomous_level` — inspect autonomy/capability level
7. `aimo_status` — inspect AIMO gateway status
8. `solve_aimo` — run AIMO optimization with replayable local path and Z3 feasibility evidence

**Supabase**

9. `query_database`
10. `update_records`
11. `list_tables`
12. `manage_rls_policies`
13. `execute_migrations`
14. `check_auth_session`

**Governance & Spine**

15. `spine_execute_governed`
16. `spine_get_execution_status`
17. `spine_commit_evidence`
18. `brain_analyze`

**Deployment**

19. `list_deployments`
20. `trigger_deploy`

**Stripe**

21. `stripe_create_product`
22. `stripe_create_price`
23. `stripe_create_payment_link`

**Z3**

24. `verify_proof`
25. `generate_proof`

**AIMO aliases**

26. `aimo_status`
27. `solve_aimo`

**Grafana / Incident response**

28. `search_dashboards`
29. `get_dashboard_summary`
30. `query_prometheus`
31. `query_loki_logs`
32. `list_incidents`
33. `get_incident`
34. `verify_recovery_plan`

***

## Core MCP functions

### `get_proof`

Generate or retrieve an immutable audit proof and evidence chain for a completed execution.

```json
{
  "execution_id": "exec_abc123",
  "include_trace": true,
  "include_lineage": true
}
```

Read-only. Approval is not required.

### `list_app_builder_jobs`

Lists jobs with filters and pagination.

```json
{
  "status": "completed",
  "limit": 20,
  "offset": 0,
  "order_by": "created_at.desc"
}
```

### `create_app_builder_job`

Creates a new governed app-builder job. Mutation approval is required.

```json
{
  "name": "New Feature Dashboard",
  "plan": {
    "type": "standard",
    "components": ["table", "chart", "form"],
    "database_tables": ["users", "transactions"]
  }
}
```

### `create_job_plan`

Creates a deterministic execution plan from requirements. This is an analysis/planning operation rather than an execution mutation.

### `route_agent_command`

Routes natural-language instructions to an execution handler and reports whether approval is needed.

### `get_autonomous_level`

Returns the configured autonomy level and capabilities for an agent/runtime context.

### `aimo_status`

Returns readiness information for the AIMO MCP gateway.

### `solve_aimo`

Executes Ising/QUBO optimization through the AIMO path.

```json
{
  "problem_type": "qubo",
  "matrix": [[1, 0.5], [0.5, 2]],
  "solver": "local_deterministic",
  "seed": 42,
  "constraints": ["hard_constraints_list"]
}
```

Expected audit fields include candidate/solution information, energy, hashes, seed, solver version, and Z3 version where available.

**Verification boundary**

* Same QUBO/linear terms, seed and algorithm version on the local deterministic path are intended to be replayable.
* Z3 evidence verifies hard-constraint feasibility of the pinned candidate.
* Z3 feasibility is **not** proof of global optimality.
* External/live Ising backends are not claimed to be deterministic merely because their returned result is normalized or hashed.

***

## Supabase tools

### `query_database`

Parameterized, RLS-protected reads.

### `update_records`

Governed record mutation. Approval is required for mutation execution.

### `list_tables`

Lists available tables/schema metadata.

### `manage_rls_policies`

Used for inspection/audit of Row-Level Security policy configuration.

### `execute_migrations`

Administrative migration application/verification. Requires elevated credentials and approval.

### `check_auth_session`

Checks JWT/session actor context and permissions.

***

## Governance & Spine

### `spine_execute_governed`

Runs an action through the governed execution pipeline and returns an execution identifier plus policy/proof metadata where supported.

### `spine_get_execution_status`

Reads current execution state, decision, reason and trace/lineage data when available.

### `spine_commit_evidence`

Commits evidence against an execution. Whether approval is required depends on the action context.

### `brain_analyze`

Advisory analysis/decision-support operation. Advisory output is not itself an execution approval.

***

## Deployment tools

### `list_deployments`

Reads deployment status from the configured deployment provider.

### `trigger_deploy`

Triggers a deployment mutation and therefore requires approval.

***

## Stripe payment tools

### `stripe_create_product`

Creates a Stripe product through a governed mutation.

### `stripe_create_price`

Creates a Stripe price for a product.

### `stripe_create_payment_link`

Creates a payment link for a price.

All three are mutations and should be gated by the configured approval policy.

***

## Z3 formal-verification tools

### `verify_proof`

Pins a candidate assignment and checks hard-constraint feasibility using Z3.

```json
{
  "qubo_hash": "sha256_qubo_abc",
  "solution": [1, 0],
  "solution_hash": "sha256_solution_def",
  "constraints": ["hard_constraint_1", "hard_constraint_2"],
  "timeout_ms": 5000
}
```

A SAT result means the pinned candidate satisfies the modeled hard constraints. It does not establish that the candidate is the globally optimal solution.

### `generate_proof`

Generates/pins a proof artifact for a policy decision or action candidate. This operation is governed and requires approval where configured.

***

## Grafana & incident-response tools

The Cinema Proof Agent documentation describes these capabilities for dashboard/search, Prometheus and Loki queries, incident lookup, and recovery-plan verification:

* `search_dashboards`
* `get_dashboard_summary`
* `query_prometheus`
* `query_loki_logs`
* `list_incidents`
* `get_incident`
* `verify_recovery_plan`

`verify_recovery_plan` is the governed verification step for a recovery action; a positive feasibility/authorization decision must still be interpreted within the configured policy and approval boundary.

***

## Authentication

Use a Bearer token supplied from a secret store or environment variable. Do not paste a real token into documentation or shell history.

```bash
export DSG_MCP_API_KEY='<your-key-from-secret-store>'
```

Then call a configured MCP endpoint:

```bash
curl -X POST "$DSG_MCP_ENDPOINT" \
  -H "Authorization: Bearer ${DSG_MCP_API_KEY}" \
  -H "Content-Type: application/json" \
  -d '{
    "jsonrpc": "2.0",
    "id": 1,
    "method": "spine_get_execution_status",
    "params": {"execution_id": "exec_xyz"}
  }'
```

This replaces the three literal `Authorization: Bearer dsg_mcp_xyz...` examples from the PR source, which were detected by the repository's `curl-auth-header` Gitleaks rule as secret-like placeholders.

***

## Error handling

Standard JSON-RPC errors documented by the PR include:

| Code     | Meaning              |
| -------- | -------------------- |
| `-32700` | Parse error          |
| `-32600` | Invalid request      |
| `-32601` | Method not found     |
| `-32602` | Invalid params       |
| `-32603` | Internal error       |
| `-32000` | Authentication error |
| `-32001` | Approval required    |
| `-32002` | Rate limit exceeded  |

Exact error behavior is runtime/version dependent; clients should not infer unsupported guarantees from example payloads.

***

## Formal proof boundaries

### Proven / independently checkable within the modeled scope

* Candidate assignment can be checked against hard constraints using Z3 SAT/UNSAT.
* Proof metadata can include input/candidate hashes and solver version for replay/audit.
* Energy can be recalculated from the original QUBO rather than trusting an external energy field.

### Not proven by feasibility alone

* Global optimality.
* Continued feasibility after constraints change.
* Determinism of an external solver backend.
* Convergence when a solver times out.

If verification is unsupported or incomplete, the result must not be represented as a verified PASS.

***

## Security guidance

* Store API keys in an environment variable or secret manager.
* Do not commit real credentials or credential-shaped examples.
* Enforce RLS and actor/workspace isolation for database access.
* Require approval for mutations and high-risk external actions.
* Record execution IDs, proof hashes, policy versions and evidence needed for replay/audit.

***

## Developer integration checklist

* Install/use the MCP SDK version required by the target runtime.
* Configure the MCP endpoint and credentials in a secret store.
* Handle approval-required responses explicitly.
* Record execution IDs for audit correlation.
* Implement bounded retry/backoff for transient errors.
* Treat rate-limit headers as runtime data rather than fixed documentation constants.
* Re-verify proof hashes/evidence for critical operations when supported.
* Test against a non-production environment before enabling mutations.

***

## Published Docs MCP endpoint

The published DSG Docs site exposes its GitBook read-only MCP endpoint at:

```
https://dsg-3.gitbook.io/dsg-docs/~gitbook/mcp
```

This endpoint lets compatible AI clients read the **published** DSG documentation. Draft change requests are not exposed until merged/published.

***

## Source and verification record

* GitHub repository: `tdealer01-crypto/tdealer01-crypto-dsg-control-plane`
* Source PR: `#1088`
* Source head: `11ec564133f12e625a55018ce6cca75a3199114b`
* Source file: `docs/mcp/MCP_FUNCTIONS_REFERENCE.md`
* CCVS Evidence Tests observed for the PR: **4,719 tests, 0 failures, 0 errors**
* Repository CI Security check observed on that head: **failed**, because the PR-history Gitleaks scan reported **3 secret-like findings**. The published examples above remove the three literal Bearer placeholders responsible for the matching pattern; this page does not claim that PR #1088 itself is all-green until GitHub CI confirms it.

Last reviewed: 2026-08-12
