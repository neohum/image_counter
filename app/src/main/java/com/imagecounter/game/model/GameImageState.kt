package com.imagecounter.game.model

import androidx.annotation.DrawableRes

sealed interface ImageSource {
    data class Local(@DrawableRes val resId: Int) : ImageSource
    data class Remote(val url: String) : ImageSource
}

data class GameImageState(
    val id: Int,
    val imageSource: ImageSource,
    val xFraction: Float,
    val yFraction: Float,
    val isTapped: Boolean = false,
    val value: Int = 1,
)
