# java-appium-mobile

[![Mobile Tests](https://github.com/SerhiiKorniiukAQA/java-appium-mobile/actions/workflows/mobile-tests.yml/badge.svg)](https://github.com/SerhiiKorniiukAQA/java-appium-mobile/actions/workflows/mobile-tests.yml)
[![Allure Report](https://img.shields.io/badge/Allure-report-orange)](https://serhiikorniiukaqa.github.io/java-appium-mobile/)
![Java](https://img.shields.io/badge/Java-21-blue)
![Appium](https://img.shields.io/badge/Appium-3-purple)

Mobile test automation framework for **Android & iOS** built with **Java, Appium, TestNG, REST Assured and Allure**.
Tests run on Android emulators and iOS simulators in **GitHub Actions**, and the combined Allure report is published to GitHub Pages.

**App under test:** [Sauce Labs My Demo App (React Native)](https://github.com/saucelabs/my-demo-app-rn), an open-source demo store with catalog, cart, login and checkout.
**API under test:** [DummyJSON](https://dummyjson.com), a demo store backend (products, auth, carts).

## What is covered

| Layer | Tests | Examples |
|---|---|---|
| Mobile UI | Login, catalog, cart | validation messages, sorting, quantity, empty cart |
| E2E | Full purchase | guest → cart → login → address → payment → order confirmation |
| Platform | Lifecycle, deep links, permissions, interruptions | background/foreground, terminate/relaunch, `mydemoapprn://` links, location permission + GPS, notification shade |
| API | Products, auth, carts | pagination, JSON schema, 404, AuthN/AuthZ with Bearer token, cart totals |
| Docs | [Test plan](docs/test-plan.md), [checklist](docs/checklist.md), [test cases](docs/test-cases.md), [bug report template](docs/bug-report-template.md) | |

## Tech stack

| Purpose | Tool |
|---|---|
| Language / build | Java 21 (release 17), Maven |
| Mobile automation | Appium 3, `java-client` 9 — UiAutomator2 (Android), XCUITest (iOS) |
| Test runner | TestNG (groups, data providers, retry listener) |
| API | REST Assured + JSON Schema Validator, Jackson |
| Assertions / data | AssertJ, Datafaker |
| Reporting | Allure (steps, screenshots and page source on failure, API request/response) |
| CI/CD | GitHub Actions: API job, Android matrix (API 30, 34), iOS simulator, report → GitHub Pages |

## Framework design

```
src/main/java/io/github/serhiikorniiuk/mobile
├── config/        Config (system property → env var → config.properties), Platform
├── driver/        DriverFactory (UiAutomator2Options / XCUITestOptions), DriverManager (ThreadLocal)
├── pages/         Page Objects shared by Android and iOS + AppHeader component
├── utils/         MobileActions ("mobile:" commands), Gestures (W3C actions)
├── api/           ApiClient (base spec), StoreApi (service layer), models (records)
└── data/          Users, Address, PaymentCard, TestDataFactory

src/test/java/io/github/serhiikorniiuk/mobile
├── base/          BaseMobileTest: session per test, evidence on failure
├── listeners/     RetryAnalyzer + RetryListener
├── ui/            LoginTest, CatalogTest, CartTest
├── e2e/           CheckoutE2ETest
├── platform/      AppLifecycleTest, DeepLinkTest, AndroidPlatformTest
└── api/           ProductsApiTest, AuthApiTest, CartApiTest
```

Key decisions:

- **One code base, two platforms.** The app uses the same accessibility ids on Android and iOS, so each screen has one Page Object. Platform differences (text reading, app id, deep link args) are isolated in `BasePage` and `MobileActions`.
- **Independent tests.** Every test gets a fresh Appium session and clean app data. Any test can run alone or in any order.
- **Platform features via `mobile:` commands** (`backgroundApp`, `terminateApp`, `deepLink`, `changePermissions`, `setGeolocation`, `openNotifications`). They are the stable API of the UiAutomator2/XCUITest drivers.
- **Platform-only tests** are tagged `android-only` / `ios-only` and excluded in the other suite (`testng-ios.xml`, `testng-android.xml`).
- **Honest retries.** CI retries a failed test once; Allure keeps every attempt, so flaky tests stay visible.
- **One report for all devices.** `scripts/tag-allure-results.sh` adds a `device` parameter to each result so the same test on API 30, API 34 and iOS appears as separate entries.

## Running locally

### Prerequisites

- JDK 17+ and Maven 3.9+
- Node.js 20.19+ / 22 and Appium 3:
  ```bash
  npm install -g appium
  appium driver install uiautomator2   # Android
  appium driver install xcuitest       # iOS, macOS only
  ```
- Android: Android Studio with an emulator (or a real device with USB debugging), `ANDROID_HOME` set
- iOS: macOS with Xcode and a simulator. On Windows, iOS runs only in CI.

### Steps

```bash
# 1. Download the app builds into ./apps
bash scripts/download-apps.sh android     # or: ios | all

# 2. Start an emulator, then Appium in another terminal
appium

# 3. Run
mvn test                # API tests (default profile, no device needed)
mvn test -Pandroid      # Android suite
mvn test -Pios          # iOS suite (macOS)

# Useful overrides
mvn test -Pandroid -Dandroid.udid=emulator-5554 -Dtest=LoginTest
mvn test -Pios -Dios.device.name="iPhone 16" -Dios.platform.version=18.2

# 4. Open the report
allure serve target/allure-results
```

All settings are in [`config.properties`](src/main/resources/config.properties) and can be overridden with `-Dkey=value` or an environment variable (`IOS_DEVICE_NAME`, `APPIUM_URL`, …).

## CI/CD

Workflow: [`.github/workflows/mobile-tests.yml`](.github/workflows/mobile-tests.yml)

| Trigger | When |
|---|---|
| `push`, `pull_request` | `main` |
| `schedule` | nightly regression, Mon–Fri 03:00 UTC |
| `workflow_dispatch` | manual run: `all`, `api`, `android`, `ios` |

```
api ───────────────┐
android (API 30) ──┤
android (API 34) ──┼──► report (merge Allure results) ──► GitHub Pages
ios (simulator) ───┘
```

- **Android:** `ubuntu-latest` with KVM + `reactivecircus/android-emulator-runner`.
- **iOS:** `macos-15-intel`; the newest iPhone simulator is picked and booted by `scripts/boot-ios-simulator.sh`.
- Artifacts: Allure results per device, Appium log on failure, the full HTML report.

## Author

**Serhii Korniiuk** — Automation QA Engineer
Related projects: [python-playwright](https://github.com/SerhiiKorniiukAQA/python-playwright) · [typescript-playwright](https://github.com/SerhiiKorniiukAQA/typescript-playwright)
