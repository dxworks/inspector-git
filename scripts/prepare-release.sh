#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?Usage: prepare-release.sh <version>}"

mkdir -p iglog/results
cp README.md iglog/README.md
cp gitclient/target/iglog.jar iglog/iglog.jar
cp chr-helper/target/ig-chr-helper.jar iglog/ig-chr-helper.jar

zip -r iglog.zip iglog
