package com.imagecounter.game.model

import androidx.annotation.DrawableRes

data class GameImageState(
    val id: Int,
    @DrawableRes val drawableResId: Int,
    val xFraction: Float,
    val yFraction: Float,
    val isTapped: Boolean = false,
)
