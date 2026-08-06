from __future__ import annotations

import os
from dataclasses import dataclass


@dataclass(frozen=True)
class Settings:
    api_key: str | None
    proof_signing_secret: str | None
    audit_db_path: str


def load_settings() -> Settings:
    return Settings(
        api_key=os.getenv("DSG_API_KEY") or None,
        proof_signing_secret=os.getenv("PROOF_SIGNING_SECRET") or None,
        audit_db_path=os.getenv("AUDIT_DB_PATH", "./.data/dsg_audit.sqlite3"),
    )
