---
description: >-
  Verified Render runtime evidence, limits, claim boundaries and next proof
  requirement.
---

# 🛡️ DSG QUBO & Ising Solver — Evidence-First Status

> **Overall status: REVIEW.** The current Render deployment is reachable and its health, readiness and MCP discovery endpoints passed direct checks. This does not prove every solver, integration, payment or external action.

_Last verified: 2026-08-13 UTC_

## What users can use now

| Surface                | Status            | Where to look                                             | What the result means                                                        |
| ---------------------- | ----------------- | --------------------------------------------------------- | ---------------------------------------------------------------------------- |
| Render service         | **PASS**          | `https://tdealer01-crypto-dsg-control-plane.onrender.com` | Current production provider and deployed revision.                           |
| Health                 | **PASS**          | `/api/health`                                             | Service, core and database checks returned healthy for the recorded request. |
| Readiness              | **PASS**          | `/api/readiness`                                          | Seven readiness checks returned `ok=true`.                                   |
| MCP discovery          | **PASS**          | `/api/mcp` or JSON-RPC `tools/list`                       | Live registry v1.2.0 advertised 65 tools.                                    |
| Authenticated MCP call | **REVIEW**        | JSON-RPC `tools/call`                                     | Not verified in this run because no existing credential was available.       |
| Anonymous MCP call     | **PASS — denied** | `dsg.system.status`                                       | Returned 401, confirming the tested request was not allowed.                 |

## Current source and deployment

* PR #1093: merged
* Merge commit: `69c6204e04363ea9a5c4f20721c2757907180337`
* Render deploy: `dep-d9uhm27qj5pc73fk4fgg`
* Deploy status: `live`
* Deploy completed: `2026-08-13T01:20:19.568323Z`
* Active deployment evidence on this page is **Render**, not Vercel.

## Claim boundaries

| Claim area                      | Status              | Rule                                                                                                               |
| ------------------------------- | ------------------- | ------------------------------------------------------------------------------------------------------------------ |
| QUBO/Ising candidate generation | **REVIEW**          | Publish performance only with commit-linked raw benchmark artifacts.                                               |
| Z3 feasibility                  | **REVIEW**          | `SAT` proves the encoded candidate satisfies encoded constraints, not global optimality or legal correctness.      |
| Revenue Autopilot               | **REVIEW**          | Code and deployment exist; scheduler runs, persistence and business outcomes require post-deploy receipts.         |
| Third-party APIs                | **UNVERIFIED**      | No `200 OK` or connected claim without credential-safe response and correlation ID.                                |
| PR #1088                        | **BLOCK for merge** | Draft, stale relative to `main`, security workflow failed and interface names do not match the live v1.2 registry. |
| Certification/compliance        | **BLOCK**           | No certification/compliance claim without issuer, scope, audit and legal evidence.                                 |

## Next user-visible milestone

One authenticated read-only MCP status call must return a structured result plus audit/execution evidence. Until then, use the registry to discover tools but do not present tool execution as verified.
