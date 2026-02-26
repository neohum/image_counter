package com.imagecounter.game.data.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreepikSearchResponse(
    val data: List<FreepikResource>,
)

@Serializable
data class FreepikResource(
    val id: Int,
    val title: String,
    val image: FreepikImage,
)

@Serializable
data class FreepikImage(
    val source: FreepikImageSource,
)

@Serializable
data class FreepikImageSource(
    val url: String,
    val size: String? = null,
)
