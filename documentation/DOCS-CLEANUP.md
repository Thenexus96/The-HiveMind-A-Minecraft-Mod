# Documentation Cleanup — Action List

Context: a lot of `documentation/` was written during earlier GPT sessions,
before the ownership-architecture problems in `PROJECT_STATUS.md` were
identified, and before the monorepo idea was deprioritized. Those docs
describe a different, more finished-sounding project than the one that
actually exists. This file is the one-time cleanup pass. Once actioned, this
file itself can be deleted.

Legend: **KEEP** (accurate, still useful) · **ARCHIVE** (historically
interesting, not to be treated as current truth — move to
`documentation/history/archive/`) · **DELETE** (superseded, redundant, or
actively misleading — safe to remove) · **REWRITE** (kept but needs an update
pass)

## New authoritative docs (this batch)

- `PROJECT_STATUS.md` — **KEEP** (new). Current-state source of truth.
- `VISION.md` — **KEEP** (new). Replaces the manifesto-style docs below.
- `archive/ROADMAP.md` (root-level, this version) — **KEEP** (new). Replaces all
  roadmap variants listed below.

## Roadmap variants — DELETE (superseded by new `archive/ROADMAP.md`)

- `RoadMap v1`
- `Road Map v2`
- `Road Map v3`
- `archive/ROADMAP.md` (old version)
- `documentation/roadmap/phase-1-foundation.md`
- `documentation/roadmap/phase-2-drone-framework.md`
- `documentation/roadmap/phase-3-control-link.md`
- `documentation/roadmap/phase2-sprint1.md`
- `documentation/roadmap/sprint1-runbook.md`
- `documentation/roadmap/progress-tracking.md`

These conflict with each other and mark unfinished work (e.g. hive ownership)
as complete. If any specific sprint task in them is still relevant, it should
be re-added to the new `archive/ROADMAP.md` deliberately, not kept via these files.

## "Implementation complete / verified" reports — DELETE

- `CHAT_SESSION_FINAL_VERIFICATION.md`
- `archive/IMPLEMENTATION_VERIFICATION.md`
- `archive/IMPLEMENTATION_COMPLETE.md`
- `documentation/COMPLETE_IMPLEMENTATION.md`
- `archive/REPO_CLEANUP_SUMMARY.md`

These self-report "✅ VERIFIED" / "✅ COMPLETE" on large amounts of
infrastructure without a real way to check that against the repo, and overlap
each other almost entirely. If something in them is genuinely still true and
useful (e.g. the CI workflow explanation), that content already lives more
reliably in `CI.md` / `GITEA_CI.md` / `CODE_QUALITY.md`.

## Philosophy / manifesto / "session transcript" docs — ARCHIVE

Not deleted outright because they're an interesting record of how the
project's thinking evolved, but must never be read as current guidance:

- `documentation/00_Project/LSP Planning/LSP.md`
- `documentation/doc conversation.md`
- `documentation/project_memory.md`
- `documentation/history/document_pyramid.md`
- `documentation/PROJECT_BIBLE.md`
- `documentation/PROJECT_CHARTER.md`
- `documentation/00_Project/PROJECT_VISION.md`
- `documentation/00_Project/PROJECT_OVERVIEW.md`
- `documentation/00_Project/DESIGN_PHILOSOPHY.md`
- `documentation/00_Project/CORE_PRINCIPLES.md`
- `documentation/history/future_README.md`

Move these to `documentation/history/archive/` verbatim. The genuinely useful
ideas from them (systems-over-features, emergence-over-scripting, the HiveCore
long-term idea) are already folded into `VISION.md` in a couple paragraphs
instead of a dozen documents.

## Architecture decision on monorepo — ARCHIVE, with a correction note

- `documentation/history/ADR/ADR-0006-monorepo-organization.md` — **ARCHIVE**,
  but add a one-line note at the top: *"Superseded — monorepo restructuring
  is explicitly deprioritized as of mid-2026. See `VISION.md`."* Don't delete
  it; it's a real decision record, it's just no longer the active decision.

## Real ADRs — KEEP

- `ADR-0001-project-origin.md`
- `ADR-0002-fabric-selection.md`
- `ADR-0003-golem-to-drone.md`
- `ADR-0004-cardinal-components-decision.md`
- `ADR-0005-roadmap-restructure.md` — mostly fine, but note it's about a
  roadmap structure that's since been replaced again by this cleanup.

These document real, specific technical decisions with reasoning and
alternatives considered. That's exactly what ADRs should be. Keep the format
for future decisions too.

## Architecture docs — REWRITE (not now, but flagged)

- `documentation/architecture/SYSTEM_ARCHITECTURE.md`
- `documentation/architecture/ENTITY_ARCHITECTURE.md`

These are fine in tone but abstract/aspirational rather than describing the
actual current class structure. Lower priority than the code fix — worth a
pass once the ownership system is actually unified, so the architecture doc
can describe the real thing instead of the intended one.

## CI / code quality / release docs — KEEP

- `CI.md`, `GITEA_CI.md`, `CODE_QUALITY.md`, `MAINTENANCE.md`,
  `API_STABILITY.md`, `RELEASE.md`, `DECISION_PROCESS.md`, `QUICK_START.md`
- `GITEA_OPTIMIZATION.md` — **DELETE** (its content is fully covered by
  `GITEA_CI.md`; keeping both is exactly the duplication problem)

These are grounded in real config files (`build.gradle`, `checkstyle.xml`,
`.gitea/workflows/`) and match them. Genuinely useful reference material —
no changes needed right now.

## Design decisions doc — KEEP

- `documentation/DESIGN_DECISIONS.md` — accurate record of real Phase 2
  decisions (roles, combat, multiplayer hive model, performance targets) made
  July 2, 2026. This is a good model for how future decisions should be
  recorded — short, dated, reasoning included.

## Wiki / player-facing docs — KEEP, low priority

- `wiki/Player-Guide.md`, `wiki/Developer-Guide.md`, `wiki/API-Documentation.md`,
  `wiki/Getting-Started.md`, `wiki/Home.md` — fine as-is, revisit once
  gameplay/API surface stabilizes post-architecture-fix.

## Misc

- `Things-to-change.md`, `loom_issue_bundle.md`, `OG-README.md` — **KEEP**,
  these are small, honest, and low-maintenance. `OG-README.md` in particular
  is worth keeping as-is — it's Nick's own voice and the actual origin story,
  which is more genuine than anything in the manifesto docs.
- `documentation/00_Project/LSP Planning/` folder — after archiving `LSP.md`,
  this folder can be removed if empty.
- `documentation/master document checklist.md` — **DELETE**. It's a checklist
  for producing the exact document sprawl this cleanup is undoing.
- `documentation/PROJECT_INDEX.md` — **DELETE**. Describes a documentation
  hierarchy (`00_Project` through `08_HiveMind`, Tier 1–4 authority levels)
  that was never actually built out and adds more structure than a
  single-module hobby-scale mod needs.

## Net effect

Before: ~50+ documentation files, several contradicting each other, no single
place to check "is this actually true right now."

After this pass: `PROJECT_STATUS.md`, `VISION.md`, `archive/ROADMAP.md`,
`DESIGN_DECISIONS.md`, the 5 real ADRs, the CI/quality docs, and the wiki —
roughly 15 files, all of which should actually match reality.