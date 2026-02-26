package com.imagecounter.game.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface FreepikApi {

    @GET("/v1/resources")
    suspend fun searchResources(
        @Query("term") term: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10,
        @Query("order") order: String = "relevance",
        @Query("filters[content_type][vector]") vectorOnly: Int = 1,
    ): FreepikSearchResponse
}
