#!/usr/bin/env bash
# Downloads Sauce Labs "My Demo App" (React Native) builds into ./apps
# Usage: scripts/download-apps.sh [android|ios|all]
set -euo pipefail

VERSION="${MY_DEMO_APP_VERSION:-v1.3.0}"
BASE_URL="https://github.com/saucelabs/my-demo-app-rn/releases/download/${VERSION}"
ANDROID_FILE="${ANDROID_APK_FILE:-Android-MyDemoAppRN.1.3.0.build-244.apk}"
IOS_FILE="${IOS_SIM_ZIP_FILE:-iOS-Simulator-MyRNDemoApp.1.3.0-162.zip}"

TARGET="${1:-all}"
APPS_DIR="$(cd "$(dirname "$0")/.." && pwd)/apps"
mkdir -p "$APPS_DIR"

download_android() {
  echo "Downloading Android app: ${ANDROID_FILE}"
  curl -fsSL --retry 3 -o "$APPS_DIR/android.apk" "${BASE_URL}/${ANDROID_FILE}"
  ls -lh "$APPS_DIR/android.apk"
}

download_ios() {
  echo "Downloading iOS simulator app: ${IOS_FILE}"
  local tmp
  tmp="$(mktemp -d)"
  curl -fsSL --retry 3 -o "$tmp/ios.zip" "${BASE_URL}/${IOS_FILE}"
  unzip -q "$tmp/ios.zip" -d "$tmp/unzipped"
  local app
  app="$(find "$tmp/unzipped" -maxdepth 3 -type d -name '*.app' | head -n 1)"
  if [[ -z "$app" ]]; then
    echo "No .app bundle found in ${IOS_FILE}" >&2
    exit 1
  fi
  rm -rf "$APPS_DIR/MyDemoApp.app"
  mv "$app" "$APPS_DIR/MyDemoApp.app"
  rm -rf "$tmp"
  ls -ld "$APPS_DIR/MyDemoApp.app"
}

case "$TARGET" in
  android) download_android ;;
  ios) download_ios ;;
  all) download_android; download_ios ;;
  *) echo "Unknown target: $TARGET (use android|ios|all)" >&2; exit 1 ;;
esac
