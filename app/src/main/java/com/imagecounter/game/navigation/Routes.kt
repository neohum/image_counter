package com.imagecounter.game.navigation

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object StageSelect

@Serializable
data class GamePlay(
    val stageId: Int,
    val startLevel: Int = 1,
)

@Serializable
object MathPlay
