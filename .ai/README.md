# HiveMind Documentation

Start here. This index is the map for everything else in `documentation/`.

If you're new to the project: read `../OG-README.md` first for the actual
origin story and tone of the mod, then `VISION.md`, then `PROJECT_STATUS.md`.

## For players

- [`guides/GETTING_STARTED.md`](guides/GETTING_STARTED.md) — install, join a
  HiveMind, deploy your first drone
- [`guides/COMMANDS.md`](guides/COMMANDS.md) — every player-facing command
- [`reference/`](reference/) — one entry per item, block, entity, and command.
  This is also the source content for the in-game Codex (see below).

## For contributors

- [`../CONTRIBUTING.md`](../CONTRIBUTING.md) — how to file issues, PR
  checklist, code style
- [`PROJECT_STATUS.md`](PROJECT_STATUS.md) — **the current source of truth.**
  What's actually built, what's broken, what's being worked on right now.
- [`VISION.md`](VISION.md) — why this project exists and where it might go
- [`ROADMAP.md`](ROADMAP.md) — near-term plan
- [`architecture/`](architecture/) — how the systems that exist actually work,
  written against real code, not aspirational design
- [`decisions/`](decisions/) — ADRs: specific technical decisions, the
  alternatives considered, and why. Add a new one whenever a non-obvious
  choice gets made — see the template in that folder.

## For the in-game Codex

- [`guides/CODEX_SPEC.md`](guides/CODEX_SPEC.md) — how the `reference/` docs
  become in-game lookup content, and the plan for the GUI that reads them.

## If you're an AI assistant picking this up cold

This documentation set is deliberately written to work as onboarding context
for any LLM session — not just ones with prior chat history on this project.
Read in this order:

1. `PROJECT_STATUS.md` — what's actually true right now. Trust this over
   your own inferences from the code, and trust it over any other doc.
2. `architecture/` — how the systems that exist actually work. If a system
   isn't documented here yet, don't assume it's fine; check `PROJECT_STATUS.md`
   for whether it's a known problem area first.
3. `ROADMAP.md` — what's intentionally next, so you don't propose work
   that's already planned differently or already rejected (see its
   "Explicitly not doing" section).
4. `VISION.md` — before proposing restructuring, multi-module splits, or
   "let's future-proof this," read this. Several of those ideas were already
   considered and explicitly parked — check here before re-proposing them as new.

**Do not treat `history/` (archived docs) as current truth**, even though it's
in the same repo and may read as confident and finished. It's kept for
context on how the project's thinking evolved, not as a reliable source.

For *behavioral* rules — how to act while contributing, not what's true about
the project — see [`../.ai/README.md`](../.ai/README.md). That directory and
this section are meant to complement each other, not duplicate each other:
this reading order is the one to follow either way.

## Historical record

- [`history/`](history/) — changelog, implementation log, and the archived
  GPT-era planning documents (kept for context, not treated as current truth)

---

## Documentation principles for this project

These exist because the previous documentation set violated most of them:

1. **One doc, one truth.** If two documents could disagree about the same
   fact, that's a bug in the doc set — merge them or make one clearly
   authoritative (`PROJECT_STATUS.md` always wins for "what's true right now").
2. **Write against the code, not against the plan.** Architecture docs
   describe what's implemented. Plans belong in `ROADMAP.md`, clearly marked
   as not-yet-real.
3. **Short and current beats long and aspirational.** A two-paragraph doc
   that's accurate is worth more than ten pages of philosophy that predate
   the last three architecture changes.
4. **Every non-obvious decision gets an ADR.** Future-you (or a contributor)
   should never have to guess why something is the way it is.
5. **Reference docs are dual-purpose.** Anything in `reference/` should be
   written so it works equally well as a wiki page and as in-game Codex text
   — plain language, short entries, no repo-only assumptions (no "see the
   file above," since in-game there is no "above").