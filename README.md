---
description: >-
  Verified Render runtime evidence, limits, claim boundaries and next proof
  requirement.
---

# 🛡️ DSG Docs — Governed AI Execution

DSG is a governance and evidence layer for AI agents, MCP servers, API workflows, CI/CD automation, and autonomous runtimes.

The goal is simple: **let approved work execute, block unsupported or out-of-plan actions, and preserve evidence of what actually happened.**

## What DSG does

```
Your Agent / MCP / Automation
            │
            ▼
     DSG Control Plane
            │
     ┌──────┴──────┐
     │             │
  OBSERVE        ENFORCE
     │             │
 Record only    Gate action
     │             │
     └──────┬──────┘
            ▼
       Existing System
```

### OBSERVE

Use DSG as an evidence and audit layer without blocking the existing workflow. DSG records the action, plan alignment, permission state, evidence, and execution result.

### ENFORCE

Use DSG as a pre-execution governance gate.

```
PASS               → execute
BLOCKED            → stop; action is outside the approved plan/policy
WAITING_PERMISSION → required authority is missing
UNVERIFIED         → required evidence is missing
FAILED             → execution or verification failed
```

DSG must not block an action merely because governance exists. If an action is covered by the user-approved plan and the required permissions/constraints are satisfied, it should proceed.

## What the operator sees

The runtime experience is organized around five operational surfaces:

1. **ACTION** — what the agent is attempting.
2. **PLAN ALIGNMENT** — whether it belongs to the approved plan.
3. **PERMISSION** — whether the executor has the required authority.
4. **EVIDENCE** — what proves the result.
5. **EXECUTION / AUDIT** — what actually happened and what was recorded.

The operator should be able to answer: **What was requested? Was it approved? Was it permitted? What executed? What proves the result? What must happen next?**

## Current production authority

As of **1 September 2026**, the Control Plane repository binds production to **Azure App Service**.

* Control Plane production URL: `https://dsg-control-plane.azurewebsites.net`
* Health probe: `GET /api/health`
* Deployment model: exact commit → container image → Azure Container Registry → staging slot → runtime/evidence verification → production promotion
* Rollback: staging-slot reverse swap
* Vercel and Render are **not active DSG production targets** for the Control Plane.

The production target is configured as `BOUND_FAIL_CLOSED_LIVE_VERIFICATION_REQUIRED`. This means configuration alone is **not** evidence that the latest production deployment passed.

## DSG Cinema Proof Agent

Cinema is the deterministic verification/governance runtime used for proof-oriented execution.

Current repository evidence dated **28 August 2026** records:

* Cinema production: `https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io`
* MCP: `https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io/api/v1/mcp`
* Native Z3 verifier: `https://dsg-z3-verifier-production.nicetree-a005fe99.westus3.azurecontainerapps.io`
* MCP transport: HTTP JSON-RPC 2.0, protocol `2025-06-18`
* Production deployment proof and external MCP exact-select proof were recorded as PASS in GitHub Actions evidence for that dated release.

Cinema separates caller intent from verifier judgment. Callers submit plans, actions, execution facts and evidence; DSG computes the governance/proof result. Caller-supplied verdicts are not accepted as proof.

## DSG AGI Simulation

The simulation system performs deterministic search, simulation, candidate admission and evidence generation. It **cannot authorize its own promotion**.

```
Simulation / candidate
        ↓
Cinema independent verification
        ↓
Control Plane promotion authority
        ↓
Governed merge / deployment
```

Production authority for the simulation stack is Azure. Runtime secrets are expected to be resolved through Azure Key Vault / Managed Identity; unresolved secret references are fail-closed.

## Evidence-first rule

Do not claim `production-ready`, `FULL LIVE E2E PASS`, certified compliance, successful deployment, verified proof, or external solver execution unless current evidence proves that specific claim.

Configuration is not execution evidence. Source code is not production evidence. A successful command is not necessarily proof of the resulting external state.

Useful evidence includes:

* tests and CI output
* deployment status
* runtime API responses
* database records
* commit SHA / image digest
* audit records
* proof receipts
* replay verification

## Current documentation note

This GitBook space was previously synced from the `Compliance-ising-z3-Deterministic-` repository and still contains legacy QUBO/Ising-oriented pages. This overview has been updated to represent the broader DSG product and current Azure governance model. Legacy pages should be treated according to their own dated evidence until they are migrated or replaced.

***

**DSG Control Plane — govern the action, preserve the evidence, verify the result.**
