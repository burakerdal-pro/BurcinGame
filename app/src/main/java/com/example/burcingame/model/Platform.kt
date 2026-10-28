package com.example.burcingame.model

enum class PlatformType {
    RAINBOW_CLOUD,
    GOLDEN_BLOCK,
    CANDY_LEDGE,
    LAVA_PIT // [Hazard] Mario tarzı lav çukurları (Lava Pit) için platform türü
}

data class Platform(
    val id: Int,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val type: PlatformType = PlatformType.RAINBOW_CLOUD
)
