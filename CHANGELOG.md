# Changelog

All notable changes to the HiveMind mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

## [Sprint 1] - 2026-08-06

### Added
- **Debug Mode** (`/hive debug toggle`, `/hive debug dump`) — Real-time state inspection for developers
- **HiveManager & Persistence** — Player collectives with NBT-based world persistence
- **Research System** — Unlock research nodes to unlock colony upgrades
    - Sample node: `soldier_damage_1` (+5% soldier damage)
    - Research state persists across server restarts
- **Node Claiming** (`/hive node claim <nodeId>`) — Claim resource nodes to gather resources
- **Service Layer Abstraction** — Testable ownership and data access patterns

### Fixed
- DroneLinkUtilsTest compile bug (duplicate variable declarations)
- Service-layer bypass in DroneEntity connection methods
- Ownership system architecture (formalized via ADR-0007)

### Quality
- 44 unit tests, all passing
- Integration tests verified (server restart persistence)
- 0 new bugs introduced
- Code style compliance (checkstyle)
- Full test coverage for core Sprint 1 features

### Technical
- Established HiveMindServiceManager abstraction layer
- Formalized ownership hierarchy (ADR-0007)
- Created DESIGN_DECISIONS.md for architecture documentation
- Documented debug command system

### Known Issues
- 40 pre-existing SpotBugs warnings (deferred to Tier 2 Quality Pass)
- IntelliJ formatter conflicts with Spotless (formatting issue, not code quality)

See [QUICK_START.md](documentation/QUICK_START.md) for new feature usage.

## [Unreleased]

### Added

- Debug mode system for faster development
- Drone role system (Idle, Scout, Soldier)

### Changed

- Improved drone rotation sync for possession

### Fixed

- Drone ownership not persisting across restarts

## [0.2.0] - 2025-11-17

### Added

- HiveCode system for drone identification (D-001, D-002, etc.)
- Drone Core item for deployable drones
- Visual effects for linked drones
- Persistent drone data across server restarts

### Changed

- Renamed "Golem" references to "Drone"
- Improved networking for drone control

### Fixed

- Camera jitter during drone possession
- Ownership data not saving properly

## [0.1.0] - 2025-11-05

### Added

- Initial release
- HiveMind Access item
- Basic drone entity
- Possession system
- `/hivemind_link` and `/hivemind_list` commands

[Unreleased]: compare-link

[0.2.0]: release-link

[0.1.0]: release-link