---
description: >-
  Historical Render MCP registry snapshot from August 2026. Current production
  authority is AWS and current discovery must be performed against aws.dsg.pics.
---

# Historical Unified MCP Runtime Reference — Render v1.2.0

> **Historical snapshot only.** The 65-tool registry below was verified on the former Render control-plane deployment on 13 August 2026. It is retained for audit/history and is **not** the current AWS production registry.

## Current production entry

Current governed MCP edge:

`https://aws.dsg.pics/mcp`

Current runtime claims must come from fresh discovery/verification against the AWS edge or the current private connector. Do not treat the historical 65-tool Render list as evidence that those tools are currently advertised or their providers are online.

## Historical Render endpoint

Former endpoint:

`https://tdealer01-crypto-dsg-control-plane.onrender.com/api/mcp`

Historical verification:

| Check                    | Historical result                          |
| ------------------------ | ------------------------------------------ |
| Server                   | `dsg-control-plane-unified-mcp`            |
| Version                  | `1.2.0`                                    |
| Tool discovery           | 65 tools                                   |
| Anonymous protected call | HTTP 401 / fail-closed                     |
| Authenticated execution  | REVIEW at that verification time           |
| Verified date            | 2026-08-13 UTC                             |
| Deployed commit          | `69c6204e04363ea9a5c4f20721c2757907180337` |

## Historical protocol examples

```json
{"jsonrpc":"2.0","id":"init-1","method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"client","version":"1.0"}}}
```

```json
{"jsonrpc":"2.0","id":"list-1","method":"tools/list","params":{}}
```

The historical tool catalogue remains useful for migration comparison, but every entry must be rediscovered on the current AWS surface before being treated as live.

## Truth boundary

* Historical registry presence proves only what the Render endpoint advertised at the recorded time.
* It does not prove current AWS route discovery.
* It does not prove downstream provider availability.
* A previous anonymous 401 proves that tested denial only.
* Dispatch or queue success remains REVIEW until provider/runtime postconditions and evidence are verified.

Use the current AWS production/verification pages for present-tense status.
