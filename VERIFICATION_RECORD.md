---
description: >-
  Current AWS deployment, health, image binding, MCP and claim-boundary
  evidence.
---

# Verification record

**Last runtime verification:** 2 October 2026

This page separates current runtime evidence from historical deployment records and repository configuration.

### AWS production host

* EC2: `i-01a2ee90890a558c3`
* Region/AZ: `us-east-1 / us-east-1a`
* Instance type: `t3.small`
* State: `running`
* EC2 instance/system/EBS reachability: `ok`
* SSM managed instance: `Online`

### Live containers and immutable bindings

| Component  | Runtime state     | Verified image                                                            |
| ---------- | ----------------- | ------------------------------------------------------------------------- |
| DSG ONE V1 | running / healthy | `sha256:2b79455abf1a4d8b29abccb384e55fe83f82d8b44a117fe86133a7024dfa06ad` |
| Spacetime  | running / healthy | `sha256:5087dcc08956e0618036aa7889f9dbb875f74dfcca661b1b354249b153099e35` |
| Cinema     | running / healthy | `sha256:b876f7bb86a8eb86a4c732a07a706c217993092da4054bc1fac205c4ca78b3c3` |

The current Spacetime container was created from governed stage `36991572295-1`.

### DSG ONE status proof

`GET http://127.0.0.1:8080/api/agent/status` returned HTTP 200 with:

* `ok=true`
* repo `dsg-one-v1`
* source `ea70fbecdde020b0a4d4e44180798418af209f27`
* image digest `sha256:2b79455abf1a4d8b29abccb384e55fe83f82d8b44a117fe86133a7024dfa06ad`
* `sourceBound=true`
* `digestBound=true`
* DB, automation DB, process and automation-engine checks true
* Microsoft Agent Framework `1.18.0`

During the same runtime check, `/api/health`, `/api/readiness`, and `/api/v1/status` on the DSG ONE port returned HTTP 404. Do not document those paths as current DSG ONE health/readiness endpoints without a newer proof.

### Spacetime proof

`GET http://127.0.0.1:8787/health` returned HTTP 200:

```json
{"ok":true,"service":"dsg-spacetime-mcp","transport":"streamable-http","protocolVersion":"2025-06-18"}
```

Production public MCP remains `https://aws.dsg.pics/mcp`.

### Cinema proof

`GET http://127.0.0.1:8000/health` returned HTTP 200:

```json
{"status":"ready","backend":"ready"}
```

### Governed RDC state

Governed activation run `36991572295` is the current RDC acceptance proof and includes status/fs-read/process-list positive execution, negative approval gates for high-risk operations, and evidence-chain verification.

RDC PASS is scoped evidence. It does not by itself make the entire release PASS.

### Remaining gates

1. Authenticated Workroom signed-in user E2E.
2. Production multi-action autonomous Workroom proof with multiple governed provider executions in one goal.
3. Promoted-source external governed public MCP read closeout; latest state remains partial/open.
4. Promoted-source XR state smoke/replay/tamper/remove/final-read closeout.
5. Desktop native enrollment and separate Playwright/Chrome DevTools production bindings.

Until these are closed, whole-system live runtime status remains `PENDING_REVERIFY`.

### Historical evidence

Earlier Azure Container Apps, Azure App Service, Render and pre-promotion AWS digests remain historical records. They must not be substituted for the current AWS bindings above.

### Evidence hierarchy

```
current live runtime + exact deployment identity + persisted evidence
        >
current CI / deployment evidence
        >
repository configuration
        >
historical documentation
```

If evidence is missing, preserve `OPEN`, `PARTIAL`, `UNVERIFIED`, `BLOCKED`, or the applicable fail-closed state.
