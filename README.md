<p align="center">
  <img src="assets/logo.png" width="128" alt="VibeGram logo" />
</p>

<h1 align="center">VibeGram</h1>

<p align="center">
  Fast, clean, and customizable Android Telegram client with Python plugins, advanced privacy, Material styling, and built-in productivity tools.
</p>

---

## Features

- **Vibe Tools**:
  - **Quick Reply Snippets**: create, search, and insert reusable message templates right from any chat or manage them from VibeGram settings.
  - **Per-Chat Private Notes**: attach local, private notes to any user, group, or channel (`ownerId` + `dialogId` scoped) with quick access from the chat and profile menus.
  - **Chat Insights & Diagnostics**: view dialog metadata, DC ID, user/chat flags, and loaded message/media breakdown with one-tap ID copy.
  - **Smart Auto-Trim & Cache Optimizer**: background cache inspection, one-tap cleanup, configurable automatic cache trimming threshold, and smooth scroll optimizations.
- **UI & Customization**:
  - Custom adaptive VibeGram icon, icon packs (`.icons`), launcher icon switcher, Pill Stack, glass-style message menus and surfaces, customizable bottom navigation and drawer layout.
- **Chats & Privacy**:
  - Local deleted-message and edit-history retention, ghost mode controls, regex message filters, double-tap/gesture actions, URL tracking parameter cleaner, and `.vibe` encrypted settings backup/restore.
- **Python Plugin Engine**:
  - Built-in Chaquopy Python 3.11 runtime with `com.exteragram.messenger.*` plugin compatibility bridge (`ClassAliases`), Xposed-style method hooking, and granular `PluginSinkGate` security controls.

---

## Building

1. Make sure **JDK 21+** and **Android SDK** (`compileSdk 37`, `minSdk 24`, `targetSdk 36`) are installed and configured (`ANDROID_HOME` or `local.properties`).
2. Initialize submodules (`git submodule update --init --recursive`) if building with native/Chaquopy sources.
3. Build a release or debug APK:

```bash
./gradlew :TMessagesProj:assembleAfatRelease
```

For fast Java/Kotlin iteration without native CMake/JNI compilation:

```bash
./gradlew :TMessagesProj:assembleAfatDebug -PNATIVE_TARGET=SKIP
```

---

## Architecture Notes

- VibeGram-specific modules live under `TMessagesProj/src/main/java/app/vibegram/` and `TMessagesProj/src/main/kotlin/app/vibegram/`.
- Legacy plugin API compatibility (`com.exteragram.messenger.*`) is preserved through `app.vibegram.plugins.ClassAliases` and lightweight bridge classes under `TMessagesProj/src/main/java/com/exteragram/messenger/`.
- Main configuration lives in `TMessagesProj/src/main/kotlin/app/vibegram/` (`VibeConfig.kt`, `AppearanceConfig.kt`, `ChatsConfig.kt`, `GeneralConfig.kt`, `UtilsConfig.kt`).

---

## License

Source code is licensed under GNU GPL v2 or later, following Telegram for Android licensing requirements.
