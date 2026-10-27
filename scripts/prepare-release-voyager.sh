#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?Usage: prepare-release-voyager.sh <version>}"

mkdir -p iglog/results
mkdir -p iglog/templates
cp README.md iglog/README.md
cp gitclient/target/iglog.jar iglog/iglog.jar
cp gitclient/src/main/resources/instrument.yml iglog/instrument.yml
cp gitclient/src/main/resources/instrument.v2.yml iglog/instrument.v2.yml

# The tool's version, from the release tag, in both manifests (V1 and V2)
for f in iglog/instrument.yml iglog/instrument.v2.yml; do
  awk -v v="$VERSION" '!/^version:/ {print} /^name:/ {print "version: " v}' "$f" > "$f.tmp"
  mv "$f.tmp" "$f"
done

cp lib/ig-summary.py iglog/ig-summary.py
cp lib/summary_extract.py iglog/summary_extract.py
cp lib/summary_render.py iglog/summary_render.py
cp lib/templates/summary.html iglog/templates/summary.html

zip -r iglog-voyager.zip iglog
