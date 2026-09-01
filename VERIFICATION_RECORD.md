---
description: Current deployment, health, readiness, MCP and claim-boundary evidence.
---

# Verification record

**Last documentation review:** 1 September 2026

This page separates **verified repository/deployment evidence** from configuration or historical integration status.

## Control Plane

### Verified from current repository

* Production provider is configured as `AZURE_APP_SERVICE`.
* Production URL is `https://dsg-control-plane.azurewebsites.net`.
* Health probe is `/api/health`.
* Production deployment is enabled through the governed Azure deployment adapter.
* Rollback target is a staging-slot reverse swap.
* Vercel and Render are explicitly marked as inactive production targets.
* Latest inspected main commit: `0680e134e7cdf518df922c3f72a2765f837bd7f1` — `fix(ci): point daily health monitoring at Azure (#1208)`.

### Claim boundary

The production target configuration itself says:

`BOUND_FAIL_CLOSED_LIVE_VERIFICATION_REQUIRED`

Therefore **FULL LIVE E2E PASS is not asserted here** without a current green governed production workflow plus live runtime/database evidence.

### Legacy integration noise

GitHub commit status for the latest inspected commit still contains failures/pending states from Vercel and Railway integrations. Those statuses are not treated as Control Plane production authority because the current repository configuration binds production to Azure. They should be removed or disabled separately to prevent operational confusion.

## Cinema Proof Agent

Repository evidence dated **28 August 2026** records:

* production deploy GitHub Actions run `33189890939` — PASS
* external production MCP proof run `33198810484` — PASS
* Cinema endpoint: `https://dsg-cinema-production.nicetree-a005fe99.westus3.azurecontainerapps.io`
* MCP endpoint: `/api/v1/mcp`
* native Z3 verifier endpoint: `https://dsg-z3-verifier-production.nicetree-a005fe99.westus3.azurecontainerapps.io`
* direct Z3 proof and Cinema proof matched for the recorded production proof
* replay match recorded as true

These are dated release proofs, not a guarantee that every endpoint remains healthy at the moment this page is read.

## AGI Simulation

Verified repository policy:

* Azure is the production authority.
* Simulation may search, score, reject and propose candidates.
* Simulation cannot self-promote.
* Cinema performs independent raw-evidence verification.
* Control Plane is the canonical promotion authority.
* Unresolved Azure Key Vault references are expected to fail closed.

## DSG ONE V1

The current README declares Azure-only production authority and explicitly states that the current live hostname must be resolved from a governed deployment receipt rather than asserted without evidence.

Its older test/evidence section is dated May 2026 and should not be interpreted as proof of September 2026 production state.

## Verification policy

Use this hierarchy when making claims:

```
current live runtime + exact deployment identity + persisted evidence
        >
current CI / deployment evidence
        >
repository configuration
        >
historical documentation
```

If evidence is missing, report the state as `UNVERIFIED`, `REVIEW`, `BLOCKED`, or otherwise according to the active policy. Do not upgrade missing evidence into success.
