# Implementation Plan: Firebase Firestore & Authentication Integration

Enable Firebase Firestore and authentication dependencies in `app/build.gradle.kts`, configure Firestore for real-time requirement/quote synchronization alongside the authoritative PostgreSQL backend, and establish Credential Manager One-Tap authentication.

## User Review Required

> [!IMPORTANT]
> **Authentication & Provisioning Note**:
> In the Google AI Studio environment, backend-provisioned Firebase Auth directly supports Google Sign-In via Jetpack Credential Manager (including Google One-Tap). For phone-based workflows (such as Indian fabricator/customer WhatsApp and mobile contacts), the phone number is verified during profile onboarding and linked to the user's authenticated record.
> 
> **Dual Backend Strategy**:
> PostgreSQL at `parkzone.in/cjfabrication` remains the authoritative database for enterprise transactions, vendor assignments, and financial audits. Firestore acts as the real-time client sync layer for live RFQ status, quotation notifications, and mobile offline caching.

---

## Proposed Changes

### Build Configuration (`app/build.gradle.kts`)

- Uncomment Firestore dependency:
  - `implementation(libs.firebase.firestore)`
- Uncomment Firebase Authentication and Credential Manager dependencies to support One-Tap authentication and security rules:
  - `implementation(libs.firebase.auth)`
  - `implementation(libs.androidx.credentials)`
  - `implementation(libs.androidx.credentials.play.services)`
  - `implementation(libs.googleid)`
- Keep all Room, Retrofit, Coroutines, and Moshi dependencies intact so existing PostgreSQL API clients continue to function.

---

### Backend & Firebase Provisioning

- Trigger Firebase setup handshake via `set_up_firebase` with `platform: "android"` and `requestedProducts: ["firestore"]`.
- Initialize `firestore.rules` and `firebase-blueprint.json` defining collection schemas for:
  - `users/{userId}`: User profile, role (Customer/Vendor/Admin), phone number, and preferences.
  - `rfq_sync/{rfqId}`: Real-time mirror of active RFQs, status badges, and milestone timestamps.
  - `quotes_sync/{quoteId}`: Real-time vendor quote notifications gated by vendor/customer authorization rules.

---

### Data Layer Synchronization (`app/src/main/java/com/example/data/`)

- Create `FirestoreSyncManager.kt`:
  - Handles snapshot listeners for live updates on active customer RFQs and assigned vendor quotes.
  - Caches status changes and milestone notifications locally.
  - Coordinates dual-write / sync: write requests route transactionally to the PostgreSQL backend first, with Firestore emitting real-time telemetry updates.
- Create `AuthManager.kt`:
  - Jetpack `CredentialManager` integration using `GetSignInWithGoogleOption` for smooth one-tap sign-in.
  - Capture and link the user's mobile/phone number during profile onboarding for WhatsApp handoffs and vendor communication.

---

### Verification & Testing

- Run `compile_applet` to confirm Gradle dependency resolution and successful compilation with Firestore and Credential Manager.
- Run `gradle :app:testDebugUnitTest` to ensure all existing Robolectric tests (RBAC, calculations, WhatsApp link generation, orders) pass without regressions.
- Verify that Firestore security rules enforce strict user-isolation.

---

## Verification Plan

### Automated Tests
1. `compile_applet`: Verify clean compilation with updated Gradle dependencies.
2. `gradle :app:testDebugUnitTest`: Run existing 14 Robolectric & unit tests.
3. Validate Firestore rule syntax and compilation.

### Manual Verification Steps
1. Verify `app/build.gradle.kts` has `libs.firebase.firestore` and auth libraries cleanly uncommented.
2. Verify app launches and handles authentication and RFQ tracking gracefully.
