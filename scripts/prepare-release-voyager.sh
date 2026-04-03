#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:?Usage: prepare-release-voyager.sh <version>}"

mkdir -p iglog/results
cp README.md iglog/README.md
cp gitclient/target/iglog.jar iglog/iglog.jar
cp gitclient/src/main/resources/instrument.yml iglog/instrument.yml

zip -r iglog-voyager.zip iglog
