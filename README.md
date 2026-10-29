# 🌈 Burçin's Magical Rainbow Adventures

![Kotlin](https://img.shields.io/badge/Kotlin-1.9%2B-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-TV%20%26%20Mobile-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-Canvas-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue?style=for-the-badge)

A charming, vibrant, and fun 2D side-scrolling platformer game built purely in **Kotlin** and **Jetpack Compose Custom Canvas** for Android & Android TV!

---

## ✨ Overview

**Burçin's Magical Rainbow Adventures** is a cute, lighthearted indie platformer created purely for **fun, entertainment, and learning**. 

Join **Burçin** as she travels through a magical world filled with multi-tiered golden platforms, floating rainbow clouds, candy ledges, twinkling stars, and friendly magical creatures while dodging hot molten lava pits!

Designed with **Android TV (16:9 large screen)** and mobile devices in mind, the game features edge-to-edge full-screen auto-scaling, smooth camera tracking, and D-Pad / Keyboard / Touch input support.

---

## 🌟 Key Features

* 🎨 **100% Jetpack Compose Canvas Graphics:** Beautifully rendered 2D graphics featuring gradient skies, twinkling stars, animated parallax clouds, and multi-colored rainbow arcs without external heavy game engine bloat.
* 🏃 **Fun Platforming Mechanics:**
  * **Dynamic World:** Explore a 3000f wide multi-tiered landscape with golden bedrock ground, candy ledges, and floating cloud bridges.
  * **Lava Pit Hazards:** Watch out for heat glows, bubbling magma, and rising volcanic sparks!
  * **Smooth Camera Following:** Linear interpolation camera tracking centered on Burçin.
* 💎 **Collectibles & Companions:**
  * 🌟 **Rainbow Stars**, 💎 **Magic Diamonds**, and 🔮 **Unicorn Crystals** (+150 Pts).
  * 🐾 **Magical Cats** and 🦉 **Wise Owls** that award +300 bonus points when approached!
* 🗺️ **6 Level Selection Grid:** Select from 6 different adventure levels with interactive focused TV menu cards.
* 🎮 **Full TV & Keyboard Control Support:**
  * **D-Pad / Arrow Keys:** Left/Right navigation across the landscape.
  * **OK / Enter / Space:** Jump over lava pits and reach high cloud platforms.
* 📺 **Full-Screen Auto-Scaling Layout:** Dynamically centers and enlarges the game view for all screen resolutions (1080p, 4K Android TV, tablets, and phones) with floating overlay HUDs.

---

## 🎮 How to Play

1. **Launch the Game** from the Main Menu.
2. Select **"Play"** or choose a level from **"Level Select"**.
3. **Controls:**
   | Action | Keyboard | TV Remote / Controller |
   | :--- | :--- | :--- |
   | **Move Left** | `Left Arrow` / `A` | `D-Pad Left` |
   | **Move Right** | `Right Arrow` / `D` | `D-Pad Right` |
   | **Jump** | `Space` / `Enter` / `Up Arrow` | `D-Pad Center (OK)` / `Button A` |
4. **Goal:** Collect all magical items, greet friendly animal companions, avoid falling into lava, and reach the victory portal!

---

## 🛠️ Tech Stack & Architecture

- **Language:** 100% [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Rendering:** Jetpack Compose `Canvas` with `DrawScope`, `withTransform`, custom gradients, shapes, and paths.
- **State Management:** Kotlin `StateFlow`, `MutableStateFlow`, and Coroutine-based 60 FPS Game Loop (~16ms ticks).
- **Target Devices:** Android TV, Android Handhelds, Tablets, and Chromebooks (API 24+).

---

## 🚀 Getting Started

### Prerequisites

- [Android Studio](https://developer.android.com/studio) (Hedgehog or newer recommended)
- Android SDK API 24 (Android 7.0) or higher
- An Android Emulator / TV Emulator or physical Android device

### Building & Running

1. Clone or download this repository:
   ```bash
   git clone https://github.com/your-username/BurcinGame.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync dependencies.
4. Select your target device (e.g. **Android TV Emulator** or **Phone**) and click **Run (Shift + F10)**.

---

## 💜 Disclaimer & Fun Purpose

> **Note:** This game was created as a fun, creative, and joyful project! It is meant purely for entertainment, family fun, and as a demonstration of native Jetpack Compose 2D canvas game capabilities. Enjoy the magical journey! 🌈✨

---

Made with ❤️ and magic!
