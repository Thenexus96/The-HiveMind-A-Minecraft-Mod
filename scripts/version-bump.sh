#!/bin/bash
# Version bump script for HiveMind
# Usage: ./scripts/version-bump.sh [version] [minecraft-version]
# Example: ./scripts/version-bump.sh 0.3.0          # Keep Minecraft version
# Example: ./scripts/version-bump.sh 0.3.0 1.21     # Update Minecraft version

set -e

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

# Color codes
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Parse arguments
NEW_VERSION=$1
NEW_MC_VERSION=$2

# Validate inputs
if [ -z "$NEW_VERSION" ]; then
  echo -e "${RED}Error: Version argument required${NC}"
  echo "Usage: $0 [version] [minecraft-version]"
  echo "Example: $0 0.3.0        # Keep current Minecraft version"
  echo "Example: $0 0.3.0 1.21   # Update Minecraft version"
  exit 1
fi

# Validate version format
if ! [[ $NEW_VERSION =~ ^[0-9]+\.[0-9]+\.[0-9]+(-[a-zA-Z0-9]+)?$ ]]; then
  echo -e "${RED}Error: Invalid version format${NC}"
  echo "Expected: X.Y.Z or X.Y.Z-alpha/beta/rc"
  echo "Got: $NEW_VERSION"
  exit 1
fi

# Read current gradle.properties
GRADLE_PROPS="gradle.properties"
CURRENT_MOD_VERSION=$(grep "^mod_version=" $GRADLE_PROPS | cut -d'=' -f2)
CURRENT_MC_VERSION=$(echo "$CURRENT_MOD_VERSION" | sed 's/.*+//')

# Determine new Minecraft version
if [ -z "$NEW_MC_VERSION" ]; then
  FINAL_MC_VERSION=$CURRENT_MC_VERSION
  echo "Using current Minecraft version: $CURRENT_MC_VERSION"
else
  FINAL_MC_VERSION=$NEW_MC_VERSION
  echo "Updating Minecraft version to: $NEW_MC_VERSION"
fi

# Create new version string
NEW_MOD_VERSION="${NEW_VERSION}+${FINAL_MC_VERSION}"

echo
echo -e "${YELLOW}Version Change${NC}"
echo "Current: $CURRENT_MOD_VERSION"
echo "New:     $NEW_MOD_VERSION"
echo

# Prompt for confirmation
read -p "Continue? (y/n) " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
  echo "Cancelled."
  exit 1
fi

# Update gradle.properties
echo "Updating gradle.properties..."
sed -i "s/^mod_version=.*/mod_version=$NEW_MOD_VERSION/" $GRADLE_PROPS
echo -e "${GREEN}✓ gradle.properties updated${NC}"

# Update README.md status line
if grep -q "Version: $CURRENT_MOD_VERSION" README.md; then
  sed -i "s/Version: $CURRENT_MOD_VERSION/Version: $NEW_MOD_VERSION/" README.md
  echo -e "${GREEN}✓ README.md updated${NC}"
fi

# Verify changes
echo
echo -e "${YELLOW}Verify Changes${NC}"
echo "gradle.properties:"
grep "mod_version=" $GRADLE_PROPS
echo

# Show next steps
echo -e "${GREEN}✓ Version bump complete!${NC}"
echo
echo "Next steps:"
echo "1. Update CHANGELOG.md"
echo "2. Commit: git add gradle.properties CHANGELOG.md README.md"
echo "3. Commit: git commit -m \"Docs: prepare release $NEW_MOD_VERSION\""
echo "4. Tag: git tag -a v$NEW_VERSION -m \"Release HiveMind v$NEW_VERSION\""
echo "5. Push tag: git push origin v$NEW_VERSION"
echo

