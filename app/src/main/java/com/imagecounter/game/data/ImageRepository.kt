package com.imagecounter.game.data

import com.imagecounter.game.data.api.FreepikApiProvider

data class RemoteImageInfo(
    val url: String,
    val title: String,
)

class ImageRepository {

    private val api = FreepikApiProvider.api

    private val cache = mutableMapOf<String, List<RemoteImageInfo>>()

    val searchTerms = listOf(
        "star", "heart", "flower", "apple", "fish",
        "butterfly", "rainbow", "cat", "dog", "sun",
        "moon", "tree", "bird", "cloud", "mushroom",
    )

    suspend fun getImagesForTerm(term: String, limit: Int = 10): Result<List<RemoteImageInfo>> {
        cache[term]?.let { cached ->
            if (cached.size >= limit) return Result.success(cached.take(limit))
        }

        return try {
            val response = api.searchResources(term = term, limit = limit)
            val images = response.data.map { resource ->
                RemoteImageInfo(
                    url = resource.image.source.url,
                    title = resource.title,
                )
            }
            if (images.isEmpty()) {
                Result.failure(Exception("No images found for term: $term"))
            } else {
                cache[term] = images
                Result.success(images)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
