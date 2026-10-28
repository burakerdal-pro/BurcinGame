# Project Plan

Create an Android TV (Mi Box) Mario-style platformer game called 'Burçin\'in Sihirli Gökkuşağı Maceraları' (Burçin's Magical Rainbow Adventures) featuring the user's daughter (from the attached photo) as the heroic main character. The game is specifically designed for Android TV (Mi Box) using D-pad remote controls (Left/Right to walk/run, Up/Center/OK to jump). Key features include: 1. Custom character sprite/avatar based on Burçin (red skirt, white top, holding a flag or magical wand). 2. Vibrant Rainbow theme with floating platforms, stars, and clouds. 3. Friendly and challenging creatures: friendly helper cats and wise guardian owls, obstacles, and collectible rainbow gems. 4. Smooth 2D Canvas-based platformer engine built in Jetpack Compose for Android TV. 5. Immersive menu, level selection, score tracking, victory/defeat screens, and TV-optimized focus states.

## Project Brief

# Project Brief: Burçin'in Sihirli Gökkuşağı Maceraları

## Features
1. **TV D-Pad Menu & Navigation**: Immersive main menu, level selection, score tracking, and victory/defeat screens with fully TV-optimized focus states navigable via Android TV (Mi Box) remote D-pad.
2. **2D Canvas Platformer Engine**: Smooth 2D canvas-based game engine built in Jetpack Compose supporting responsive D-pad controls (Left/Right to walk/run, Up/Center/OK to jump) and collision detection across dynamic floating platforms.
3. **Character Avatar & Vibrant Rainbow Theme**: Visual theme featuring floating platforms, stars, clouds, and a custom character sprite/avatar of the hero (Burçin in red skirt and white top with a magical wand).
4. **Collectibles & Interactive Creatures**: Engaging gameplay elements including collectible rainbow gems, friendly helper cats, wise guardian owls, and obstacles.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (with 2D Canvas API for game rendering)
- **Concurrency & State Management**: Kotlin Coroutines & StateFlow (for game loop and reactive state management)
- **Navigation**: Jetpack Navigation 3 (state-driven navigation for TV screens and menus)
- **Adaptive Strategy**: Compose Material Adaptive library (for responsive TV layouts and layout adaptation)

## Implementation Steps
**Total Duration:** 16m 14s

### Task_1_SetupAndNavigation: Set up data models, navigation structure using Jetpack Navigation 3, and TV D-Pad Menu screens (Main Menu, Level Selection, Score/Victory/Defeat screens) with TV focus states.
- **Status:** COMPLETED
- **Updates:** Successfully completed Task 1: Android TV manifest configuration, data models, TV navigation screens (Main Menu, Level Select, Victory/GameOver), and MainActivity wiring with clean build.
- **Acceptance Criteria:**
  - Project builds successfully
  - TV D-pad navigation between main menu, level select, and game screen works
  - app does not crash
- **Duration:** 9m 46s

### Task_2_GameEngineAndCanvas: Implement the 2D Canvas platformer game engine using Jetpack Compose, handling game loop, physics, D-pad input controls (Left/Right/Jump), and collision detection across dynamic floating platforms.
- **Status:** COMPLETED
- **Updates:** Successfully implemented Task 2: GameEngine physics loop, D-pad key event handling, platform collision detection, collectible interactions, and unit tests with successful build and test pass.
- **Acceptance Criteria:**
  - Game loop runs smoothly using Coroutines and StateFlow
  - D-pad controls player movement and jumping
  - Collision detection working on platforms
  - app does not crash
- **Duration:** 1m 58s

### Task_3_ThemeSpritesAndCollectibles: Implement vibrant rainbow theme assets (floating platforms, stars, clouds, Burçin avatar with red skirt/white top/magical wand) and interactive entities (collectible gems, helper cats, guardian owls, obstacles).
- **Status:** COMPLETED
- **Updates:** Successfully implemented Task 3: Vibrant rainbow theme canvas rendering, custom Burçin avatar with red skirt/white top/magical wand, helper cats, guardian owls, sparkling collectibles, and TV-optimized HUD.
- **Acceptance Criteria:**
  - Custom character sprite and rainbow theme rendered correctly
  - Collectibles and interactive creatures functional in gameplay
  - app does not crash
- **Duration:** 2m 30s

### Task_4_RunAndVerify: Run and verify the complete Android TV Mario-style platformer game app. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** COMPLETED
- **Updates:** Successfully verified Task 4: Critic agent verified build, unit tests, Android TV emulator execution, D-pad focus, and platformer gameplay with zero crashes.
- **Acceptance Criteria:**
  - make sure all existing tests pass
  - build pass
  - app does not crash
  - gameplay is smooth and fully navigable via D-pad on Android TV
- **Duration:** 2m

