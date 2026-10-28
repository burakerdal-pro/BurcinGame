package com.example.burcingame.model

enum class CollectibleType {
    RAINBOW_STAR,
    MAGIC_DIAMOND,
    UNICORN_CRYSTAL
}

data class Collectible(
    val id: Int,
    val x: Float,
    val y: Float,
    val type: CollectibleType = CollectibleType.RAINBOW_STAR,
    val collected: Boolean = false
)
