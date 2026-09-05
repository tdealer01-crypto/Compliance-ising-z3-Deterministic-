---
description: Current deployment, health, readiness, MCP and claim-boundary evidence.
---

# Verification record

**Last documentation review:** 5 September 2026

This page separates **verified live execution evidence** from repository configuration and historical integration status.

## DSG Spacetime — Azure production provider stack

### Exact deployment identity

Verified production rollout:

```
Private production commit: 95cf915ed4593720cbfae02d65788726b1c1df87
Deploy workflow: 33968246995
Workflow conclusion: SUCCESS
Image digest: sha256:3cfb71591e5bae45bdbc50a6f51b4af5563a19495aaf267b9627e2efaae212f2
```

Recorded production endpoints:

```
Health: https://dsg-spacetime-prod.greenglacier-493f3f71.westus3.azurecontainerapps.io/health
MCP:    https://dsg-spacetime-prod.greenglacier-493f3f71.westus3.azurecontainerapps.io/mcp
```

The MCP endpoint is protected by the configured production authentication boundary.

### Executed provider proof

The deployment workflow did not stop at image build or health configuration. It executed the governed provider path and recorded:

```
AZURE_PROVIDER_ASTRA_PROPOSAL=PASS
AZURE_PROVIDER_ANTHROPIC_MCP=PASS
AZURE_PROVIDER_ASTRA_FINAL=PASS
DSG Spacetime Azure full runtime: VERIFIED
AZURE_PROVIDER_STACK=PASS
```

Executed path:

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

The same rollout also verified:

* unauthenticated MCP access was rejected as expected;
* governed compose/execute reached the authorized execution boundary;
* the source-free production image contained the Spacetime runtime plus the Astra and Anthropic MCP provider executables;
* Spacetime evidence remained valid after a new Azure Container Apps revision.

### Storage boundary

Spacetime evidence and Core Spin history are separate stores.

Spacetime owns the execution ledger fields such as:

```
plan_id
route_id
decision_hash
request_hash
result_hash
previous_hash
evidence_hash
```

Core Spin owns job/session history, workflow status, provider references and correlation references. It must not copy the Spacetime hash ledger into its own tables.

## Core Spin — production Supabase persistence

Production persistence verification job:

`08e4b8be-6b8d-4207-be67-fd8d66873f76`

Verified current row state:

```
status: COMPLETED
risk_level: LOW
completion_report_id: 10cf3e27-14d0-45bc-b197-fa19f084411b
source_run_id: 33966856203
provider_sequence: OpenAI → Anthropic → OpenAI
unified_read: references_only
spacetime_storage: separate
production_persistence_test: true
```

The job's runtime history contains three `SPACETIME_ROUTE_COMPLETED` events:

| Step | Provider  | Route                               | Spacetime evidence index |
| ---: | --------- | ----------------------------------- | -----------------------: |
|    0 | OpenAI    | `route.agent.astra.live`            |                        0 |
|    1 | Anthropic | `route.mcp.anthropic.protocol.live` |                        1 |
|    2 | OpenAI    | `route.agent.astra.live`            |                        2 |

Each event keeps the corresponding Spacetime evidence hash as a correlation reference. Core Spin does not duplicate Spacetime's `decision_hash`, `request_hash`, `result_hash` or `previous_hash` chain.

{% hint style="warning" %}
The Core Spin Supabase proof uses full-system run `33966856203`. The Azure production provider-stack deployment uses later run `33968246995`. Both are verified, but they are **not the same transaction**. A fresh post-deploy Core Spin → Azure Spacetime → Core Spin persistence loop remains a separate proof if required.
{% endhint %}

## Cinema Proof Agent

Dated Cinema production evidence remains a separate proof surface. Earlier verified records include Azure deployment, external production MCP proof, native Z3 verification, replay matching and rolling-revision persistence for the Cinema workflow.

Cinema production dashboard:

`https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io/dashboard`

These Cinema records must not be substituted for Spacetime provider-stack evidence, and vice versa.

## Control Plane

Verified repository policy:

* production authority is Azure App Service;
* production URL is `https://dsg-control-plane.azurewebsites.net`;
* health probe is `/api/health`;
* rollback target is a staging-slot reverse swap;
* Vercel and Render are not active production authorities.

Repository configuration alone is not sufficient to assert a new Control Plane deployment.

## Evidence hierarchy

Use this hierarchy for all present-moment claims:

```
current live runtime + exact deployment identity + persisted evidence
        >
current CI / deployment evidence
        >
repository configuration
        >
historical documentation
```

If evidence is missing, report the state as `UNVERIFIED`, `REVIEW`, `BLOCKED`, or the applicable fail-closed state. Do not upgrade missing evidence into success.
