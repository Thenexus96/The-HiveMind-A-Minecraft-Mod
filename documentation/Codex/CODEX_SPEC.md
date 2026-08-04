# In-Game Codex — Spec

**Status:** Design only — not implemented yet. This is a plan to build
against once the architecture cleanup in `../PROJECT_STATUS.md` is far enough
along that adding a player-facing feature makes sense.

## Goal

A player (or Nick, mid-testing) should be able to press a key or open a GUI
and read "what does this block/item/system do" without leaving the game — and
that text should come from the same source as the repo wiki, not be retyped.

## Content model

Every lookup-able thing (a block, an item, a system like "Drone Roles") gets
one file in ``, in a specific subfolder:

```
documentation/reference/
├── items/
│   └── drone-core.md
├── blocks/
│   └── hive-core.md
├── entities/
│   └── drone.md
├── commands/
│   └── hive-join.md
└── systems/
    └── drone-roles.md
```

Each file uses simple frontmatter plus body text — see
`reference/_TEMPLATE.md` for the exact format. The frontmatter (`id`,
`title`, `category`, optionally `related`) is what lets tooling turn this into
structured data later; the body is plain Markdown that reads fine both on the
repo page and (after stripping Markdown syntax down to Minecraft's `Text`
formatting) in-game.

## Why files, not a database or in-code strings

- Writing entries as Markdown means the *same* file is genuinely useful as a
  GitHub-rendered wiki page with zero extra work.
- Keeping them out of Java means non-code contributors (or future-you on a
  laptop without the dev environment set up) can add/edit entries.
- One file per topic keeps diffs small and PR review sane.

## Getting content into the game (implementation plan, not yet built)

1. **Short term (cheap, good enough to start):** a Gradle task
   (`generateCodexData`) that reads `documentation/reference/**/*.md`, strips
   frontmatter into a small JSON index (`id → title, category, plaintext
   body`), and writes it to `src/main/resources/data/hivemind/codex/`. This
   runs at build time, so the JSON is always regenerated from the Markdown —
   never hand-edited.
2. **In-game side:** a `CodexScreen` (same GUI pattern already used by
   `HiveMindMainGui` / `HiveMindConfigScreen` — `GridWidget` + `ButtonWidget`
   list) with:
    - A category list on the left (Items / Blocks / Entities / Commands / Systems)
    - A simple text panel on the right rendering the current entry's body
    - Optional: a "jump to related entry" link using the `related` frontmatter field
3. **Trigger points:** a keybind to open the Codex directly, *and*
   context-sensitive access — e.g. right-clicking a Hive Core block with an
   empty hand while sneaking opens straight to that block's entry. (Nice to
   have, not required for v1.)

## What this explicitly is not

- Not a Patchouli-style illustrated book (mentioned in an old roadmap draft).
  That's a bigger asset/formatting investment; plain text entries are the
  right scope until there's a reason to want more.
- Not real-time synced with the repo — it's baked in at build time. If docs
  change, the mod needs a rebuild to pick it up. That's fine for this project's scale.

## Next concrete step

Once 3–4 real entries exist in `` (see the examples already added:
`items/drone-core.md`, `systems/drone-roles.md`), the Gradle task in step 1
above is a good, contained, teachable piece of Java/Groovy to build together
— reading files, parsing simple frontmatter, writing JSON. That's a good
follow-up session once the ownership-system fix is done.