package com.imagecounter.game.model

data class Level(
    val number: Int,
    val singleCount: Int,
    val groupsOf10: Int = 0,
) {
    val totalValue: Int
        get() = groupsOf10 * 10 + singleCount
    val totalImages: Int
        get() = groupsOf10 + singleCount
}
