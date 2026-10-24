# Test Plan — My Demo App (Android & iOS)

| | |
|---|---|
| **Product** | Sauce Labs *My Demo App* (React Native), v1.3.0 — mobile e-commerce store |
| **Backend under API test** | DummyJSON store API (`https://dummyjson.com`) — products, auth, carts |
| **Author** | Serhii Korniiuk |
| **Status** | Active |

## 1. Objectives

- Confirm that the critical purchase flow (browse → cart → login → checkout → order) works on both platforms.
- Cover new and existing functionality with automated UI, API and E2E tests that run on every change.
- Verify platform specifics: app lifecycle, deep links, runtime permissions, location, system interruptions.
- Give the team a fast quality signal in CI and a readable report (Allure) for release decisions.

## 2. Scope

### In scope

| Area | Level | Platforms |
|---|---|---|
| Login & validation | UI | Android, iOS |
| Catalog: list, sort, product details, scroll | UI | Android, iOS |
| Cart: add, quantity, remove | UI | Android, iOS |
| Checkout (address, payment, review, confirmation) | E2E | Android, iOS |
| App lifecycle: background/foreground, terminate/relaunch | Platform | Android, iOS |
| Deep links | Platform | Android, iOS |
| Runtime permissions + geolocation | Platform | Android (automated), iOS (manual) |
| System interruptions (notification shade) | Platform | Android (automated), iOS (manual) |
| Products, auth (AuthN/AuthZ), cart totals, JSON schema | API | — |

### Out of scope

- Real payment processing (demo app accepts any valid-format card).
- Performance/load testing, penetration testing.
- Real devices and device farms (planned, see §10).
- Push notifications sent by a backend — the demo app has no push integration; covered by checklist for manual testing.

## 3. Approach

**Test pyramid.** API tests are the fastest and most stable layer, so business rules (pagination, totals, auth) are checked there. UI tests check what the user sees; E2E covers the single most valuable journey.

**One code base for two platforms.** Page Objects use accessibility ids that are identical on Android and iOS. Platform differences live in `BasePage` / `MobileActions`, and platform-only tests are tagged with `android-only` / `ios-only` groups.

**Test independence.** Each test opens its own Appium session with a clean app state (`noReset=false`), so tests do not depend on order and can be re-run alone.

**Test data.** Static users for the demo app (`bob@example.com` valid, `alice@example.com` locked), generated addresses/cards (Datafaker) so each run uses fresh data.

**Flakiness policy.** CI retries a failed test once (`retry.count=1`). Allure shows every attempt, so a test that passes only on retry is visible and gets investigated, not hidden.

## 4. Test types

| Type | Tooling | Where |
|---|---|---|
| Smoke (`smoke` group) | TestNG groups | every push / PR |
| Regression (`regression` group) | TestNG groups | every push + nightly |
| API | REST Assured, JSON schema validator | every push |
| E2E | Appium + TestNG | every push |
| Exploratory / platform guidelines | Manual, [checklist](checklist.md) | before release |

## 5. Environments

| Platform | Device | OS | Runner |
|---|---|---|---|
| Android | Emulator `pixel_6`, `google_apis`, x86_64 | API 30 (Android 11), API 34 (Android 14) | GitHub Actions `ubuntu-latest` |
| iOS | Newest available iPhone simulator | Latest iOS on the runner image | GitHub Actions `macos-15-intel` |
| API | — | — | GitHub Actions `ubuntu-latest` |

Automation stack: Java 21, Maven, Appium 3 (UiAutomator2, XCUITest), TestNG, REST Assured, AssertJ, Allure.

## 6. Entry / exit criteria

**Entry:** build of the app is available; Appium server and devices start; smoke API tests pass.

**Exit (release candidate):**
- 100 % of `smoke` tests passed on all platforms.
- ≥ 95 % of `regression` tests passed; every failure is analysed and has a bug or a known-issue note.
- No open Blocker/Critical defects.
- Checklist for platform guidelines passed for the release scope.

## 7. Process (STLC)

| STLC phase | What happens in this project |
|---|---|
| Requirement analysis | User stories → test conditions, risks (§9) |
| Test planning | This document |
| Test case design | [test-cases.md](test-cases.md), [checklist.md](checklist.md) |
| Environment setup | CI workflow, emulators/simulators, `scripts/` |
| Test execution | GitHub Actions on push, PR, nightly, manual dispatch |
| Defect reporting | GitHub Issues using the [bug report template](bug-report-template.md) |
| Test closure | Allure report on GitHub Pages: pass rate, trends, failures by feature |

In the SDLC the tests act as a quality gate: a pull request cannot be merged while the smoke suite is red.

## 8. Defect management

- Every failed test is triaged: **product bug**, **test bug**, or **environment issue**.
- Product bugs are filed as GitHub Issues with the template (steps, expected/actual, device, OS, build, evidence from Allure: screenshot + page source + Appium log).
- Severity: Blocker / Critical / Major / Minor / Trivial. Priority is set with the product owner.

## 9. Risks

| Risk | Impact | Mitigation |
|---|---|---|
| Emulators/simulators on shared CI runners are slower and less stable | Flaky failures | Generous timeouts, one retry, Appium log as artifact |
| Locators change in a new app build | Many tests fail at once | Locators live only in Page Objects; accessibility ids preferred over XPath |
| iOS CI time (WebDriverAgent build) | Slow feedback | Run iOS in parallel with Android; smoke only on PR if needed |
| Public API (DummyJSON) unavailable | API job red | Failure triaged as environment; base URL configurable |

## 10. Next steps

- Run on real devices via a device farm (BrowserStack / Sauce Labs) using the same capabilities layer.
- Parallel execution on several emulators (ThreadLocal driver is already in place).
- Visual checks for key screens.
