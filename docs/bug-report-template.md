# Bug Report Template

The same template is available as a GitHub issue form: **Issues → New issue → Bug report**.

```
Title: [Platform][Area] Short description of the problem
       e.g. [iOS][Checkout] "To Payment" button is hidden by keyboard on iPhone SE

Severity: Blocker | Critical | Major | Minor | Trivial
Priority: High | Medium | Low

Environment
- App build: My Demo App RN 1.3.0 (build 244 / 162)
- Device: Pixel 6 emulator / iPhone 16 simulator / real device model
- OS: Android 14 (API 34) / iOS 18.x
- Network: Wi-Fi / 4G / offline
- User: bob@example.com

Preconditions
- Fresh install, user is logged out

Steps to reproduce
1. ...
2. ...
3. ...

Expected result
- ...

Actual result
- ...

Reproducibility: Always | Intermittent (3/5) | Once

Evidence
- Screenshot / video
- Allure report link (failed test, screenshot, page source)
- Appium / logcat / Xcode console log

Notes
- Works on: Android 11 (API 30)
- Related test: CartTest.addProductWithQuantity (TC-CART-002)
```

## Severity guide

| Severity | Meaning | Example |
|---|---|---|
| Blocker | Main flow impossible, no workaround | Checkout crashes the app |
| Critical | Key feature broken, workaround is hard | Cart loses items after background |
| Major | Feature works incorrectly | Sort by price is wrong |
| Minor | Small functional or UI issue | Error text truncated on small screen |
| Trivial | Cosmetic | Typo in label |
