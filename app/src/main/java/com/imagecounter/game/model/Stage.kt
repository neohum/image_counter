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
        title = "양감 기르기 (기초)",
        description = "이미지를 하나씩 탭하며 수를 세어보세요",
        levels = (1..10).map { Level(number = it, singleCount = it) },
        iconResId = R.drawable.ic_star,
    ),
    Stage(
        id = 2,
        title = "큰 수 알아보기",
        description = "10개 묶음을 활용하여 더 큰 수를 세어보세요",
        levels = listOf(
            Level(number = 1, singleCount = 2, groupsOf10 = 1), // 12
            Level(number = 2, singleCount = 5, groupsOf10 = 1), // 15
            Level(number = 3, singleCount = 0, groupsOf10 = 2), // 20
            Level(number = 4, singleCount = 3, groupsOf10 = 2), // 23
            Level(number = 5, singleCount = 8, groupsOf10 = 2), // 28
            Level(number = 6, singleCount = 1, groupsOf10 = 3), // 31
            Level(number = 7, singleCount = 6, groupsOf10 = 3), // 36
            Level(number = 8, singleCount = 4, groupsOf10 = 4), // 44
            Level(number = 9, singleCount = 9, groupsOf10 = 4), // 49
            Level(number = 10, singleCount = 0, groupsOf10 = 5), // 50
        ),
        iconResId = R.drawable.ic_heart,
    ),
)
