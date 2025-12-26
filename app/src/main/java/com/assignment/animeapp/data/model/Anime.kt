package com.assignment.animeapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Room entity representing an Anime.
 * Also used as the domain model throughout the app.
 */
@Parcelize
@Entity(tableName = "anime")
data class Anime(
    @PrimaryKey
    val malId: Int,
    val title: String,
    val titleEnglish: String?,
    val titleJapanese: String?,
    val synopsis: String?,
    val episodes: Int?,
    val score: Double?,
    val scoredBy: Int?,
    val rank: Int?,
    val popularity: Int?,
    val status: String?,
    val rating: String?,
    val duration: String?,
    val season: String?,
    val year: Int?,
    val imageUrl: String?,
    val largeImageUrl: String?,
    val trailerUrl: String?,
    val trailerEmbedUrl: String?,
    val trailerImageUrl: String?,
    val genres: String?, // Stored as JSON string
    val studios: String?, // Stored as JSON string
    val source: String?,
    val type: String?,
    val lastUpdated: Long = System.currentTimeMillis()
) : Parcelable {
    
    /**
     * Check if the cached data is stale (older than 1 hour)
     */
    fun isStale(): Boolean {
        val oneHourInMillis = 60 * 60 * 1000L
        return System.currentTimeMillis() - lastUpdated > oneHourInMillis
    }
    
    /**
     * Get formatted episode count
     */
    fun getFormattedEpisodes(): String {
        return episodes?.let { "$it Episodes" } ?: "? Episodes"
    }
    
    /**
     * Get formatted score
     */
    fun getFormattedScore(): String {
        return score?.let { String.format("★ %.1f", it) } ?: "★ N/A"
    }
    
    /**
     * Parse genres from JSON string
     */
    fun getGenresList(): List<String> {
        return genres?.removeSurrounding("[", "]")
            ?.split(",")
            ?.map { it.trim().removeSurrounding("\"") }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }
    
    /**
     * Parse studios from JSON string
     */
    fun getStudiosList(): List<String> {
        return studios?.removeSurrounding("[", "]")
            ?.split(",")
            ?.map { it.trim().removeSurrounding("\"") }
            ?.filter { it.isNotBlank() }
            ?: emptyList()
    }
}
