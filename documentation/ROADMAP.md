# Roadmap & Feature Planning — HiveMind

This guide describes the HiveMind project roadmap, how features are planned, and how to contribute to the vision.

## Project Vision

**HiveMind**: A Minecraft mod that allows players to connect to a collective consciousness and control autonomous drone entities to extend their reach in the world.

**Long-term Goals**:
1. Create a fully playable, immersive drone control system
2. Provide strategic gameplay challenges and depth
3. Enable creative player experimentation
4. Maintain performance and stability
5. Support modular expansion and extensibility

---

## Current Status

**Version**: 0.2.0-alpha+1.20.1  
**Phase**: Phase 2 (Drone Framework) — Active Development  
**Target Minecraft**: 1.20.1

### Phase Overview

| Phase | Version | Status | Focus | ETA |
|-------|---------|--------|-------|-----|
| **Phase 1** | 0.1.x | ✅ Complete | Foundation, entity basics | - |
| **Phase 2** | 0.2.x | 🟡 Active | Drone framework, AI, control | Q3 2026 |
| **Phase 3** | 0.3.x | 📋 Planned | Control link, deep integration | Q4 2026 |
| **Phase 4** | 0.4.x | 📋 Planned | Advanced features, content | Q1 2027 |
| **Phase 5** | 1.0.0 | 📋 Planned | Stability, performance, polish | Q2 2027 |

---

## Phase Descriptions

### Phase 1: Foundation (0.1.x) — ✅ Complete

**Objectives**: Establish core entity system and basic infrastructure

**Completed**:
- ✅ DroneEntity implementation
- ✅ Ownership system (player ↔ drone binding)
- ✅ HiveCode identifier system (D-001, D-002, etc.)
- ✅ Basic persistent data storage
- ✅ Fabric mod setup and configuration

**Technologies**: Fabric Loom, Minecraft Forge integration, NBT serialization

---

### Phase 2: Drone Framework (0.2.x) — 🟡 Active

**Objectives**: Build complete drone entity with AI, behaviors, and control

**Current Features** (0.2.0-alpha):
- ✅ Entity rendering and models
- ✅ Basic AI behaviors (idle, patrol, follow)
- ✅ Ownership validation
- ✅ Command integration (partial)
- ✅ Save/load persistence

**In Progress**:
- 🟡 Advanced AI behaviors
- 🟡 Player direct possession mode
- 🟡 Drone roles/classes (Scout, Soldier, Worker, etc.)
- 🟡 Debug tools and visualization

**Planned for 0.2.1+**:
- 📋 Drone formation system
- 📋 Task queue and scheduling
- 📋 Resource carrying/interaction
- 📋 Health and combat mechanics
- 📋 Customization and upgrades

**Success Criteria**:
- Drones fully controllable via commands
- Smooth player possession with first-person view
- Stable AI pathfinding and behaviors
- Persistent drone states across server reloads
- Performance: 50+ drones without lag

---

### Phase 3: Control Link (0.3.x) — 📋 Planned

**Objectives**: Deep integration with player consciousness, immersive control

**Planned Features**:
- 📋 **Hive Network**: Real-time drone consciousness sync
- 📋 **Sensory Integration**: See/hear from drone perspective
- 📋 **Thought Broadcasting**: Send commands to multiple drones
- 📋 **HiveMind Node System**: Build network infrastructure
- 📋 **Resource Flow**: Energy/resources through network
- 📋 **Advanced Roles**: Specialized drone classes
- 📋 **Upgrades & Tech Tree**: Progression mechanics

**Success Criteria**:
- Players can coordinate 10+ drones simultaneously
- Immersive sensory feedback from drone perspective
- Network mechanics add strategic depth
- Drones feel like extensions of player

**Estimated**: Q4 2026

---

### Phase 4: Advanced Features (0.4.x) — 📋 Planned

**Objectives**: Expand gameplay and content

**Planned Features**:
- 📋 **Combat System**: PvP, mob encounters, base defense
- 📋 **Automation**: Automated tasks (mining, building, crafting)
- 📋 **Content**: Structures, rare items, boss encounters
- 📋 **Mod Interactions**: Integration with other mods
- 📋 **Customization**: Drone appearance, colors, cosmetics
- 📋 **Multiplayer**: Cooperative hive networks

**Success Criteria**:
- Rich gameplay variety
- Replayability and endgame content
- Mod compatibility with top mods
- Solid foundation for community content

**Estimated**: Q1 2027

---

### Phase 5: Stability & Polish (1.0.0) — 📋 Planned

**Objectives**: Production-ready, stable release

**Planned Work**:
- 📋 **Performance**: Optimize all systems
- 📋 **Bug Fixes**: Address accumulated issues
- 📋 **Polish**: Visual/audio improvements
- 📋 **Documentation**: Comprehensive guides
- 📋 **API Stability**: Freeze public APIs
- 📋 **Testing**: Comprehensive testing suite

**Success Criteria**:
- Zero known critical bugs
- Performance: 100+ drones, no lag
- API stable for mod developers
- Production-ready for servers/single-player

**Estimated**: Q2 2027

---

## Feature Request Process

### 1. Propose Feature

Create an issue with:
- **Title**: Clear, concise feature name
- **Problem**: What problem does this solve?
- **Solution**: Your proposed approach
- **Scope**: Estimated complexity (Small/Medium/Large)
- **Phase**: Which phase should this be in?

### 2. Discussion & Refinement

- Team discusses feasibility
- Technical alternatives evaluated
- Scope adjusted if needed
- Assigned to phase

### 3. Prioritization

Features prioritized by:
- Impact on gameplay
- Technical complexity
- Community feedback
- Strategic alignment

### 4. Implementation

- Added to phase roadmap
- GitHub issue created
- Assigned to contributor
- Development begins

### 5. Review & Merge

- Code review
- Testing
- Documentation
- Merge to main branch

---

## Technical Decision Process

### Before Making Changes

1. **Check Roadmap**: Does this align with current phase?
2. **API Stability**: Will this break existing mod APIs?
3. **Performance**: What's the performance impact?
4. **Compatibility**: Will this work with target Minecraft versions?

### Decision Framework

**Accept if**:
- ✅ Aligns with current phase goals
- ✅ Doesn't break API stability
- ✅ Performance acceptable
- ✅ Minimal complexity added

**Reconsider if**:
- ⚠️ Scope creep (out of current phase)
- ⚠️ Breaking changes planned
- ⚠️ Performance concerns
- ⚠️ Adds technical debt

**Reject if**:
- ❌ Conflicts with roadmap
- ❌ Breaks API without deprecation
- ❌ Unacceptable performance hit
- ❌ Conflicts with vision

---

## Progress Tracking

### Current Phase Checklist

**Phase 2 (0.2.x) — Drone Framework**:

**Core Features**:
- [x] DroneEntity implementation
- [x] Basic AI behaviors
- [x] Ownership system
- [x] HiveCode system
- [ ] Player direct possession
- [ ] Drone roles/classes
- [ ] Advanced AI pathfinding
- [ ] Drone formations
- [ ] Health/damage system

**Supporting Systems**:
- [x] Persistent storage (NBT)
- [x] Command integration
- [ ] Configuration system
- [ ] Debug tools
- [ ] Performance optimization

**Documentation**:
- [x] Developer guide
- [x] API documentation
- [ ] Modding guide
- [ ] Configuration guide

**Testing**:
- [x] Unit tests
- [x] Integration tests
- [ ] Performance tests
- [ ] Load tests (50+ entities)

**Release Checklist**:
- [x] Code quality checks (Spotless, Checkstyle)
- [ ] Static analysis (SpotBugs)
- [ ] Performance profiling
- [ ] Changelog updated
- [ ] Release notes prepared
- [ ] Artifacts built and verified

### Updating Progress

Progress tracked in GitHub/Gitea issues and PRs:
- Each feature has an issue
- PRs reference issues
- Milestones track phases
- Release checklist before version bump

---

## Branching Strategy

**Branches**:
- `main` — Stable releases
- `dev` — Main development branch
- `feature/*` — Feature branches
- `release/v*` — Release branches

**Workflow**:
1. Create `feature/name` from `dev`
2. Implement feature with tests
3. Create PR to `dev`
4. Code review and merge
5. Release manager merges `dev` to `main` with tag

---

## Milestone Schedule

| Milestone | Target | Status |
|-----------|--------|--------|
| **0.2.0** | Q3 2026 | 🟡 In Progress |
| **0.2.1** | Q3 2026 | 📋 Planned |
| **0.3.0** | Q4 2026 | 📋 Planned |
| **0.4.0** | Q1 2027 | 📋 Planned |
| **1.0.0** | Q2 2027 | 📋 Planned |

---

## Contributing to the Roadmap

### Want to Contribute?

1. **Pick an issue** from current phase
2. **Ask to be assigned**
3. **Fork and create feature branch**
4. **Implement with tests**
5. **Create PR with description**
6. **Code review and iterate**
7. **Merge to dev branch**

### Want to Suggest a Feature?

1. **Check roadmap** first
2. **Create GitHub/Gitea issue**
3. **Describe problem and solution**
4. **Discussion and feedback**
5. **Prioritization** for future phases

### Want to Report a Bug?

1. **Create issue** with reproducible steps
2. **Include logs** and error messages
3. **Platform info**: OS, Java version, etc.
4. **Prioritize**: Critical/High/Medium/Low
5. **Fix and PR** (or wait for assignment)

---

## Viewing the Roadmap

### Short Term (Next Release)

See: `documentation/roadmap/phase-2-drone-framework.md`

### Medium Term (Next Phase)

See: `documentation/roadmap/phase-3-control-link.md`

### Long Term (Future Phases)

See: `documentation/roadmap/` directory

### Live Tracking

GitHub/Gitea Issues and Milestones:
- Milestones: Filter by version
- Issues: Label by phase
- Projects: Visual kanban board

---

## Key Decisions Pending

Issues that need decisions before moving forward:

### Decisions (July 2026)

The following decisions were chosen to guide Phase 2 development (recorded July 2, 2026):

1. **Drone Roles**: Both. Drones will use class-based role definitions (Scout, Soldier, Worker, etc.) implemented as concrete classes/components. The player's progression and hive-level advancement will be driven by a separate research/skill-tree system that improves the HiveMind or player NPCs (not the drone classes directly).

2. **Combat Design**: Hybrid. All drone roles will have combat capability, but roles oriented toward combat (e.g., Soldier) will have higher base stats and combat-focused skills. Utility roles will gain limited combat effectiveness but excel at their non-combat tasks.

3. **Integrations**: Deferred. Official third-party mod integrations are planned for a later production stage; not in Phase 2.

4. **Multiplayer Hive Model**: Per-player hives by default, with the option to join another player's HiveMind. If a player joins, the joining player's hive will go dormant but their resources remain accessible to the host hive while joined.

5. **Performance Targets & Server/Client Trade-offs**: Start conservative and expose as settings. The initial default will be a relatively low safe entity-count target (e.g., 50 drones) and move upward after profiling. A config option will allow server admins to choose higher targets (including a near-infinite option) but with clear warnings. See the new design doc `documentation/DESIGN_DECISIONS.md` for details on server/client trade-offs.

These choices will guide the Phase 2 sprint planning and implementation.

---

## Community Feedback

Ways to provide feedback:

- **GitHub Discussions**: Feature requests and ideas
- **Issues**: Bug reports and feature requests
- **Discord**: Real-time discussion (if available)
- **Surveys**: Periodic feedback collection

---

## FAQ

### Q: When will feature X be implemented?

**A**: Check the roadmap for target phase. If not listed, create an issue to propose it.

### Q: Can I suggest a feature?

**A**: Yes! Create a GitHub/Gitea issue with description. It may be added to future phases.

### Q: How long until 1.0.0?

**A**: Estimated Q2 2027 (planning to complete Phase 5).

### Q: Will old 0.x features work in 1.0.0?

**A**: See `documentation/API_STABILITY.md` for API compatibility guarantees.

### Q: How can I contribute to the roadmap?

**A**: Pick an issue from the current phase, ask to be assigned, and submit a PR.

### Q: What if I don't agree with the roadmap?

**A**: Create an issue to discuss! Community input shapes the roadmap.

---

## References

- **Current Phase Details**: `documentation/roadmap/phase-2-drone-framework.md`
- **API Stability**: `documentation/API_STABILITY.md`
- **Contributing**: `CONTRIBUTING.md`
- **Release Management**: `documentation/RELEASE.md`
