package com.assignment.animeapp.data.remote

import com.assignment.animeapp.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service interface for Jikan API
 */
interface JikanApiService {

    /**
     * Get top rated anime list
     * @param page Page number for pagination (default: 1)
     * @param limit Number of results per page (default: 25, max: 25)
     */
    @GET("top/anime")
    suspend fun getTopAnime(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): TopAnimeResponse

    /**
     * Get full details of a specific anime
     * @param id of the anime
     */
    @GET("anime/{id}/full")
    suspend fun getAnimeDetails(
        @Path("id") id: Int
    ): AnimeDetailResponse

    /**
     * Get anime by filter (for search/category)
     * @param filter Filter type: airing, upcoming, by popularity, favorite
     * @param page Page number
     */
    @GET("top/anime")
    suspend fun getAnimeByFilter(
        @Query("filter") filter: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 25
    ): TopAnimeResponse

    companion object {
        const val BASE_URL = BuildConfig.BASE_URL
    }
}
