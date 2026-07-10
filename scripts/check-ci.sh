#!/bin/bash
# HiveMind local CI checker - runs the same checks as CI pipelines
# Usage: ./scripts/check-ci.sh

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

echo "=========================================="
echo "HiveMind Local CI Check"
echo "=========================================="
echo

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Counters
PASSED=0
FAILED=0

run_check() {
  local name=$1
  local command=$2

  echo -e "${YELLOW}▶ Running: $name${NC}"
  if eval "$command"; then
    echo -e "${GREEN}✓ $name passed${NC}\n"
    ((PASSED++))
  else
    echo -e "${RED}✗ $name failed${NC}\n"
    ((FAILED++))
  fi
}

# Ensure gradlew is executable
if [ ! -x gradlew ]; then
  chmod +x gradlew
fi

# Run checks
run_check "Unit Tests" "./gradlew --no-daemon --console=plain --warning-mode all test"
run_check "Build" "./gradlew --no-daemon --console=plain --warning-mode all build"

echo "=========================================="
echo "Summary"
echo "=========================================="
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo "=========================================="

if [ $FAILED -eq 0 ]; then
  echo -e "${GREEN}All checks passed! You can safely push.${NC}"
  exit 0
else
  echo -e "${RED}Some checks failed. Please fix and try again.${NC}"
  exit 1
fi

