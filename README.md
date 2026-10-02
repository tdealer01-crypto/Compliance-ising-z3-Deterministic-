---
description: >-
  Current DSG ONE product map, verified AWS production authority, current
  Spacetime/Cinema runtime state, and evidence-first claim boundaries.
---

# 🛡️ DSG Docs — Governed AI Execution

Page "DSG Docs — Governed AI Execution" — id: dvqXZLaiaujd1LXgzfVV, path: /readme

***

### description: >- Current DSG ONE product map, verified AWS production authority, current Spacetime/Cinema runtime state, and evidence-first claim boundaries.

## DSG Docs — Governed AI Execution

**Canonical product domain:** https://www.dsg.pics

DSG ONE is a governance, execution, orchestration, and evidence system for AI agents, MCP clients, API workflows, CI/CD automation, and autonomous runtimes.

The operating rule is: **approved work executes only through the authorized boundary; out-of-plan work stops; missing capabilities remain waiting; and production claims require runtime evidence.**

#### Current production truth — 2 October 2026

| Surface                                      | Verified current state                                                                                                |
| -------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| AWS host                                     | EC2 `i-01a2ee90890a558c3`, `t3.small`, `us-east-1a`, running; instance/system/EBS reachability passed                 |
| SSM                                          | Managed instance is Online                                                                                            |
| DSG ONE V1                                   | `aws-dsg-one-v1-1` healthy; `GET /api/agent/status` HTTP 200                                                          |
| DSG ONE source                               | `ea70fbecdde020b0a4d4e44180798418af209f27`                                                                            |
| DSG ONE image                                | `sha256:2b79455abf1a4d8b29abccb384e55fe83f82d8b44a117fe86133a7024dfa06ad`                                             |
| Automation engine                            | Microsoft Agent Framework `1.18.0`; process, DB, automation DB, deployment identity and automation engine checks true |
| Spacetime                                    | `aws-spacetime-1` healthy; `/health` HTTP 200                                                                         |
| Spacetime image                              | `sha256:5087dcc08956e0618036aa7889f9dbb875f74dfcca661b1b354249b153099e35`                                             |
| Cinema                                       | `aws-cinema-1` healthy; `/health` returns `status=ready`, `backend=ready`                                             |
| Cinema image                                 | `sha256:b876f7bb86a8eb86a4c732a07a706c217993092da4054bc1fac205c4ca78b3c3`                                             |
| Governed RDC acceptance                      | PASS in governed activation run `36991572295`                                                                         |
| Authenticated Workroom user E2E              | OPEN                                                                                                                  |
| Promoted-source public governed MCP closeout | PARTIAL / OPEN                                                                                                        |
| Promoted-source XR state smoke               | OPEN                                                                                                                  |
| Whole-system live runtime release            | `PENDING_REVERIFY`; do not upgrade scoped PASS results into whole-system PASS                                         |

The generic paths `/api/health`, `/api/readiness`, and `/api/v1/status` on the DSG ONE port returned HTTP 404 during the same verification. The verified DSG ONE status endpoint is `/api/agent/status`.

#### Current production architecture

```
User / ChatGPT / Access Hub / CLI
        ↓
DSG ONE V1
  product surface + Core Spin orchestration
        ↓
Microsoft Agent Framework 1.18.0
        ↓
DSG Spacetime
  plan / Route / policy / permission / approval authority
        ↓
Governed provider
  BrowserOS / Cinema / RDC / API / approved model adapter
        ↓
Evidence + verification
        ↓
Core Spin next turn / user-visible result
```

There is no separate production component named `EVO`. The autonomous loop is the composed DSG ONE/Core Spin + durable Workroom + Spacetime + evidence-feedback loop.

#### Authority boundaries

* **DSG Spacetime** owns governed Route binding, plan/payload alignment, permission, approval, provider invocation and evidence binding.
* **DSG ONE V1 / Core Spin** owns product workflow and orchestration; it must not bypass Spacetime for external side effects.
* **Cinema** is the verification/evidence/replay and BrowserOS execution surface under governed authorization.
* **RDC** is a governed provider. High-risk write/shell paths require exact payload binding and approval.
* **Brain / Agent v0 / simulation / repair** propose, rank or synthesize. A model route is not execution authority.

#### User-facing product map

**DSG Spacetime**

Production MCP: `https://aws.dsg.pics/mcp`

Spacetime is the single governed execution boundary. The reasoning model is replaceable; authorization is not delegated to the model.

**Cinema / BrowserOS**

Cinema is running on the AWS production host and reports ready/backend ready. Customer interaction should enter through the current DSG surfaces rather than the retired Azure Container Apps dashboard URL.

**Workroom**

Customer entry: `https://dsg.pics/dsg/workroom`

The unauthenticated boundary is expected to redirect to login. A complete signed-in workspace → Agent Chat → Command Center → identity/memory/evidence flow is still an open acceptance gate.

**DSG ONE V1**

The production status probe verified on the current AWS runtime is:

`GET /api/agent/status`

Decision semantics remain `ALLOW`, `WAITING_PERMISSION`, and `BLOCK`, followed by execution evidence.

#### Historical provider evidence

Azure Container Apps, Azure App Service, and Render deployment records remain valid **only for their original timestamps and source revisions**. They are not current production authority and must not be used to describe the 2 October 2026 runtime.

#### Evidence-first claim rule

```
current live runtime + exact deployment identity + persisted evidence
        >
current CI / deployment evidence
        >
repository configuration
        >
historical documentation
```

Configuration is not execution evidence. A route being registered does not prove its external provider is online. A scoped PASS does not automatically promote the whole system to PASS.

#### Documentation synchronization state

**Verified current state — 2 October 2026:**

* Full-site Git Sync is **not yet enabled** for the DSG Docs site.
* The `Docs` space is connected to GitHub repo `tdealer01-crypto/Compliance-ising-z3-Deterministic-` and its latest export completed successfully after this documentation update.
* The other site spaces are not currently Git-synced.
* Therefore the site does **not yet** have one repository acting as the canonical source for all Architecture / Runtime / Spacetime / Cinema / Evidence / Deployment documentation.

**Target state:** configure GitBook Full-site Git Sync to one explicitly selected documentation repository, then treat GitBook change requests + that repository as the controlled documentation workflow. Do not claim this target state until the site-level configuration is verified.

#### Documentation navigation

* **Verification record** — current runtime evidence and remaining gates
* **Cinema Proof Agent** — Cinema/BrowserOS execution and evidence surface
* **Control Plane** — current AWS governance/control-plane state
* **DSG ONE V1** — plan-authorized execution contract
* **DSG API Reference** — API contract; current production claims must be AWS-bound
* **AGI Simulation** — proposal/candidate-generation layer

**DSG ONE — govern the action, preserve the evidence, verify the result.**
