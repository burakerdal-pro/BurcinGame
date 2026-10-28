package com.example.burcingame.model

data class PlayerState(
    val x: Float = 100f,
    val y: Float = 300f,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val isJumping: Boolean = false,
    val score: Int = 0,
    val lives: Int = 3,
    val facingRight: Boolean = true
)
