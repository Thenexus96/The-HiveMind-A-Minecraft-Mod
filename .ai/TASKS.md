# Documentation Bootstrap Tasks

## Task 001 — AI onboarding layer

**Status:** Complete

Create an AI-agnostic `.ai/` contribution layer that directs users to the
existing HiveMind knowledge base without duplicating or replacing it.

## Task 002 — Documentation inventory and reconciliation

**Status:** Complete

Resolved by `documentation/DOCS_CLEANUP.md` — a full file-by-file action list
(keep / archive / delete / rewrite) covering everything that was in
`documentation/` at the time, plus the new `documentation/PROJECT_STATUS.md`,
`VISION.md`, `ROADMAP.md`, and `architecture/` set that replaced the
contradictory GPT-era material. Actioning `DOCS_CLEANUP.md`'s archive/delete
list against the live repo is still a manual step for Nick — this task
covered producing the proposal, not executing every file move.

## Task 003 — Canonical-document decision

**Status:** Complete

Decision: the old `PROJECT_BIBLE.md` / `PROJECT_CHARTER.md` /
`00_Project/*` set did not satisfy the need — it was internally
contradictory, described unbuilt systems as complete, and argued for
directions (monorepo restructuring) that were later explicitly deprioritized.
`documentation/PROJECT_STATUS.md` and `documentation/VISION.md` are the new
canonical documents. The old set is archived per `DOCS_CLEANUP.md`, not
treated as current.

## Task 004 — Reconcile `.ai/` against the new `documentation/` set

**Status:** Complete

`.ai/README.md`, `PROJECT.md`, and `DOCUMENT_INDEX.md` previously pointed at
now-archived docs (`PROJECT_BIBLE.md`, `00_Project/`,
`architecture/SYSTEM_ARCHITECTURE.md`) and `.ai/README.md` duplicated a
separate AI-onboarding reading order that disagreed with the one added to
`documentation/README.md`. All three files updated to defer to
`documentation/README.md`'s onboarding section and reference the current
doc set.

## Task 005 — Empty / unevaluated `documentation/development/` files

**Status:** Pending maintainer decision

`documentation/design/architecture.md` and `documentation/design/data-flow.md`
exist but are empty. `documentation/development/ENGINEERING_HANDBOOK.md` was
not evaluated in `DOCS_CLEANUP.md` and its actual current content wasn't
reviewed as part of this pass. Options: fill them in once there's real
content to put there (e.g. `design/architecture.md` once the ownership
reconciliation settles), or delete if they're not going to be used. Don't
treat their existence as evidence those topics are documented — they aren't.