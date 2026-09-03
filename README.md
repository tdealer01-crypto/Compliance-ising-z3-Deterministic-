---
description: >-
  Current DSG ONE product map, Azure production authority, Cinema runtime entry
  point, and evidence-first claim boundaries.
---

# 🛡️ DSG Docs — Governed AI Execution

**Canonical product domain:** https://www.dsg.pics

DSG ONE is a governance, execution, and evidence layer for AI agents, MCP clients, API workflows, CI/CD automation, and autonomous runtimes.

The core rule is simple: **approved work should execute; out-of-plan work should stop; missing capabilities should remain waiting rather than being misclassified; and every important result should carry evidence.**

## Start here

For the current customer-facing governed execution runtime, use **DSG Cinema Proof Agent**:

https://dsg-3.gitbook.io/dsg-docs/cinema-proof-agent/

Primary production dashboard:

https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io/dashboard

The normal flow is:

```
User / Agent
    ↓
Plan proposal
    ↓
Exact plan hash
    ↓
User approval when required
    ↓
DSG preflight
    ├─ ALLOW
    ├─ WAITING_PERMISSION
    └─ BLOCK
    ↓
Approved execution
    ↓
Evidence + audit + verification
```

## Product map

### Cinema Proof Agent

Cinema is the current customer-facing deterministic execution and evidence runtime. It combines:

* Agent Chat
* exact-plan approval
* Shared Browser
* paired-Agent Remote MCP
* Universal Runtime
* native Z3 verification where required
* evidence, replay, and durable audit
* five live operator views: ACTION, PLAN ALIGNMENT, PERMISSION, EVIDENCE, EXECUTION / AUDIT

Current repository evidence dated **3 September 2026** records durable Agent pairing across replicas and a real rolling Azure Container Apps revision, with the pre-roll pairing token successfully resolving on the new revision. Treat that as dated production proof for that workflow, not as a permanent uptime claim.

### Control Plane

The Control Plane is the broader governance and promotion authority for existing agents, MCP servers, APIs, and automated workflows.

**Authoritative production platform:** Azure App Service

* Production URL: `https://dsg-control-plane.azurewebsites.net`
* Health probe: `GET /api/health`
* Deployment path: exact commit → container image → Azure Container Registry → staging slot → runtime/evidence verification → production promotion
* Rollback: staging-slot reverse swap
* Vercel and Render are not active Control Plane production targets

The configured target remains `BOUND_FAIL_CLOSED_LIVE_VERIFICATION_REQUIRED`: repository configuration does not by itself prove the latest production deployment passed.

### DSG ONE V1

DSG ONE V1 defines the plan-authorized execution contract used by Cinema and related integrations. The important semantics are:

| State                | Meaning                                                                                                                             |
| -------------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| `ALLOW`              | Exact action is inside the approved plan and required capability is ready.                                                          |
| `WAITING_PERMISSION` | Action remains inside the approved plan, but a server-side capability, credential, tool, or infrastructure dependency is not ready. |
| `BLOCK`              | Action is outside the approved boundary, plan identity does not match, or action/target/parameters differ from the approved step.   |

A valid credential does not create out-of-plan authority.

### AGI Simulation

Simulation may search, score, reject, and propose candidates. It does not authorize its own promotion.

```
Simulation / candidate
        ↓
Cinema independent verification
        ↓
Control Plane promotion authority
        ↓
Governed merge / deployment
```

## Observe and Enforce

### OBSERVE

DSG records plan alignment, permission state, evidence, and execution outcome without automatically blocking the customer's existing runtime.

### ENFORCE

DSG makes the governance decision effective at the execution boundary. Approved work proceeds; out-of-plan work may be blocked; missing capabilities remain waiting until resolved.

Changing mode changes execution effect, not the underlying governance classification.

## What the operator should see

Every governed execution should answer five questions:

1. **ACTION** — What is the Agent attempting?
2. **PLAN ALIGNMENT** — Is it inside the approved plan?
3. **PERMISSION** — Is the required authority/capability available?
4. **EVIDENCE** — What proves the result?
5. **EXECUTION / AUDIT** — What actually happened and what was recorded?

## Evidence-first claim rule

Do not claim `production-ready`, `FULL LIVE E2E PASS`, successful deployment, certified compliance, external solver execution, or verified proof unless current evidence proves that specific claim.

Use this evidence hierarchy:

```
current live runtime + exact deployment identity + persisted evidence
        >
current CI / deployment evidence
        >
repository configuration
        >
historical documentation
```

Configuration is not execution evidence. Source code is not production evidence. A historical successful workflow does not prove a different current execution.

## Documentation navigation

* **Cinema Proof Agent** — current customer runtime and production evidence
* **Control Plane** — Azure governance and promotion authority
* **DSG ONE V1** — plan-authorized execution contract
* **DSG API Reference** — Control Plane API contract
* **AGI Simulation** — deterministic candidate-generation and simulation layer

***

**DSG ONE — govern the action, preserve the evidence, verify the result.**
