#!/usr/bin/env bash
# Picks the newest available iPhone simulator, boots it and exports its
# name / version / udid to GITHUB_ENV (or prints them when run locally).
set -euo pipefail

SIM="$(xcrun simctl list devices available -j | jq -r '
  .devices | to_entries
  | map(select(.key | test("SimRuntime.iOS-")))
  | map(select(.value | any(.name | startswith("iPhone"))))
  | sort_by(.key | capture("iOS-(?<maj>[0-9]+)-(?<min>[0-9]+)") | [(.maj | tonumber), (.min | tonumber)])
  | last
  | (.key | capture("iOS-(?<maj>[0-9]+)-(?<min>[0-9]+)") | "\(.maj).\(.min)") as $ver
  | .value | map(select(.name | startswith("iPhone"))) | first
  | [.name, $ver, .udid] | @tsv')"

IFS=$'\t' read -r SIM_NAME SIM_VERSION SIM_UDID <<< "$SIM"
echo "Selected simulator: ${SIM_NAME} (iOS ${SIM_VERSION}) ${SIM_UDID}"

xcrun simctl boot "$SIM_UDID" || true
xcrun simctl bootstatus "$SIM_UDID" -b

if [[ -n "${GITHUB_ENV:-}" ]]; then
  {
    echo "IOS_DEVICE_NAME=${SIM_NAME}"
    echo "IOS_PLATFORM_VERSION=${SIM_VERSION}"
    echo "IOS_UDID=${SIM_UDID}"
  } >> "$GITHUB_ENV"
fi
