# 🧠 HiveMind

**A Minecraft mod for controlling drone entities through a neural hive network**

- **Status:** Active development
- **Minecraft:** 1.20.1
- **Loader:** Fabric

## 🎮 About

HiveMind allows players to connect to a collective consciousness and control autonomous drone entities. Deploy, command,
and directly possess drones to extend your reach in the Minecraft world.

## ✨ Features

- **HiveMind Access System** - Join the collective consciousness
- **Drone Deployment** - Deploy and link drones to your HiveMind
- **HiveCode System** - Short, readable drone identifiers (D-001, D-002, etc.)
- **Direct Control** - Possess drones for first-person control
- **Persistent Data** - Drones survive server restarts
- **Role System** - Assign roles (Idle, Scout, Soldier) to drones

## 📦 Installation

1. Build the project locally, or obtain a release when one is published.
2. Install Fabric Loader for Minecraft 1.20.1.
3. Install Fabric API compatible with the selected Minecraft version.
4. Place the mod JAR in your `mods` folder
5. Launch Minecraft

## 🚀 Quick Start

1. **Get HiveMind Access** - Use the HiveMind Access Item
2. **Deploy a Drone** - Use a Drone Core on the ground
3. **Link Drones** - Use `/hivemind_link` while looking at a drone
4. **Control Drones** - Use `/hivemind control D-001` to take direct control

## 📖 Documentation

**Start Here**:
- [🚀 Quick Start Guide](documentation/QUICK_START.md) — Navigation for all docs
- [✅ Implementation Complete](documentation/IMPLEMENTATION_COMPLETE.md) — Summary of all implementations

**Development & CI**:
- [Gitea CI/CD Guide](documentation/GITEA_CI.md) (Primary — Recommended First Read)
- [Code Quality & Static Analysis](documentation/CODE_QUALITY.md)
- [CI & Developer Workflows](documentation/CI.md) (GitHub Actions reference)

**Release & Versioning**:
- [Release & Version Management](documentation/RELEASE.md)
- [API Stability & Backward Compatibility](documentation/API_STABILITY.md)

**Planning & Roadmap**:
- [Project Roadmap & Feature Planning](documentation/ROADMAP.md)
- [Technical Decision Process](documentation/DECISION_PROCESS.md)

**Project Docs**:
- [Player Guide](documentation/wiki/Player-Guide.md)
- [Commands Reference](documentation/wiki/Commands.md)
- [Developer Documentation](documentation/wiki/Developer-Guide.md)
- [API Guide](documentation/wiki/API-Documentation.md)
- [Repository Maintenance](documentation/MAINTENANCE.md)

## 🗺️ Roadmap

See our [Development Roadmap](documentation/roadmap/progress-tracking.md) for current progress and upcoming features.

### Current Phase: Phase 2 - Drone Framework ✅

- [x] DroneEntity implementation
- [x] Ownership system
- [x] HiveCode system
- [x] Basic AI behaviors
- [ ] Debug tools (in progress)

## 🤝 Contributing

Contributions are welcome! Please read [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## 🐛 Bug Reports

Found a bug? Use the repository's bug-report template in `.github/ISSUE_TEMPLATE/bug_report.md`.

## 📜 License

A license file is not currently present in the repository. Do not distribute or reuse the project under an assumed license; add an explicit license before publishing releases.

## 🙏 Acknowledgments

- Built with [Fabric](https://fabricmc.net/)
- Minecraft version 1.20.1

---

**Status:** Active Development | **Version:** 0.2.0-alpha+1.20.1 | **Minecraft:** 1.20.1
