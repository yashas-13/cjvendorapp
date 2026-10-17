# CJ FABRICATIONS — ANDROID & PWA SPECIFICATION & BUILD GUIDE

This guide details the Android native client and PWA configuration for the CJ Fabrications marketplace.

---

## 1. Native Android Application Architecture
- **Language:** Kotlin 2.2+
- **UI Framework:** Jetpack Compose with Material Design 3 (M3)
- **Architecture:** Clean Architecture + MVVM (Model-View-ViewModel)
- **Local Persistence:** Android Jetpack Room Database (`AppDatabase`) with entities:
  - `RfqEntity`, `RfqItemEntity`, `VendorQuoteEntity`
- **Networking:** Retrofit 2 + Moshi Converter + OkHttp logging interceptor
- **Theme & Brand:** Industrial Metalwork Palette:
  - Forge Amber Primary (`#D97706` / `#F59E0B`)
  - Structural Steel Slate (`#64748B` / `#334155`)
  - Deep Iron Background (`#0B0F17`)
- **Adaptive Icon:** Custom adaptive vector icon with structural I-beam, gate arch, and welding flare.

### Building & Running Android Tests Locally
```bash
# Run all unit tests and Robolectric tests
gradle :app:testDebugUnitTest

# Assemble Debug APK
gradle :app:assembleDebug
```
The generated APK will be located in:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 2. Progressive Web App (PWA) Manifest & Service Worker
For web clients at `https://parkzone.in/cjfabrication/`:

### `manifest.webmanifest`
```json
{
  "name": "CJ Fabrications Marketplace",
  "short_name": "CJ Fabrication",
  "start_url": "/cjfabrication/",
  "display": "standalone",
  "background_color": "#0B0F17",
  "theme_color": "#111827",
  "icons": [
    {
      "src": "/cjfabrication/icons/icon-192.png",
      "sizes": "192x192",
      "type": "image/png"
    },
    {
      "src": "/cjfabrication/icons/icon-512.png",
      "sizes": "512x512",
      "type": "image/png"
    }
  ]
}
```

### Mobile Capabilities
- **File Chooser Support:** Native support for attaching engineering CAD drawings, site measurement PDFs, and reference photographs.
- **WhatsApp Intent Dispatch:** Direct intent handoff launching the native WhatsApp application on Android devices with pre-formatted message parameters.
- **Back Handler:** Hardware and gesture back-button handling across all sub-screens (Design Detail, RFQ Tracking, Custom Quote form).
