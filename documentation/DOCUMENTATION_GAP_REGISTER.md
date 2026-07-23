# Documentation Gap Register

This register distinguishes missing documents from unimplemented systems. It
is not a feature roadmap — see `ROADMAP.md` for that.

## Resolved since the last version of this register

- `PROJECT_STATUS.md`, `VISION.md`, `ROADMAP.md` created — the project no
  longer lacks a single current-truth document.
- `architecture/OWNERSHIP_SYSTEM.md`, `SERVICE_LAYER.md`,
  `ROLE_BEHAVIOR_SYSTEM.md`, `NETWORKING.md` added as deep-dive companions
  to the existing `AI_ARCHITECTURE.md`, `DATA_ARCHITECTURE.md`,
  `EVENT_ARCHITECTURE.md`, `ENTITY_ARCHITECTURE.md`, `SYSTEM_ARCHITECTURE.md`
  — all cross-linked so the overview/deep-dive relationship is explicit.
- The `PROJECT_INDEX.md` layout mismatch this register previously flagged is
  resolved by deletion — see `DOCS_CLEANUP.md`. `documentation/README.md` is
  now the actual navigation entry point.
- The GPT-era contradictory doc set (multiple roadmap versions, several
  "implementation complete" reports, philosophy/manifesto docs) has an
  executed cleanup plan — see `DOCS_CLEANUP.md` and
  `DOCS_CLEANUP_ADDENDUM.md`.

## Still-open gaps (missing documents)

- `architecture/CONFIG_SYSTEM.md`, `DEBUG_TOOLS.md`, `CONTROL_POSSESSION.md`
  — identified as reasonable next architecture docs, intentionally not
  written yet. `ModConfig` in particular has real pending issues (SpotBugs
  batches 5–6 in `PROJECT_STATUS.md`) — writing its architecture doc before
  those land means writing it twice.
- `reference/` per-topic entries exist for exactly two topics
  (`items/drone-core.md`, `systems/drone-roles.md`) as format
  demonstrations. Full coverage (every block/item/command) is real,
  mechanical work, deliberately deferred — see `guides/CODEX_SPEC.md`.
- `documentation/wiki/Getting-Started.md`, `Home.md`, `Player-Guide.md`,
  `API-Documentation.md` are empty files, linked from the root `README.md`
  as if they have content. Tracked as `.ai/TASKS.md` Task 005.
- `documentation/design/architecture.md`, `design/data-flow.md` — empty,
  same task.
- No document yet explains the Codex build pipeline in practice (the Gradle
  task described in `CODEX_SPEC.md` doesn't exist yet — the spec is a plan,
  not a build artifact).

## Planned-system documents not yet created

Documents whose underlying systems don't exist or aren't approved yet:
HiveCore intelligence, task assignment, memory system, broader AI planning,
platform-level reusable theory (the `VISION.md` HiveCore idea). Create these
only once a design is approved or a spec is ready — writing "current"
documentation for them now would misrepresent what's implemented, which is
exactly the failure mode this whole cleanup addressed.

## Layout note

`documentation/` uses flat, unnumbered category folders
(`architecture/`, `reference/`, `guides/`, `history/`, `development/`,
`specs/`). This is the actual, current, correct layout — not a placeholder
for some other intended structure. If a document from the archived GPT-era
set references a `docs/00_Project` through `docs/08_HiveMind` numbered
hierarchy, that hierarchy was never built and should not be treated as a
migration target.