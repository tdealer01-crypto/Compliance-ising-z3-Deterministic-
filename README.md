---
description: >-
  Verified deployment evidence, limits, claim boundaries, and next proof
  requirements.
---

# 🛡️ DSG QUBO & Ising Solver — Evidence-First Status

> **Overall status: REVIEW.** A current Render deployment is verified, but deployment evidence does not prove that every feature, external integration, benchmark, legal mapping, or certification claim is valid.

_Last verified: 2026-08-13 UTC_

## User result

Use this page to answer five questions:

1. **What is live?** — the currently verified Render deployment.
2. **What was tested?** — only results tied to reproducible logs and a specific commit.
3. **What is still uncertain?** — items marked REVIEW or UNVERIFIED.
4. **What blocks publication claims?** — failed security checks, missing proof bundles, and missing issuer records.
5. **What happens next?** — collect the required evidence, rerun checks, then update the status.

## Current evidence matrix

| Area                       | Status                                | Verified evidence                                                                                                                              | Boundary                                                                                                                                                 |
| -------------------------- | ------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| GitHub PR #1093            | **PASS**                              | Merged into `main` on 2026-08-13 01:16:43 UTC. Merge commit `69c6204e04363ea9a5c4f20721c2757907180337`.                                        | Proves merge state, not runtime behavior.                                                                                                                |
| Render deployment          | **PASS — deployment only**            | Deploy `dep-d9uhm27qj5pc73fk4fgg` reached `live` on 2026-08-13 01:20:19 UTC from commit `69c6204e04363ea9a5c4f20721c2757907180337`.            | Does not prove every endpoint, cron job, database write, Stripe action, or external integration works.                                                   |
| Revenue Autopilot code     | **REVIEW**                            | PR #1093 contains the implementation and fail-closed controls described in its change record.                                                  | Runtime scheduling, auth, persistence, and job outcomes still require post-deploy evidence. No customer charge was verified here.                        |
| QUBO / Ising result claims | **REVIEW**                            | Source and documentation describe deterministic candidate generation.                                                                          | Publish performance or quality numbers only with commit-linked raw logs and benchmark artifacts.                                                         |
| Z3 feasibility             | **REVIEW**                            | The design distinguishes candidate search from constraint checking.                                                                            | `SAT` proves only that the encoded candidate satisfies the encoded constraints. It does not prove global optimality, legal compliance, or certification. |
| Hash-chain example         | **BLOCK as evidence**                 | `e3b0c442…b855` is SHA-256 of empty input.                                                                                                     | Do not present it as a calculated event-block hash. Record canonical payload, predecessor hash, event hash, and verifier output.                         |
| GitBook MCP registration   | **REVIEW**                            | The Render MCP server is registered in GitBook.                                                                                                | Registration is not proof of an authenticated successful tool call.                                                                                      |
| Third-party APIs           | **UNVERIFIED**                        | No credential-safe response artifact was attached for OpenAI, Anthropic, Zapier, Stripe, AWS, Gemini/Vertex, Grafana, Firestore, or Cloud Run. | Do not show `200 OK` or “connected” without timestamped staging evidence and correlation IDs.                                                            |
| PR #1088                   | **BLOCK for merge/publication claim** | PR remains a draft and its CI Security Checks run failed at the Gitleaks step.                                                                 | Fix the findings, rerun CI, merge, and record the merge SHA before treating it as current.                                                               |
| Certification / compliance | **BLOCK**                             | No issuer record, audit report, scoped certificate, or legal review was verified.                                                              | Do not use “certified”, “compliant”, “audit-ready”, or “100% verified”. Use “internal verification draft” where appropriate.                             |

## Verified deployment evidence

* [GitHub PR #1093](https://github.com/tdealer01-crypto/tdealer01-crypto-dsg-control-plane/pull/1093)
* Merge commit: `69c6204e04363ea9a5c4f20721c2757907180337`
* Render service: `srv-d96pveupuehc73eohkag`
* Render deploy: `dep-d9uhm27qj5pc73fk4fgg`
* Deploy status: `live`
* Deploy finished: `2026-08-13T01:20:19.568323Z`
* Current deployment evidence on this page is **Render**, not Vercel.

## What still needs evidence

### 1. Post-deploy runtime checks

Record timestamped results for:

* health and readiness endpoints;
* authentication and authorization boundaries;
* Revenue Autopilot scheduler authentication;
* `revenue_autopilot_runs` persistence and idempotency;
* fail-closed behavior when secrets or dependencies are unavailable;
* no unintended customer charge, subscription mutation, or outbound email.

### 2. Reproducible test manifest

For every published test or benchmark result, attach:

* repository and commit SHA;
* branch and dependency lockfile;
* OS, runtime, JDK/Python/Node and solver versions;
* exact command and timestamp;
* unedited raw output and artifact hash.

### 3. Z3 proof bundle

For each preset, store:

* canonical input matrix and constraints;
* candidate and energy calculation;
* Z3 version, timeout, `SAT/UNSAT/UNKNOWN`;
* replay command and artifact hash;
* a statement that feasibility is not a proof of global optimum unless optimality is separately encoded and proved.

### 4. External integration checks

Verify endpoint specifications first. Use staging and read-only calls. Record status, timestamp, request correlation ID, and credential-safe response evidence. Any payment, deployment, outbound message, or other mutation requires explicit approval and an audit record.

## Claim policy

Until the missing artifacts are attached:

* do not publish unsupported benchmark values;
* do not present sample payloads as observed runtime results;
* do not claim external endpoints returned `200 OK`;
* do not claim certification or legal compliance;
* label unknown results **UNVERIFIED**;
* label incomplete but inspectable work **REVIEW**;
* label unsafe or claim-blocking gaps **BLOCK**;
* label only directly evidenced facts **PASS**.

## Meaning of this status

The user can rely on one current fact: **PR #1093 is merged and its exact commit is live on the recorded Render deploy**. Everything beyond that remains limited to the status and evidence stated above.
