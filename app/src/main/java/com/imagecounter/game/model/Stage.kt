package com.imagecounter.game.model

import com.imagecounter.game.R

data class Stage(
    val id: Int,
    val title: String,
    val description: String,
    val levels: List<Level>,
    val iconResId: Int,
)

val STAGES = listOf(
    Stage(
        id = 1,
        title = "양감 기르기",
        description = "이미지를 하나씩 탭하며 수를 세어보세요",
        levels = (1..10).map { Level(number = it, imageCount = it) },
        iconResId = R.drawable.ic_star,
    ),
)
