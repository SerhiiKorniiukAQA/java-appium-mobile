# Test Cases

Every automated test has the case ID in its TestNG `description`, so the ID is visible in the Allure report.
Preconditions for all mobile cases: fresh install / clean app data, app is opened on the catalog screen.

## Mobile UI — Login

| ID | Title | Steps | Expected result | Priority | Automated |
|---|---|---|---|---|---|
| TC-LOGIN-001 | Valid login | 1. Menu → Log In 2. Enter `bob@example.com` / `10203040` 3. Tap Login | Catalog is opened; menu shows "Log Out" | High | `LoginTest.validLogin` |
| TC-LOGIN-002 | Empty username | Leave username empty, enter password, tap Login | "Username is required"; stays on login | High | `LoginTest.invalidLoginShowsError` |
| TC-LOGIN-003 | Empty password | Enter username, leave password empty, tap Login | "Password is required" | High | `LoginTest.invalidLoginShowsError` |
| TC-LOGIN-004 | Locked user | Login as `alice@example.com` | "Sorry, this user has been locked out." | High | `LoginTest.invalidLoginShowsError` |
| TC-LOGIN-005 | Unknown credentials | Login with not existing user | "Provided credentials do not match any user…" | Medium | `LoginTest.invalidLoginShowsError` |

## Mobile UI — Catalog

| ID | Title | Steps | Expected result | Priority | Automated |
|---|---|---|---|---|---|
| TC-CAT-001 | Products are listed | Open app | Products have non-empty title and price > 0 | High | `CatalogTest.catalogShowsProducts` |
| TC-CAT-002 | Sort by price ascending | Sort → Price - Ascending | Visible prices are in ascending order | Medium | `CatalogTest.sortByPriceAscending` |
| TC-CAT-003 | Sort by name descending | Sort → Name - Descending | Visible titles are in Z→A order | Medium | `CatalogTest.sortByNameDescending` |
| TC-CAT-004 | Product details | Tap first product | Price equals catalog price; quantity = 1 | High | `CatalogTest.productDetailsMatchCatalog` |
| TC-CAT-005 | Scroll catalog | Swipe up to the end of list | Products hidden on first screen are shown | Low | `CatalogTest.catalogIsScrollable` |

## Mobile UI — Cart

| ID | Title | Steps | Expected result | Priority | Automated |
|---|---|---|---|---|---|
| TC-CART-001 | Add to cart | Open product → Add To Cart → open cart | Badge = 1; cart has 1 row | High | `CartTest.addProductToCart` |
| TC-CART-002 | Quantity | Open product → "+" twice → Add To Cart | Badge = 3; cart quantity = 3 | High | `CartTest.addProductWithQuantity` |
| TC-CART-003 | Remove from cart | Add product → cart → Remove Item | Empty state with "Go Shopping"; badge hidden | High | `CartTest.removeProductFromCart` |

## End-to-End

| ID | Title | Steps | Expected result | Priority | Automated |
|---|---|---|---|---|---|
| TC-E2E-001 | Guest purchase | 1. Add product 2. Cart → Proceed To Checkout 3. Log in 4. Fill address 5. Fill payment 6. Review → Place Order 7. Continue Shopping | Confirmation screen shown; back on catalog; cart is empty | Blocker | `CheckoutE2ETest.guestCompletesPurchase` |

## Platform specifics

| ID | Title | Steps | Expected result | Platform | Automated |
|---|---|---|---|---|---|
| TC-PLAT-001 | Background / foreground | Add product → send app to background 3 s | Same screen; cart badge = 1 | Both | `AppLifecycleTest.cartSurvivesBackground` |
| TC-PLAT-002 | Terminate / relaunch | Terminate app → launch | State NOT_RUNNING → RUNNING_IN_FOREGROUND; catalog shown | Both | `AppLifecycleTest.terminateAndRelaunch` |
| TC-PLAT-003 | Deep link to product | Open `mydemoapprn://product-details/{id}` | Product screen with price | Both | `DeepLinkTest.deepLinkOpensProduct` |
| TC-PLAT-004 | Deep link to cart | Open `mydemoapprn://cart` | Cart screen, empty for new user | Both | `DeepLinkTest.deepLinkOpensCart` |
| TC-PLAT-005 | Deep link to login | Open `mydemoapprn://login` | Login screen | Both | `DeepLinkTest.deepLinkOpensLogin` |
| TC-PLAT-006 | Location permission | Grant location permission → set device location to Kyiv → open Geo Location | Latitude 50.45…, longitude 30.52… | Android | `AndroidPlatformTest.geoLocationWithGrantedPermission` |
| TC-PLAT-007 | Notification shade | Open notification shade → Back | App returns to foreground on catalog | Android | `AndroidPlatformTest.notificationShadeInterruption` |

## API (DummyJSON)

| ID | Title | Request | Expected result | Automated |
|---|---|---|---|---|
| TC-API-001 | Product list | `GET /products?limit=30` | 200; 30 products; total > 30 | `ProductsApiTest.productListIsReturned` |
| TC-API-002 | Pagination | `GET /products?limit=10&skip=10` | ids 11..20 | `ProductsApiTest.paginationWorks` |
| TC-API-003 | Schema | `GET /products/1` | Body matches `product-schema.json` | `ProductsApiTest.productMatchesSchema` |
| TC-API-004 | Not found | `GET /products/999999` | 404, message "not found" | `ProductsApiTest.unknownProductReturns404` |
| TC-API-005 | Field selection | `GET /products/1?select=title,price` | Only `id`, `title`, `price` | `ProductsApiTest.selectReturnsOnlyRequestedFields` |
| TC-API-006 | Category filter | `GET /products/category/smartphones` | All items have category `smartphones` | `ProductsApiTest.categoryFilter` |
| TC-API-007 | Login | `POST /auth/login` | 200; access + refresh tokens | `AuthApiTest.loginReturnsTokens` |
| TC-API-008 | Authorized request | `GET /auth/me` with Bearer token | 200; same user as login | `AuthApiTest.tokenAuthorizesProfileRequest` |
| TC-API-009 | Wrong password | `POST /auth/login` wrong password | 400; "Invalid credentials" | `AuthApiTest.wrongPasswordIsRejected` |
| TC-API-010 | No token | `GET /auth/me` without token | 401/403 | `AuthApiTest.profileWithoutTokenIsRejected` |
| TC-API-011 | Cart totals | `POST /carts/add` (2× id 1, 1× id 5) | Line total = price × qty; cart total = sum of lines | `CartApiTest.cartTotalsAreCalculated` |
