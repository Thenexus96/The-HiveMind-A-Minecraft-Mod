# AI Contribution Guidelines

## Required behavior

- Read the relevant active documentation before changing code or writing new project documents.
- Follow the established Minecraft 1.20.1 and Fabric baseline.
- Preserve system ownership: the Hive handles collective strategy; drones handle local execution.
- Prefer small, composable systems with defined interfaces or events over direct, broad coupling.
- Keep persistence and identity deliberate and purpose-built.
- Follow the repository's documented engineering and documentation standards.

## Boundaries

- Do not introduce Cardinal Components as a core HiveMind dependency.
- Do not turn an entity into a global manager or a single class into a catch-all system.
- Do not invent gameplay systems, change architecture, or reinterpret a decision record without maintainer approval.
- Do not delete files or content without first explaining the reason and receiving approval.

## When uncertain

State the conflict or open question, cite the relevant documents, and ask for direction. Prefer a reversible, minimal change when implementation is authorized.

