# Implementation Plan - Burçin'in Sihirli Gökkuşağı Maceraları (Android TV Game)

Build a complete Android TV game titled "Burçin'in Sihirli Gökkuşağı Maceraları" (Burçin's Magical Rainbow Adventures) using Jetpack Compose with full D-Pad navigation support for Android TV / Mi Box, a 2D Canvas game loop with smooth physics, magical rainbow theme, character avatar (Burçin in red skirt and white top with magical wand), helper cats, guardian owls, gem collectibles, levels, victory/defeat screens, and robust verification.

## User Review Required

> [!IMPORTANT]
> - Ensure LEANBACK / TV Manifest configurations (`android.software.leanback` and TV banner/launcher intent) are correctly set up so the app appears and behaves as a proper Android TV application.
> - D-Pad controls are fully mapped for TV directional pad (`DPAD_UP`, `DPAD_DOWN`, `DPAD_LEFT`, `DPAD_RIGHT`, `DPAD_CENTER` / `KEYCODE_ENTER`).

## Open Questions

- None. Requirements are fully specified for the Android TV magical rainbow platformer/adventure game.

## Proposed Changes

### Game Architecture & Navigation (`com.example.burcingame`)

#### [NEW] [Models.kt](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/java/com/example/burcingame/model/Models.kt)
- Define game state, level definitions, player state, collectible gems, obstacles (helper cats / guardian owls), score, lives, and screen navigation states (MainMenu, LevelSelect, Playing, Victory, Defeat).

#### [NEW] [GameEngine.kt](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/java/com/example/burcingame/engine/GameEngine.kt)
- Implement 2D Canvas physics, jump gravity, platform collision detection, coroutine-based game loop, and input handling for D-Pad controllers.

#### [NEW] [GameScreen.kt](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/java/com/example/burcingame/ui/GameScreen.kt)
- Compose 2D Canvas rendering for rainbow theme, clouds, stars, Burçin character (red skirt, white top, magical wand), helper cats, guardian owls, and gem collectibles with particle sparkles.

#### [NEW] [MenuScreens.kt](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/java/com/example/burcingame/ui/MenuScreens.kt)
- TV-optimized focusable menu screens for Main Menu, Level Selection, Victory, and Defeat with D-Pad focus handling.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/java/com/example/burcingame/MainActivity.kt)
- Wire up navigation between menu screens and game screen, handle hardware key events (D-Pad).

#### [MODIFY] [AndroidManifest.xml](file:///C:/Users/burak/AndroidStudioProjects/BurcinGame/app/src/main/AndroidManifest.xml)
- Add Leanback feature requirement (optional leanback), banner, and TV launcher intent filter.

## Verification Plan

### Automated Tests
- Build debug APK using `./gradlew :app:assembleDebug`.

### Manual Verification
- Verify D-Pad focus on TV menus.
- Verify game loop, jump physics, gem collection, and victory/defeat flows.
