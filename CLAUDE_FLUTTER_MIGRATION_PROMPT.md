# Claude માટે WareVista Flutter Migration Prompt

આ prompt આપતી વખતે Claude Code ને WareVista repository ના root folder માં ખોલો. તેને આખા project ને વાંચવાની અને files edit કરવાની access આપો.

```text
You are a senior Flutter engineer responsible for migrating the existing WareVista Android app to a complete, maintainable Flutter application.

## Goal
Migrate the existing WareVista app in this repository from native Android Java/XML to Flutter/Dart. Deliver a working Flutter app that preserves the existing product behavior, business rules, backend integration, language support, and important visual identity. This is a real migration, not a mockup, WebView wrapper, or a set of disconnected sample screens.

## First: inspect before changing anything
1. Read the repository structure and all relevant source files, not just the README or launcher screen. The current app is a native Android project with Java Activities, XML layouts/resources, Gradle configuration, and Android assets. Inspect `app/src/main/java/com/example/warevista/`, `app/src/main/res/layout/`, `app/src/main/res/values/strings.xml`, `values-gu/`, `values-hi/`, `res/drawable/`, `res/raw/`, `AndroidManifest.xml`, and Gradle files.
2. Build a private migration inventory of every screen, navigation path, API request/action, request/response field, form rule, plan restriction, payment state, local preference, and asset. Trace the actual code before documenting behavior. Look for differences between similarly named classes and commented-out/unfinished functionality.
3. Identify the existing Google Apps Script backend contract from the Java code. Keep the current backend and its payload/query parameter formats compatible unless the repository contains an explicit backend change. Do not invent endpoints, payload fields, credentials, or backend behavior. Centralize the existing endpoint/configuration instead of duplicating it.
4. Check the current git status and existing project conventions before edits. Preserve useful source/assets and do not delete the Android implementation until the Flutter app has been built and the migration is reviewable. Do not overwrite unrelated user changes.
5. After the inventory, write a concise `FLUTTER_MIGRATION_PLAN.md` and a screen/API/feature checklist. Then proceed with implementation; do not stop after writing the plan or ask me to approve routine choices. If a genuinely blocking backend fact is missing, keep the affected feature explicit and configurable, implement the rest, and document the exact missing information.

## Existing app scope to verify against source
The app appears to include these flows; confirm each in code and include any others you discover:
- Splash/intro video and login/session handling, with English, Gujarati, and Hindi language choices.
- Role-based paths for warehouse users and admin: admin dashboard, warehouse creation, staff management, subscription/warehouse limit handling, sign out, and biometric verification if implemented.
- Home/dashboard navigation and plan-gated features.
- Purchase entry, crop list/selection, supplier details, quantity/rate/total calculation, date, payment method, remarks, validation, and save result.
- Sales entry, available stock/crop loading, buyer details, calculation and validation, cash/online payment, demo QR creation/status polling, save result, and bill/PDF flow if implemented.
- Live stock and filters/insights; transaction history and purchase/sales history; reports and report filters.
- Subscription/plan display, pricing/warehouse limits, Razorpay checkout/payment result handling, and confirmation screens. Distinguish live Razorpay behavior from demo QR behavior. Never mark a payment successful based only on a client-side callback if the backend is the source of truth.
- Ask WareVista/assistant flow if implemented.
- Existing loading, empty, error, retry, success, and confirmation states, plus app back behavior.

## Migration requirements
### Flutter structure and quality
- Create a standard Flutter project in this repository without losing the existing Android project/assets/history. Keep the app buildable from a clearly documented Flutter root. If a Flutter SDK/project already exists, extend it rather than nesting another project.
- Use null-safe Dart, Material 3, a feature-first structure with clear presentation/domain/data separation where it helps, reusable widgets, typed models, and a single routing/navigation approach. Avoid one giant `main.dart` and avoid putting networking/business logic in widgets.
- Use a sensible, consistent state-management approach (prefer Riverpod unless an existing repository convention dictates otherwise) and typed API/repository/service layers. Add only dependencies needed by the implemented app and choose compatible stable versions available in the environment.
- Keep responsive layouts usable on common Android phone sizes, support keyboard/scroll behavior, system insets, accessible touch targets, semantic labels, and loading/error/empty states. Preserve the WareVista branding, colors, typography feel, logo, relevant illustrations/animations, and intro experience using the existing assets where appropriate.
- Do not turn the native app into a WebView. Do not make up backend data to make screens look complete. If the real API is unavailable during development, provide a clearly isolated mock/demo mode only when necessary and never silently substitute fake production behavior.

### Backend and data behavior
- Preserve the current API module names, HTTP methods, query parameters, JSON/form encoding, field names, auth/session values, and response parsing exactly as discovered in source. Create a central API client/config and repository methods. Handle timeouts, malformed responses, non-success responses, connectivity errors, and retries appropriately; avoid duplicate submissions and polling leaks.
- Keep secrets out of source control and client binaries. Search the existing code for embedded keys/secrets and report them; do not copy credentials into Flutter code. If an existing Apps Script URL is required, put it behind one clearly named configuration mechanism and preserve current behavior without printing secret values into logs. Do not claim that client-side obfuscation makes a secret safe.
- Preserve session and language persistence using appropriate Flutter storage. Do not store passwords or payment secrets in plain preferences. Clear appropriate session data on logout.
- Preserve business validation and plan entitlements as enforced by the current app/backend. The backend remains authoritative for payment, subscription, stock, and role/permission decisions.
- Treat demo payment as demo payment. Keep Razorpay checkout only if the existing implementation and available credentials/configuration support a valid integration. Do not fabricate successful payments, live keys, callback signatures, or server verification. Clearly surface missing payment setup in the final handoff.

### Localization and assets
- Support English (`en`), Gujarati (`gu`), and Hindi (`hi`) throughout all migrated screens, validation messages, dialogs, and navigation. Reuse/translate from existing Android string resources; do not leave major UI hard-coded in English. Provide a language selector and persist the choice.
- Migrate/reuse the existing WareVista logo, illustrations, Lottie JSON, and intro videos when their use is appropriate. Create a documented Flutter asset map and ensure every declared asset exists. Do not include generated Android build outputs as source assets.

### Android/device integrations
- Configure Android permissions and platform integration only for features actually used: Internet, microphone for the assistant if needed, biometrics, QR/barcode, media/PDF saving, and payment SDK. Keep permission prompts contextual and handle denial gracefully.
- Use maintained Flutter packages or platform channels for biometrics, Razorpay, QR, video, and PDF as appropriate. Verify Android Gradle/manifest/package setup. Do not leave dead Java Activity routes that make the new Flutter app launch incorrectly.
- Preserve the current Android application ID/package identity where feasible; inspect signing/build configuration and do not invent or expose signing credentials. If a platform integration cannot be migrated safely without account/server configuration, isolate it behind an interface and document what the owner must supply.

## Execution and verification
Work in small, complete vertical slices: app shell/theme/localization/navigation; login and session; admin/warehouse/staff/subscription; purchase/sales/payment; stock/history/reports; remaining assistant/device integrations. Keep the migration checklist updated as each slice is implemented.

Run the available Flutter formatting, static analysis, and Android debug build after implementation (and fix issues caused by this migration). If dependencies cannot be fetched or the Flutter SDK is unavailable, still finish source implementation and report the precise verification blocker; do not claim a successful build. Do not remove the original Android app until the Flutter build and migrated flows are verified.

## Completion criteria
- The Flutter app launches on Android and has a coherent navigation path through every implemented WareVista feature.
- All existing screens and meaningful states/actions are accounted for; no feature is silently dropped. Any intentionally unsupported/incomplete feature is listed with its reason and required follow-up.
- Forms, calculations, validation, filters, role/plan access, persistence, and backend calls match the existing behavior.
- English, Gujarati, and Hindi work across the migrated UI.
- No fake successful API/payment behavior, hardcoded credentials, or accidental secret logging is introduced.
- Flutter assets/configuration and build instructions are documented; the migration checklist and API mapping are complete.

At the end, provide a concise handoff with: what was migrated, the Flutter project location, important architecture/configuration choices, build/run commands, verification actually completed and its result, remaining backend/payment/device setup, and any feature that could not be migrated. Be precise; do not claim tests/builds that you did not run.
```
