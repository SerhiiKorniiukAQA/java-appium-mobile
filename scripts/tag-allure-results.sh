#!/usr/bin/env bash
# Makes results from different devices unique before they are merged into one
# Allure report. Without it Allure treats the same test on Android API 30,
# Android API 34 and iOS as retries of one test (same historyId).
# Usage: scripts/tag-allure-results.sh <results-dir> <device-label>
set -euo pipefail
DIR="$1"
LABEL="$2"

shopt -s nullglob
for f in "$DIR"/*-result.json; do
  jq --arg label "$LABEL" '
      .historyId = ((.historyId // .fullName) + "-" + $label)
    | .parameters = ((.parameters // []) + [{"name": "device", "value": $label}])
    | .labels = ((.labels // []) + [{"name": "tag", "value": $label}])
  ' "$f" > "$f.tmp" && mv "$f.tmp" "$f"
done
echo "Tagged $(ls "$DIR"/*-result.json 2>/dev/null | wc -l) results with device=$LABEL"
