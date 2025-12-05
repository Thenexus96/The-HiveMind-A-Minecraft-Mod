# Changelog

All notable changes to the HiveMind mod will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

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