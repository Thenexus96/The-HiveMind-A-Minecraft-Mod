# Documentation Index

The current knowledge base lives in `documentation/`. Use the following
sources by purpose.

| Need | Primary source |
| --- | --- |
| What's true about the project *right now* | `documentation/PROJECT_STATUS.md` — always the tiebreaker if anything disagrees |
| Why this project exists, long-term direction | `documentation/VISION.md` |
| Near-term plan, what's explicitly not being done | `../documentation/archive/ROADMAP.md` |
| System and entity boundaries, how things actually work | `documentation/architecture/` |
| Significant accepted decisions and their reasoning | `documentation/history/ADR/` and `documentation/DESIGN_DECISIONS.md` |
| Engineering and documentation standards | `documentation/development/` |
| Historical context — GPT-era planning docs, old roadmaps | `documentation/history/archive/` — **context only, never current truth** |
| User- and contributor-facing guides | `documentation/guides/`, `documentation/reference/`, `README.md`, `CONTRIBUTING.md` |
| What happened to old docs and why | `documentation/DOCS_CLEANUP.md` |

## A note on `PROJECT_INDEX.md`

`documentation/PROJECT_INDEX.md` (if still present) describes an intended
`docs/00_Project` through `docs/08_HiveMind` numbered hierarchy that was
never actually built — the repo uses `documentation/` with the flatter
category structure listed above instead. Treat `PROJECT_INDEX.md` as
superseded by this file and by `documentation/README.md`; per
`DOCS_CLEANUP.md` it's slated for removal. If you find yourself following
its numbered-folder paths and they don't exist, that's expected — use the
table above instead.