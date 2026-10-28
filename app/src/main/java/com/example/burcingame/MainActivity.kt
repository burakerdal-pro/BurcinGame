package com.example.burcingame

import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import com.example.burcingame.model.GameState
import com.example.burcingame.ui.*

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            BurcinGameTheme {
                var gameState by remember { mutableStateOf(GameState.MENU) }
                var selectedLevel by remember { mutableStateOf(1) }
                var currentScore by remember { mutableStateOf(0) }

                when (gameState) {
                    GameState.MENU -> {
                        MainMenuScreen(
                            onNavigate = { newState -> gameState = newState },
                            onQuit = { finish() }
                        )
                    }
                    GameState.LEVEL_SELECT -> {
                        LevelSelectScreen(
                            onSelectLevel = { level ->
                                selectedLevel = level
                                gameState = GameState.PLAYING
                            },
                            onBack = { gameState = GameState.MENU }
                        )
                    }
                    GameState.PLAYING -> {
                        GameplayScreen(
                            level = selectedLevel,
                            onGameOver = {
                                currentScore = 150
                                gameState = GameState.GAME_OVER
                            },
                            onVictory = {
                                currentScore = 500
                                gameState = GameState.VICTORY
                            },
                            onBackToMenu = { gameState = GameState.MENU }
                        )
                    }
                    GameState.GAME_OVER -> {
                        GameOverScreen(
                            score = currentScore,
                            onRetry = { gameState = GameState.PLAYING },
                            onMenu = { gameState = GameState.MENU }
                        )
                    }
                    GameState.VICTORY -> {
                        VictoryScreen(
                            score = currentScore,
                            onNextLevel = {
                                if (selectedLevel < 6) selectedLevel++
                                gameState = GameState.PLAYING
                            },
                            onMenu = { gameState = GameState.MENU }
                        )
                    }
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUi()
        }
    }

    private fun hideSystemUi() {
        window.insetsController?.apply {
            hide(WindowInsets.Type.systemBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}
