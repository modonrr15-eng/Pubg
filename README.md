# Axiom PUBG Companion (Android)

A native Android companion dashboard with:
- Android battery percentage and battery temperature
- System memory snapshot
- Sensitivity preset profiles and editable camera / ADS / gyroscope values
- Exportable preset text for manual entry in PUBG settings

## Important limits
Android does not provide an ordinary third-party app with reliable access to PUBG Mobile's internal FPS counter or per-game temperature. This project reports **device-level** metrics only; it does not claim to measure in-game FPS. It does not access game memory, draw ESP, inject code, automate aiming, or bypass anti-cheat.

## Build
1. Install Android Studio (current stable) and Android SDK Platform 35.
2. Open this folder in Android Studio.
3. Allow Gradle sync to finish.
4. Connect an Android device with USB debugging enabled, or use an emulator.
5. Select `app` and press Run.

To create an APK: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
The debug APK is normally generated at `app/build/outputs/apk/debug/app-debug.apk`.

## Sensitivity presets
The values are illustrative starting points, not guaranteed best settings. Select a profile, tune the sliders, export the text, then enter the values manually in PUBG settings. Adjust gradually and test in Training Ground.

## Notes
- `BATTERY_STATS` is a protected permission and is not used by this app; Android battery readings are obtained through public APIs.
- Memory usage is a broad device snapshot, not PUBG-specific process memory.
- Temperature is the battery sensor temperature where the device exposes it.
