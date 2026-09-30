# WareVista

**Warehouse + Vista** — a Smart Agricultural Warehouse Monitoring and
Analytics System. WareVista is a native Android app that lets
warehouse admins and staff record crop purchases and sales, track
live stock, review history and reports, and manage subscriptions —
backed by a Google Apps Script + Google Sheets backend.

## Features

- **Role-based dashboards** — separate flows for **Staff** and
  **Admin** users after login.
- **Purchase entry** — record crop purchases from farmers (crop,
  quantity, rate, auto-calculated total, payment method, date,
  remarks).
- **Sales entry** — record sales to buyers, with a live available-stock
  check per crop, **Cash** payments, and an **Online** demo-QR payment
  flow (scan → poll for confirmation → save).
- **Live Stock** — current stock levels per crop for the warehouse.
- **History & Reports** — purchase history, sales history, a history
  summary (today vs. total records), and reports with crop/date
  filters.
- **Warehouse & staff management** (Admin) — create warehouses, add
  staff accounts, assign credentials.
- **Subscriptions** — **Free / Gold / Platinum** plans that gate
  access to History and Live Stock and control how many warehouses
  an account can run.
- **Ask WareVista** — an in-app assistant screen for natural-language
  questions about the warehouse data.
- **Biometric verification** for sensitive actions.
- **Multi-language UI** — English, Gujarati (ગુજરાતી), and Hindi
  (हिन्दी), switchable at login.

## Tech stack

| Layer | Technology |
|---|---|
| Client | Android (Java), XML layouts, single-module Gradle project |
| Networking | [Volley](https://developer.android.com/training/volley) (GET/POST against a single backend endpoint) |
| Backend | Google Apps Script Web App (one deployment, dispatched by a `module=` operation parameter) |
| Data store | Google Sheets |
| Analytics | Power BI dashboards on top of the Sheets data |
| Payments | Razorpay Checkout SDK, plus an in-app demo/sandbox QR payment mode |
| Other | ZXing (QR), AndroidX Biometric, Lottie (animations), Media3 ExoPlayer (intro videos) |

## Project structure

```
WareVista/
├── app/
│   ├── src/main/java/com/example/warevista/   # Activities & business logic
│   ├── src/main/res/
│   │   ├── layout/                            # Screen layouts (XML)
│   │   ├── values/, values-gu/, values-hi/    # Strings: English, Gujarati, Hindi
│   │   ├── raw/                               # Lottie JSON animations, intro videos
│   │   └── drawable/                          # Icons, images, logo
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle/
```

Key screens (Activities), roughly in the order a user encounters
them: `SplashActivity` → `LoginActivity` → `HomeActivity` (staff) /
`AdminDashboardActivity` (admin) → `PurchaseActivity`,
`SalesActivity`, `StockActivity`, `HistoryActivity` (→
`PurchaseHistoryActivity`, `SalesHistoryActivity`),
`ReportsActivity`, `CreateWarehouseActivity`, `ManageStaffActivity`,
`SubscriptionActivity` (→ `SubscriptionPaymentActivity` →
`SubscriptionPaymentConfirmationActivity`), `QrPaymentActivity`,
`BiometricVerificationActivity`, `AskWareVistaActivity`.

## Requirements

- Android Studio (recent stable release)
- JDK 11
- Gradle 9.5 (bundled via the Gradle wrapper — no separate install
  needed)
- Android SDK: **minSdk 24**, **targetSdk / compileSdk 36**
- A physical device or emulator running Android 7.0 (API 24) or
  higher

## Getting started

1. Clone the repository:
   ```bash
   git clone https://github.com/khushi-project-space/WareVista.git
   cd WareVista
   ```
2. Open the project root in Android Studio and let it sync Gradle
   (or build from the command line):
   ```bash
   ./gradlew assembleDebug
   ```
3. Run on an emulator or connected device:
   ```bash
   ./gradlew installDebug
   ```
   or use Android Studio's **Run** button.

The app talks to a Google Apps Script backend configured directly in
the source (see `BASE_URL` in the Activity classes). No local server
setup is required to run the app against the existing backend.

## Permissions

The app requests:
- `INTERNET` — all backend communication
- `RECORD_AUDIO` — voice input for the Ask WareVista assistant
- `USE_BIOMETRIC` — biometric verification screen

## Localization

All user-facing strings are maintained in three resource sets:
`values/` (English, default), `values-gu/` (Gujarati), and
`values-hi/` (Hindi). The in-app language switcher persists the
chosen language across sessions.

## License

No license file is currently included in this repository. Add one
(e.g. MIT, Apache-2.0) if you intend for others to reuse this code.
