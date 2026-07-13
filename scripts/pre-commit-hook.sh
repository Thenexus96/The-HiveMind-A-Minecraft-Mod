#!/bin/bash
# Pre-commit hook for HiveMind
# This hook runs code quality checks before allowing commits
# To install: cp scripts/pre-commit-hook.sh .git/hooks/pre-commit && chmod +x .git/hooks/pre-commit

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "🔍 Running pre-commit checks..."
echo

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Track failures
FAILED=0

# 1. Check formatting with Spotless
echo -e "${YELLOW}▶ Checking code formatting...${NC}"
if ./gradlew spotlessCheck --quiet 2>/dev/null; then
  echo -e "${GREEN}✓ Code formatting OK${NC}\n"
else
  echo -e "${RED}✗ Code formatting issues found${NC}"
  echo "  Fix with: ./gradlew spotlessApply"
  FAILED=1
  echo
fi

# 2. Run Checkstyle
echo -e "${YELLOW}▶ Running Checkstyle...${NC}"
if ./gradlew checkstyleMain --quiet 2>/dev/null; then
  echo -e "${GREEN}✓ Checkstyle OK${NC}\n"
else
  echo -e "${RED}✗ Checkstyle violations found${NC}"
  echo "  Check: ./gradlew checkstyleMain"
  FAILED=1
  echo
fi

# 3. Run SpotBugs
echo -e "${YELLOW}▶ Running SpotBugs...${NC}"
if ./gradlew spotbugsMain --quiet 2>/dev/null; then
  echo -e "${GREEN}✓ SpotBugs OK${NC}\n"
else
  echo -e "${RED}✗ SpotBugs issues found${NC}"
  echo "  Check: ./gradlew spotbugsMain"
  FAILED=1
  echo
fi

# 4. Run unit tests
echo -e "${YELLOW}▶ Running unit tests...${NC}"
if ./gradlew test --quiet 2>/dev/null; then
  echo -e "${GREEN}✓ Unit tests passed${NC}\n"
else
  echo -e "${RED}✗ Unit tests failed${NC}"
  FAILED=1
  echo
fi

# Summary
if [ $FAILED -eq 0 ]; then
  echo -e "${GREEN}✓ All pre-commit checks passed!${NC}"
  exit 0
else
  echo -e "${RED}✗ Some checks failed. Fix the issues above and try again.${NC}"
  echo -e "${YELLOW}  Tip: Run ./scripts/check-ci.sh to see all issues${NC}"
  exit 1
fi

