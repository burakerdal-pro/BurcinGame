package com.example.burcingame.model

enum class EntityType {
    CAT,
    OWL,
    OBSTACLE
}

data class GameEntity(
    val id: Int,
    val x: Float,
    val y: Float,
    val type: EntityType,
    val vx: Float = 0f,
    val vy: Float = 0f,
    val isAlive: Boolean = true
)
