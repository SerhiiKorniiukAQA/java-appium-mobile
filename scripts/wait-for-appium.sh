#!/usr/bin/env bash
# Waits until Appium server answers on /status
set -euo pipefail
URL="${APPIUM_URL:-http://127.0.0.1:4723}/status"
for i in $(seq 1 60); do
  if curl -fs "$URL" > /dev/null; then
    echo "Appium is ready: $URL"
    exit 0
  fi
  sleep 2
done
echo "Appium did not start in time" >&2
exit 1
