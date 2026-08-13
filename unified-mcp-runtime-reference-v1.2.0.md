---
description: >-
  Live Render MCP registry, protocol usage, authentication boundary and all 65
  advertised tools.
---

# Unified MCP Runtime Reference — v1.2.0

> **Status: PASS for discovery; REVIEW for authenticated execution.** This page is generated from the live Render registry. It replaces the unmerged PR #1088 interface list as the production reference.

## Start here

Production endpoint:

`https://tdealer01-crypto-dsg-control-plane.onrender.com/api/mcp`

1. Send JSON-RPC `initialize`.
2. Send `tools/list` to discover the current schemas.
3. Use `tools/call` only with a valid DSG session or stored MCP key.
4. Treat WRITE/CRITICAL, deployment, billing, outbound-message, device and file mutations as approval-controlled actions.
5. Read the returned status, proof identifiers and audit evidence before calling the action complete.

## Verified runtime evidence

| Check                        | Result            | Evidence                                                                                                                            |
| ---------------------------- | ----------------- | ----------------------------------------------------------------------------------------------------------------------------------- |
| Server                       | **PASS**          | `dsg-control-plane-unified-mcp`                                                                                                     |
| Version                      | **PASS**          | `1.2.0`                                                                                                                             |
| Tool discovery               | **PASS**          | 65 tools returned by JSON-RPC `tools/list`                                                                                          |
| Discovery request            | **PASS**          | HTTP 200; request ID `6c6d95dd-377e-44f6-a980-5ac1706786c9`                                                                         |
| Auth boundary                | **PASS — denial** | Anonymous `dsg.system.status` returned HTTP 401 / JSON-RPC `-32001 Unauthorized`; request ID `dd292674-fb22-437d-86b3-4b842a6de79f` |
| Authenticated tool execution | **REVIEW**        | No session or MCP key was available in this verification session, so no authenticated tool result or execution ID is claimed.       |

Last verified: 2026-08-13 UTC. Deployed commit: `69c6204e04363ea9a5c4f20721c2757907180337`.

## Protocol examples

Initialize:

```json
{"jsonrpc":"2.0","id":"init-1","method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"client","version":"1.0"}}}
```

Discover tools:

```json
{"jsonrpc":"2.0","id":"list-1","method":"tools/list","params":{}}
```

Authenticated read-only call:

```json
{"jsonrpc":"2.0","id":"status-1","method":"tools/call","params":{"name":"dsg.system.status","arguments":{}}}
```

Do not paste API keys into documentation. Supply credentials through the supported DSG authentication mechanism.

## Live tools

The descriptions and required fields below came from the runtime registry at verification time.

## Unified control plane (12)

| Tool                         | Purpose                                                                                                                                                                                                                                                                                       | Required input                                   |
| ---------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------ |
| `dsg.system.status`          | Return the unified DSG Control Plane MCP adapter status without exposing secrets.                                                                                                                                                                                                             | None                                             |
| `dsg.aimo.status`            | Check the DSG ONE AIMO harness surface through the unified control-plane gateway.                                                                                                                                                                                                             | None                                             |
| `dsg.aimo.solve`             | Run the governed AIMO pipeline through DSG ONE -> DSG AGI Simulation -> Cinema Proof Agent.                                                                                                                                                                                                   | `problem`                                        |
| `dsg.aws.contract`           | Return the governed AWS execution contract used by the Control Plane and AWS Agent Toolkit adapter.                                                                                                                                                                                           | None                                             |
| `dsg.aws.deploy`             | Gate and idempotently dispatch the repository CDK deployment workflow. Deployment remains REVIEW until post-deploy evidence verifies it.                                                                                                                                                      | `environment`, `approved`, `idempotencyKey`      |
| `dsg.repair.simulate`        | Generate a binary repair plan and verify the selected candidate exactly with Z3. This tool is plan-only and never mutates a repository.                                                                                                                                                       | `jobId`, `finding`, `candidates`, `allowedFiles` |
| `dsg.evaluate`               | Evaluate an AI agent action through the DSG deterministic gate. Returns gate decision (PASS/BLOCK/REVIEW), proof hash, and policy constraints checked.                                                                                                                                        | `action`, `actor`                                |
| `dsg.verifyClaim`            | Verify whether a production claim is allowed given the current evidence state. Blocks claims like "production-ready" or "certified" that require independent verification.                                                                                                                    | `claim`                                          |
| `dsg.recordEvidence`         | Record an evidence envelope into the CCVS chain. Returns an evidence envelope with integrity hash.                                                                                                                                                                                            | `kind`, `hash`                                   |
| `dsg.exportComplianceBundle` | Export a compliance bundle for a given regulatory framework. Returns the compliance matrix with control statuses and summary.                                                                                                                                                                 | `framework`                                      |
| `dsg.getReadiness`           | Get the current DSG system readiness status including compliance matrix summary, evidence chain health, and deployment posture.                                                                                                                                                               | None                                             |
| `dsg.classifyRisk`           | Deterministically classify an AI-proposed action into an EU AI Act-aligned risk tier (low/medium/high/critical), sourced from docs/consult-toolkit/risk-classification-checklist.md. Caller supplies explicit capability flags; ambiguous or unanswered flags never lower the resulting tier. | `actionDescription`                              |

## Verified Action Compiler (3)

| Tool                          | Purpose                                                                                                                                                        | Required input                     |
| ----------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------- |
| `dsg.action.registry`         | Return the deterministic Action Registry used by the Verified Action Compiler. Registry entries are the only actions the compiler may emit.                    | None                               |
| `dsg.action.compile`          | Compile a verified solution into typed Action IR without executing it. Unknown or unmapped solution parameters fail closed as UNSUPPORTED.                     | `solution`, `proof`                |
| `dsg.action.verifyAcceptance` | Verify Action IR postconditions against independently observed facts and build the final execution receipt when the complete upstream proof chain is supplied. | `plan`, `observations`, `evidence` |

## Deployment adapters (2)

| Tool                 | Purpose                                                                                                                                                    | Required input                                        |
| -------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------- |
| `dsg.deploy.status`  | Return governed deployment adapter readiness for Netlify, Render, and Supabase. GitHub Actions is the dispatcher and evidence boundary.                    | None                                                  |
| `dsg.deploy.execute` | Gate and idempotently dispatch a governed deployment to Netlify, Render, or Supabase. Dispatch success remains REVIEW until provider evidence is verified. | `target`, `environment`, `approved`, `idempotencyKey` |

## Android device and UI (8)

| Tool                           | Purpose                                                                                                                                     | Required input |
| ------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| `device.status.get`            | Queue device.status.get for Android owner-agent review. Class=PASS; owner approval is always required before device execution.              | `deviceId`     |
| `device.open_url`              | Queue device.open\_url for Android owner-agent review. Class=PASS; owner approval is always required before device execution.               | `deviceId`     |
| `device.open_app`              | Queue device.open\_app for Android owner-agent review. Class=PASS; owner approval is always required before device execution.               | `deviceId`     |
| `device.open_settings`         | Queue device.open\_settings for Android owner-agent review. Class=PASS; owner approval is always required before device execution.          | `deviceId`     |
| `ui.back`                      | Queue ui.back for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.                      | `deviceId`     |
| `ui.home`                      | Queue ui.home for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.                      | `deviceId`     |
| `ui.scroll`                    | Queue ui.scroll for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.                    | `deviceId`     |
| `device.notifications.summary` | Queue device.notifications.summary for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution. | `deviceId`     |

## File operations (7)

| Tool                | Purpose                                                                                                                            | Required input |
| ------------------- | ---------------------------------------------------------------------------------------------------------------------------------- | -------------- |
| `file.list_root`    | Queue file.list\_root for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.     | `deviceId`     |
| `file.preview`      | Queue file.preview for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.        | `deviceId`     |
| `file.select`       | Queue file.select for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.         | `deviceId`     |
| `file.send_to_claw` | Queue file.send\_to\_claw for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution. | `deviceId`     |
| `file.rename`       | Queue file.rename for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.         | `deviceId`     |
| `file.move`         | Queue file.move for Android owner-agent review. Class=REVIEW; owner approval is always required before device execution.           | `deviceId`     |
| `file.delete`       | Queue file.delete for Android owner-agent review. Class=BLOCK; owner approval is always required before device execution.          | `deviceId`     |

## Hermes runtime (33)

| Tool                           | Purpose                                                                                                                                   | Required input                |
| ------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------- |
| `hermes.readiness`             | \[READ] Fetch deployment readiness from /api/readiness with a safe warning fallback on server errors.                                     | None                          |
| `hermes.execute_action`        | \[CRITICAL] Create intent and execute through DSG gate with full audit.                                                                   | `agent_id`, `action`          |
| `hermes.browser_navigate`      | \[READ] Open a URL in a Browserbase cloud browser with full JS rendering. Returns session live-view URL + HTTP-fetched text content.      | `url`                         |
| `hermes.telegram_send`         | \[CRITICAL] Send a message to Telegram through DSG spine.                                                                                 | `agent_id`, `chat_id`, `text` |
| `hermes.audit_summary`         | \[READ] Fetch runtime truth and latest ledger entries for an agent.                                                                       | `agent_id`                    |
| `hermes.checkpoint`            | \[WRITE] Create a checkpoint hash from latest truth and ledger.                                                                           | `agent_id`                    |
| `hermes.recovery_validate`     | \[READ] Validate lineage integrity and missing sequences.                                                                                 | `agent_id`                    |
| `hermes.realtime_web_search`   | \[READ] Search live online information and return quick references.                                                                       | `query`                       |
| `hermes.capacity`              | \[READ] Fetch quota remaining and utilization.                                                                                            | None                          |
| `hermes.list_agents`           | \[READ] List org agents and current monthly usage.                                                                                        | None                          |
| `hermes.create_agent`          | \[WRITE] Create a new agent with one-time API key return.                                                                                 | `name`                        |
| `hermes.create_chatbot_agent`  | \[WRITE] Create a chatbot-ready agent with safe defaults for interactive usage.                                                           | None                          |
| `hermes.list_policies`         | \[READ] List available policies.                                                                                                          | None                          |
| `hermes.reconcile_effect`      | \[WRITE] Mark effect status as succeeded or failed.                                                                                       | `effect_id`, `status`         |
| `hermes.list_executions`       | \[READ] List recent executions for this organization.                                                                                     | None                          |
| `hermes.get_execution_proof`   | \[READ] Get replay details and proof context for one execution.                                                                           | `execution_id`                |
| `hermes.list_proofs`           | \[READ] List recent proof artifacts from audit logs.                                                                                      | None                          |
| `hermes.get_ledger`            | \[READ] Get combined ledger and core-ledger snapshot.                                                                                     | None                          |
| `hermes.get_audit`             | \[READ] Get audit events and determinism checks.                                                                                          | None                          |
| `hermes.get_usage`             | \[READ] Get current plan usage and projected overage.                                                                                     | None                          |
| `hermes.get_metrics`           | \[READ] Get current day control-plane performance metrics.                                                                                | None                          |
| `hermes.get_integration`       | \[READ] Fetch integration status and source-of-truth posture.                                                                             | None                          |
| `hermes.get_agent_detail`      | \[READ] Get details and monthly usage for one agent.                                                                                      | `agent_id`                    |
| `hermes.update_agent`          | \[WRITE] Update agent metadata, status, policy, or monthly limit.                                                                         | `agent_id`                    |
| `hermes.rotate_agent_key`      | \[CRITICAL] Rotate and return a new one-time API key for an agent.                                                                        | `agent_id`                    |
| `hermes.delete_agent`          | \[CRITICAL] Disable an agent (soft delete).                                                                                               | `agent_id`                    |
| `hermes.get_enterprise_proof`  | \[READ] Fetch public enterprise proof and attestation report.                                                                             | None                          |
| `hermes.auto_setup`            | \[CRITICAL] Auto-configure default policy, agent, seed execution, billing, onboarding, and runtime roles.                                 | None                          |
| `hermes.write_code_file`       | \[WRITE] Write a code file into the sandbox (/tmp/dsg-code/). Secret injection is blocked.                                                | `filename`, `content`         |
| `hermes.run_code`              | \[CRITICAL] Execute inline code or a sandbox file through the Hermes Brain governance gate. Supports node, python3, bash. Returns stdout. | `runtime`                     |
| `hermes.get_compliance_status` | \[READ] Get live CCVS compliance status — mutation score, claim gates, evidence chain.                                                    | None                          |
| `hermes.get_delivery_proof`    | \[READ] Run a live Delivery Proof scan — checks readiness, health, auth gates on production.                                              | None                          |
| `hermes.fetch_url`             | \[READ] Fetch a public HTTPS URL and return text content (no JS rendering). Fast and lightweight.                                         | `url`                         |

## Truth boundary

* Registry presence proves a tool is advertised by the deployed endpoint; it does not prove every downstream dependency succeeds.
* The anonymous 401 proves the tested auth boundary denied the request; it is not a successful tool execution.
* `SAT` proves feasibility of the encoded constraints only, unless optimality is separately encoded and proved.
* Dispatch or queue success remains **REVIEW** until provider/runtime postconditions and audit evidence are verified.
* PR #1088 is an open draft, is behind current `main`, and its old tool names are not the live v1.2.0 registry names.
