package com.example.burcingame

import com.example.burcingame.ui.GameEngine
import org.junit.Assert.*
import org.junit.Test

class GameEngineTest {

    @Test
    fun testGameEngineInitialState() {
        var gameOverCalled = false
        var victoryCalled = false
        val engine = GameEngine(
            onGameOverCallback = { gameOverCalled = true },
            onVictoryCallback = { victoryCalled = true }
        )

        assertEquals(3, engine.lives.value)
        assertEquals(0, engine.score.value)
        assertFalse(gameOverCalled)
        assertFalse(victoryCalled)
    }

    @Test
    fun testPhysicsSimulation500Frames() {
        val engine = GameEngine(
            onGameOverCallback = {},
            onVictoryCallback = {}
        )
        // Simulate calling updatePhysics via reflection
        val updatePhysicsMethod = GameEngine::class.java.getDeclaredMethod("updatePhysics")
        updatePhysicsMethod.isAccessible = true

        for (i in 0 until 500) {
            updatePhysicsMethod.invoke(engine)
        }

        // The player should still have 3 lives and be standing on Platform 1 (y = 940f - 75f = 865f)
        assertEquals(3, engine.lives.value)
        assertEquals(865f, engine.playerState.value.y, 0.1f)
    }

    @Test
    fun testGroundLayersAndLavaGaps() {
        val engine = GameEngine(
            onGameOverCallback = {},
            onVictoryCallback = {}
        )

        val groundPlatforms = engine.platforms.filter { it.height > 60f }
        val lavaPlatforms = engine.platforms.filter { it.type == com.example.burcingame.model.PlatformType.LAVA_PIT }

        // Zemin tek parça upuzun değil, farklı katmanlara bölünmüş
        assertTrue(groundPlatforms.size >= 5)
        // Farklı yükseklik katmanları mevcut (en az 4 farklı Y seviyesi)
        val distinctHeights = groundPlatforms.map { it.y }.distinct()
        assertTrue(distinctHeights.size >= 4)

        // Aralardaki boşluklarda lav çukurları mevcut
        assertTrue(lavaPlatforms.isNotEmpty())
        assertEquals(6, lavaPlatforms.size)
    }
}
