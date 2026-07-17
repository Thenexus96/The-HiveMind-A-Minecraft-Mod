# Feature Request & Technical Decision Templates

## Feature Request Template

Use this template when proposing a new feature:

```markdown
## Feature: [Clear Feature Name]

### Problem Statement

What problem does this feature solve? Why is it needed?

- **User Problem**: What user pain point does this address?
- **Gameplay Impact**: How does this improve gameplay?
- **Strategic Alignment**: How does this align with project vision?

### Proposed Solution

Describe your proposed approach:

- **Overview**: High-level description
- **User Experience**: How would players interact with this?
- **Technical Approach**: How would you implement it?

### Alternative Approaches

What other ways could this be solved?

- **Option A**: ...
- **Option B**: ...
- **Pros/Cons**: Compare approaches

### Scope & Complexity

- **Scope Size**: Small (< 1 week) / Medium (1-3 weeks) / Large (> 3 weeks)
- **Complexity**: Low / Medium / High
- **Estimated Effort**: X hours
- **Dependencies**: Other features/PRs needed?

### Suggested Phase

Which roadmap phase should this be in?

- [ ] Phase 2 (0.2.x) — Drone Framework
- [ ] Phase 3 (0.3.x) — Control Link
- [ ] Phase 4 (0.4.x) — Advanced Features
- [ ] Phase 5 (1.0.0) — Stability
- [ ] Future / Unknown

### API & Compatibility Impact

- **API Changes**: Will this require API changes?
- **Breaking Changes**: Will existing mods break?
- **Data Migration**: Is data migration needed?
- **Minecraft Versions**: Affects 1.20.1+?

### Success Criteria

How will we know this feature is complete and successful?

- [ ] Criterion 1
- [ ] Criterion 2
- [ ] Criterion 3

### Additional Context

Links, sketches, community discussion, etc.

- Related issues: #123, #456
- Relevant PRs: #789
- Community feedback: "Players want..."
- Mockups/sketches: [image link]
```

---

## Technical Decision Template

Use this for significant technical decisions:

```markdown
## Technical Decision: [Brief Title]

### Problem

What technical problem or decision are we facing?

- **Context**: Current state and why we need to decide
- **Constraints**: Limitations we must work within
- **Deadline**: When do we need to decide?

### Options Evaluated

#### Option A: [Approach Name]

**Pros**:
- Pro 1
- Pro 2

**Cons**:
- Con 1
- Con 2

**Estimate**: X hours of work
**Long-term Impact**: Positive / Negative / Neutral

#### Option B: [Approach Name]

(Same format as Option A)

#### Option C: [Approach Name]

(Same format as Option A)

### Recommendation

**Recommended**: Option [X]

**Reasoning**:
1. Primary reason
2. Secondary reason
3. Community feedback consideration

**Risks**:
- Potential risk 1
- Potential risk 2

### Implementation Plan

If approved, how will we implement this?

- **Step 1**: ...
- **Step 2**: ...
- **Step 3**: ...

**Timeline**: Estimated X weeks

### Rollback Plan

If this decision proves wrong, how do we revert?

- Can we rollback? Yes / No
- Effort to rollback: X hours
- Data migration needed: Yes / No

### Discussion

Open questions:

- [ ] Question 1
- [ ] Question 2

Feedback from team:

- Team member: "Comment here"
- Community: "Feedback here"

### Decision Record

**Decision**: Approved / Deferred / Rejected  
**Date**: YYYY-MM-DD  
**Decided By**: Team lead / Community vote / Author  
**Rationale**: Final explanation of decision

---
```

## Decision Log

Archive of significant technical decisions:

### Approved Decisions

| Decision | Date | Status | Details |
|----------|------|--------|---------|
| Use Fabric over Forge | 2025-01-01 | ✅ Active | Reasons: Simpler, lighter, active community |
| NBT for persistence | 2025-02-01 | ✅ Active | Alternative: Custom serialization |
| Google Java Style | 2026-01-01 | ✅ Active | Via Spotless + Checkstyle |

### Deferred Decisions

| Decision | Date | Status | Reason |
|----------|------|--------|--------|
| Mod API stability | 2026-03-01 | ⏳ Pending | Waiting for more community mods using API |

### Rejected Decisions

| Decision | Date | Status | Reason |
|----------|------|--------|--------|
| Custom entity renderer | 2025-06-01 | ❌ Rejected | Vanilla renderer sufficient, save effort |

---

## Reporting Your Decision

When making a decision:

1. **Document rationale**: Why this choice?
2. **Consider alternatives**: What else could work?
3. **Record decision**: Add to decision log
4. **Communicate**: Notify team/community
5. **Review periodically**: Was it the right call?

---

## Questions to Ask

Before making significant decisions, ask:

1. **Does it align with vision?** Does this support the project's long-term goals?
2. **Is it reversible?** Can we change course if needed?
3. **What are the tradeoffs?** Speed vs quality? Features vs polish?
4. **Who does it affect?** Mod developers? Players? Team?
5. **What's the cost of being wrong?** Can we afford a mistake here?
6. **What does the community think?** Have we sought feedback?

---

## Reference

- **Roadmap**: `documentation/ROADMAP.md`
- **API Stability**: `documentation/API_STABILITY.md`
- **Contributing**: `CONTRIBUTING.md`
