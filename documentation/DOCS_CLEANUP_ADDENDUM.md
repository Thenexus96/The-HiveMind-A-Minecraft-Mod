# Documentation Cleanup — Addendum (Audit Pass)

Follow-up to `DOCS_CLEANUP.md`. That pass covered what was obviously wrong;
this one went through everything that hadn't been checked yet. Same legend:
**KEEP** / **ARCHIVE** / **DELETE** / **REWRITE**, plus **DECISION NEEDED**
for the one item that's a real fork, not a cleanup call.

## RESOLVED — `documentation/reference/` folder collision

**Decision (Nick):** keep the separation, both live under `reference/`. The
existing index files (`SYSTEM_INDEX.md`, `ENTITY_INDEX.md`,
`ARCHITECTURE_INDEX.md`) stay as the quick-reference layer; the new
per-topic subfolders (`items/`, `systems/`, etc.) are the deeper context
layer. Same resolution pattern as the architecture-doc overlaps below —
cross-link rather than merge or relocate. Index files updated to link into
the new entries where a topic exists in both.

## Architecture doc overlaps — RESOLVED (cross-linked, not merged)

Three architecture docs already existed and weren't checked before this
session's new docs were written:

| Existing (overview) | New (deep-dive) | Resolution |
|---|---|---|
| `architecture/EVENT_ARCHITECTURE.md` | `architecture/NETWORKING.md` | Cross-linked both directions |
| `architecture/DATA_ARCHITECTURE.md` | `architecture/OWNERSHIP_SYSTEM.md` | Cross-linked both directions |
| `architecture/AI_ARCHITECTURE.md` | `architecture/ROLE_BEHAVIOR_SYSTEM.md` | Cross-linked both directions |

All three existing docs were accurate — the gap was process (new docs
written without reading the full existing `architecture/` directory first),
not content. Updated versions of the existing three are included in this
batch of files.

## RESOLVED — Rank-system content in specs

**Clarification (Nick):** the Rank content in
`documentation/specs/Drone/SPEC-0002-DRONE_LIFECYCLE.md` isn't stale or
contradicting a rejection — it's correctly describing a later stage of the
plan. Sequencing is foundational systems first (ownership reconciliation,
`DroneEntity` split), then a categorization pass for role/progression
flexibility. `PROJECT_STATUS.md` and `ROADMAP.md` updated to say "deferred,"
not "dropped." No change needed to `SPEC-0002.md` itself — it was accurate,
just about later work. Original concern (that it read as live/current when
it isn't yet) still stands as a mild readability note, not an error: a
future contributor skimming specs/ could reasonably think Rank is next up.
A one-line "Status: describes a later phase, not current work" note at the
top would remove that ambiguity if it's ever revisited.

- `documentation/specs/Drone/SPEC-0001` and `SPEC-0003` reference
  `HIVE_CORE.md`, `TASK_SYSTEM.md`, `MEMORY_SYSTEM.md` as related docs —
  none of these exist yet. Not urgent (marked "Draft" already), just worth
  knowing these are aspirational cross-references.

## Not evaluated in the original pass — new calls

- `documentation/DEVELOPER_HANDBOOK.md` — **ARCHIVE**. Philosophy-heavy,
  links to already-archived docs (`PROJECT_CHARTER.md` etc.), and overlaps
  `CONTRIBUTING.md` + `.ai/WORKFLOW.md` almost entirely. Nothing in it isn't
  said more concisely elsewhere now.
- `documentation/history/CHANGELOG.md` — **DELETE**. Duplicates the root
  `CHANGELOG.md` but with vague template content instead of real version
  entries. The root file is the accurate one — having two changelogs with
  different content is exactly the "which one do I trust" problem this
  whole cleanup exists to prevent.
- `documentation/history/IMPLEMENTATION_LOG.md` — **ARCHIVE**. Same
  GPT-era vague-status-language pattern as docs already archived
  ("Phase 2 — Drone Framework: Active Development" with no specifics);
  should have been caught in the first pass.
- `documentation/history/SESSION_HISTORY.md` — **ARCHIVE**, same reasoning.
- `documentation/DOCUMENTATION_GAP_REGISTER.md` — **KEEP, needs a refresh**.
  This one's actually good — it already flagged the `PROJECT_INDEX.md`
  layout mismatch before this cleanup started. Just needs updating to
  reflect that `PROJECT_STATUS.md`/`VISION.md`/`ROADMAP.md`/`architecture/`
  now exist, so it's not still describing them as gaps.

## Stale outbound links — needs a pass once the archive/delete list executes

These files link to docs marked for deletion in `DOCS_CLEANUP.md`. Not
broken yet (nothing's actually been deleted from the repo), but will be the
moment cleanup is executed:

- `documentation/QUICK_START.md` → links to `REPO_CLEANUP_SUMMARY.md`,
  `roadmap/progress-tracking.md`, `roadmap/phase-2-drone-framework.md`
- Root `README.md` → links to `IMPLEMENTATION_COMPLETE.md`, the old
  `documentation/ROADMAP.md`

Recommend fixing these links in the same commit that actually removes the
target files, not before — otherwise there's a window where the links are
"fixed" but pointing at files that still exist in two versions.

## Empty stub files — folding into existing Task 005

`.ai/TASKS.md` Task 005 currently only mentions `design/architecture.md`,
`design/data-flow.md`, and the unreviewed `ENGINEERING_HANDBOOK.md`. Also
empty, found this pass:

- `documentation/wiki/Getting-Started.md`
- `documentation/wiki/Home.md`
- `documentation/wiki/Player-Guide.md`
- `documentation/wiki/API-Documentation.md`

These are linked from the root `README.md` as if they have content. Same
call as the rest of Task 005: fill in once there's real content, or note
they're placeholders so nobody assumes they're documented topics.

## Confirmed fine, no action

- `.ai/DOCUMENT_RULES.md`, `AI_GUIDELINES.md`, `STYLE_GUIDE.md`, `WORKFLOW.md`
  — all still consistent with the new doc set, no stale references found
- `documentation/development/DOCUMENTATION_STYLE_GUIDE.md`,
  `ADR_TEMPLATE.md`, `SPECIFICATION_TEMPLATE.md`, `DIAGRAM_STANDARDS.md`,
  `TRACEABILITY_STANDARD.md`, `TESTING.md` — generic/accurate, hold up fine
- `documentation/history/ADR/*` (the 6 real ADRs) and `ADR/README.md` index
  — consistent, ADR-0006 already has its correction note from the first pass