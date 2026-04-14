#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?Usage: prepare-release-voyager.sh <version>}"

mkdir -p iglog/results
mkdir -p iglog/templates
cp README.md iglog/README.md
cp gitclient/target/iglog.jar iglog/iglog.jar
cp gitclient/src/main/resources/instrument.yml iglog/instrument.yml
cp gitclient/src/main/resources/instrument.v2.yml iglog/instrument.v2.yml
cp lib/ig-summary.py iglog/ig-summary.py
cp lib/summary_extract.py iglog/summary_extract.py
cp lib/summary_render.py iglog/summary_render.py
cp lib/templates/summary.html iglog/templates/summary.html

zip -r iglog-voyager.zip iglog
