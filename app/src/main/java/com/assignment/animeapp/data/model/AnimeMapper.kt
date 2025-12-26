package com.assignment.animeapp.data.model

import com.google.gson.Gson
import com.assignment.animeapp.data.remote.AnimeDto

/**
 * Extension function to convert API DTO to Room Entity/Domain Model
 */
fun AnimeDto.toAnime(): Anime {
    val gson = Gson()
    return Anime(
        malId = malId,
        title = title ?: "Unknown Title",
        titleEnglish = titleEnglish,
        titleJapanese = titleJapanese,
        synopsis = synopsis,
        episodes = episodes,
        score = score,
        scoredBy = scoredBy,
        rank = rank,
        popularity = popularity,
        status = status,
        rating = rating,
        duration = duration,
        season = season,
        year = year,
        imageUrl = images?.jpg?.imageUrl ?: images?.webp?.imageUrl,
        largeImageUrl = images?.jpg?.largeImageUrl ?: images?.webp?.largeImageUrl,
        trailerUrl = trailer?.url,
        trailerEmbedUrl = trailer?.embedUrl,
        trailerImageUrl = trailer?.images?.maximumImageUrl 
            ?: trailer?.images?.largeImageUrl 
            ?: trailer?.images?.imageUrl,
        genres = genres?.mapNotNull { it.name }?.let { gson.toJson(it) },
        studios = studios?.mapNotNull { it.name }?.let { gson.toJson(it) },
        source = source,
        type = type,
        lastUpdated = System.currentTimeMillis()
    )
}

/**
 * Extension function to convert list of DTOs to list of domain models
 */
fun List<AnimeDto>.toAnimeList(): List<Anime> {
    return map { it.toAnime() }
}
