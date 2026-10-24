# Mobile Release Checklist

Legend: **A** — automated in this repo, **M** — manual check before release.

## Functional

| # | Check | Android | iOS |
|---|---|---|---|
| F1 | Login with valid credentials, logout | A | A |
| F2 | Validation for empty username / password, locked and unknown user | A | A |
| F3 | Catalog shows products with titles and prices | A | A |
| F4 | Sorting by name and price | A | A |
| F5 | Product details: price, quantity counter | A | A |
| F6 | Cart: add, change quantity, remove, empty state | A | A |
| F7 | Checkout: address → payment → review → confirmation, cart cleared | A | A |
| F8 | Required field validation on checkout forms | M | M |

## App lifecycle

| # | Check | Android | iOS |
|---|---|---|---|
| L1 | Background → foreground keeps screen and cart | A | A |
| L2 | Terminate → cold start opens catalog | A | A |
| L3 | Low memory / process killed by OS restores gracefully | M | M |
| L4 | Incoming call / alarm during checkout | M | M |
| L5 | Device lock/unlock on any screen | M | M |

## Platform integration

| # | Check | Android | iOS |
|---|---|---|---|
| P1 | Deep links open the right screen (product, cart, login) | A | A |
| P2 | Location permission granted → coordinates shown | A | M |
| P3 | Location permission denied → app does not crash, explains why permission is needed | M | M |
| P4 | Permission changed in system settings while app is in background | M | M |
| P5 | Notification shade / Control Center over the app | A | M |
| P6 | Push notification received in foreground, background and when app is killed; tap opens correct screen | M | M |
| P7 | Push permission prompt shown at a meaningful moment (not on first launch) | M | M |
| P8 | Camera permission for QR scanner | M | M |

## Platform guidelines (Google Material / Apple HIG)

| # | Check | Android | iOS |
|---|---|---|---|
| G1 | System Back button / back swipe works on every screen | M | M |
| G2 | Touch targets ≥ 48dp (Android) / 44pt (iOS) | M | M |
| G3 | Dark mode: text readable, no hard-coded colors | M | M |
| G4 | Dynamic font size / display size up to max: layout not broken | M | M |
| G5 | Screen reader (TalkBack / VoiceOver) reads controls with meaningful labels | M | M |
| G6 | Safe areas: notch / Dynamic Island / gesture bar do not cover content | M | M |
| G7 | Keyboard does not cover active input; correct keyboard type (email, number) | M | M |
| G8 | Rotation / split screen (if supported) | M | M |

## Network & device conditions

| # | Check | Android | iOS |
|---|---|---|---|
| N1 | Offline / airplane mode: clear error, retry works | M | M |
| N2 | Slow 3G: loaders shown, no duplicate orders | M | M |
| N3 | Low battery / power saving mode | M | M |
| N4 | Install over previous version (upgrade) keeps user session | M | M |
| N5 | Fresh install, uninstall leaves no data | M | M |
