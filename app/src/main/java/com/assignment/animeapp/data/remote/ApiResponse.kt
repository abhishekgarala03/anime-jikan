package com.assignment.animeapp.data.remote

import com.google.gson.annotations.SerializedName

/**
 * API response wrapper for top anime list endpoint
 */
data class TopAnimeResponse(
    @SerializedName("pagination")
    val pagination: Pagination?,
    @SerializedName("data")
    val data: List<AnimeDto>?
)

/**
 * Pagination info from API response
 */
data class Pagination(
    @SerializedName("last_visible_page")
    val lastVisiblePage: Int?,
    @SerializedName("has_next_page")
    val hasNextPage: Boolean?,
    @SerializedName("current_page")
    val currentPage: Int?
)

/**
 * API response wrapper for single anime detail endpoint
 */
data class AnimeDetailResponse(
    @SerializedName("data")
    val data: AnimeDto?
)

/**
 * Data Transfer Object for Anime from API
 */
data class AnimeDto(
    @SerializedName("mal_id")
    val malId: Int,
    @SerializedName("title")
    val title: String?,
    @SerializedName("title_english")
    val titleEnglish: String?,
    @SerializedName("title_japanese")
    val titleJapanese: String?,
    @SerializedName("synopsis")
    val synopsis: String?,
    @SerializedName("episodes")
    val episodes: Int?,
    @SerializedName("score")
    val score: Double?,
    @SerializedName("scored_by")
    val scoredBy: Int?,
    @SerializedName("rank")
    val rank: Int?,
    @SerializedName("popularity")
    val popularity: Int?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("rating")
    val rating: String?,
    @SerializedName("duration")
    val duration: String?,
    @SerializedName("season")
    val season: String?,
    @SerializedName("year")
    val year: Int?,
    @SerializedName("images")
    val images: Images?,
    @SerializedName("trailer")
    val trailer: Trailer?,
    @SerializedName("genres")
    val genres: List<Genre>?,
    @SerializedName("studios")
    val studios: List<Studio>?,
    @SerializedName("source")
    val source: String?,
    @SerializedName("type")
    val type: String?
)

data class Images(
    @SerializedName("jpg")
    val jpg: ImageUrls?,
    @SerializedName("webp")
    val webp: ImageUrls?
)

data class ImageUrls(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?
)

data class Trailer(
    @SerializedName("youtube_id")
    val youtubeId: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("embed_url")
    val embedUrl: String?,
    @SerializedName("images")
    val images: TrailerImages?
)

data class TrailerImages(
    @SerializedName("image_url")
    val imageUrl: String?,
    @SerializedName("small_image_url")
    val smallImageUrl: String?,
    @SerializedName("medium_image_url")
    val mediumImageUrl: String?,
    @SerializedName("large_image_url")
    val largeImageUrl: String?,
    @SerializedName("maximum_image_url")
    val maximumImageUrl: String?
)

data class Genre(
    @SerializedName("mal_id")
    val malId: Int?,
    @SerializedName("name")
    val name: String?
)

data class Studio(
    @SerializedName("mal_id")
    val malId: Int?,
    @SerializedName("name")
    val name: String?
)
