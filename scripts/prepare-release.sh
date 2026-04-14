#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?Usage: prepare-release.sh <version>}"

mkdir -p iglog/results
mkdir -p iglog/templates
cp README.md iglog/README.md
cp gitclient/target/iglog.jar iglog/iglog.jar
cp chr-helper/target/ig-chr-helper.jar iglog/ig-chr-helper.jar
cp lib/ig-summary.py iglog/ig-summary.py
cp lib/summary_extract.py iglog/summary_extract.py
cp lib/summary_render.py iglog/summary_render.py
cp lib/templates/summary.html iglog/templates/summary.html

zip -r iglog.zip iglog
