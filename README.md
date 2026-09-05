---
description: >-
  Current DSG ONE product map, Azure production authority, Cinema runtime entry
  point, and evidence-first claim boundaries.
---

# 🛡️ DSG Docs — Governed AI Execution

**Canonical product domain:** https://www.dsg.pics

DSG ONE is a governance, execution, and evidence layer for AI agents, MCP clients, API workflows, CI/CD automation, and autonomous runtimes.

The core rule is simple: **approved work should execute through the authorized boundary; out-of-plan work should stop; missing capabilities should remain waiting rather than being misclassified; and every important result should carry evidence.**

## Current production architecture

```
User
  ↓
Agent + Core Spin
  ↓
DSG Spacetime
  ↓ authorized Route
Agent provider / MCP provider / customer adapter
  ↓
DSG Spacetime evidence
  ↓
Agent + Core Spin
  ↓
User
```

**Core Spin** owns job/session history, workflow state, provider references, usage and correlation references. **DSG Spacetime** independently owns plan/Route authorization, execution receipts and its tamper-evident evidence chain. The UI may join both at read time, but the stores are not merged.

There is no separate outer gate wrapped around Spacetime: **Spacetime itself is the authorization and execution boundary.**

## Verified Spacetime production stack — 5 September 2026

The private production runtime was deployed to **Azure Container Apps** from commit `95cf915ed4593720cbfae02d65788726b1c1df87`.

```
Deploy workflow: 33968246995
Result: SUCCESS
Image digest: sha256:3cfb71591e5bae45bdbc50a6f51b4af5563a19495aaf267b9627e2efaae212f2
```

The deployment proof executed:

```
Spacetime
  ↓
GPT-6 Astra proposal
  ↓
Spacetime
  ↓
Claude Sonnet 5 → Remote MCP
  ↓
Spacetime evidence
  ↓
GPT-6 Astra final
```

Verified markers:

* `AZURE_PROVIDER_ASTRA_PROPOSAL=PASS`
* `AZURE_PROVIDER_ANTHROPIC_MCP=PASS`
* `AZURE_PROVIDER_ASTRA_FINAL=PASS`
* `AZURE_PROVIDER_STACK=PASS`
* unauthenticated MCP requests fail closed
* Spacetime evidence remained valid across a new Azure Container Apps revision

Production endpoints recorded by the deployment workflow:

* Health: `https://dsg-spacetime-prod.greenglacier-493f3f71.westus3.azurecontainerapps.io/health`
* MCP: `https://dsg-spacetime-prod.greenglacier-493f3f71.westus3.azurecontainerapps.io/mcp`

The MCP endpoint requires the configured production authentication boundary. A public URL does not imply anonymous execution capability.

## Core Spin production persistence

Core Spin production persistence was verified separately in Supabase with job:

`08e4b8be-6b8d-4207-be67-fd8d66873f76`

Current verified properties:

* status: `COMPLETED`
* source full-system run: `33966856203`
* provider sequence: `OpenAI → Anthropic → OpenAI`
* unified read mode: `references_only`
* Spacetime storage: `separate`
* three `SPACETIME_ROUTE_COMPLETED` events reference Spacetime evidence indexes `0`, `1`, and `2`

Core Spin does **not** duplicate Spacetime's decision/request/result/previous-hash ledger. It keeps correlation references so an operator view can combine both sources without collapsing their ownership boundaries.

{% hint style="warning" %}
**Claim boundary:** the Core Spin production persistence proof and the later Azure provider-stack deployment proof are both verified, but they are different executed runs. Do not describe them as a fresh post-deploy Core Spin → Azure Spacetime → Core Spin transaction until that exact combined run is executed and recorded.
{% endhint %}

## Product map

### DSG Spacetime

Spacetime is the governed execution boundary for plan-authorized actions. It verifies plan identity, plan hash, registered Route, entitlement, agent binding and required approval before calling the authorized adapter/provider. Successful execution produces evidence through the Spacetime chain.

It supports a swappable-agent model: the reasoning provider is not the governance authority. GPT/Astra, Claude, Gemini, customer agents and other approved providers can sit behind the same boundary when the required Route/adapter exists.

Provider-native MCP or hosted tools may be used as execution transports, but they do not gain authority to bypass Spacetime.

### Cinema Proof Agent

Cinema is the customer-facing deterministic execution and evidence runtime with Agent Chat, exact-plan approval, Shared Browser, paired-Agent Remote MCP, Universal Runtime, Z3 verification where required, evidence, replay and durable audit.

Production dashboard:

https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io/dashboard

Cinema production evidence is separate from the Spacetime provider-stack proof above.

### Control Plane

The Control Plane is the broader governance and promotion authority for existing agents, MCP servers, APIs and automated workflows.

**Authoritative production platform:** Azure App Service

* Production URL: `https://dsg-control-plane.azurewebsites.net`
* Health probe: `GET /api/health`
* Deployment path: exact commit → container image → Azure Container Registry → staging slot → runtime/evidence verification → production promotion
* Rollback: staging-slot reverse swap
* Vercel and Render are not active Control Plane production targets

### DSG ONE V1

DSG ONE V1 defines the plan-authorized execution contract used by DSG runtimes.

| State                | Meaning                                                                                                                           |
| -------------------- | --------------------------------------------------------------------------------------------------------------------------------- |
| `ALLOW`              | Exact action is inside the approved plan and the required capability is ready.                                                    |
| `WAITING_PERMISSION` | Action remains inside the approved plan, but a capability, credential, tool or infrastructure dependency is not ready.            |
| `BLOCK`              | Action is outside the approved boundary, plan identity does not match, or action/target/parameters differ from the approved step. |

A valid credential does not create out-of-plan authority.

## What the operator should see

Every governed execution should answer five questions:

1. **ACTION** — What is the Agent attempting?
2. **PLAN ALIGNMENT** — Is it inside the approved plan?
3. **PERMISSION** — Is the required authority/capability available?
4. **EVIDENCE** — What proves the result?
5. **EXECUTION / AUDIT** — What actually happened and what was recorded?

## Evidence-first claim rule

Use this hierarchy when making production claims:

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

* **Verification record** — exact current deployment and persistence evidence
* **Cinema Proof Agent** — customer runtime and Cinema-specific production evidence
* **Control Plane** — Azure governance and promotion authority
* **DSG ONE V1** — plan-authorized execution contract
* **DSG API Reference** — Control Plane API contract
* **AGI Simulation** — deterministic candidate-generation and simulation layer

***

**DSG ONE — govern the action, preserve the evidence, verify the result.**
