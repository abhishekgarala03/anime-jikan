package com.assignment.animeapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.assignment.animeapp.data.model.Anime
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Anime table
 */
@Dao
interface AnimeDao {

    /**
     * Get all anime from local database, ordered by rank
     */
    @Query("SELECT * FROM anime ORDER BY rank ASC")
    fun getAllAnime(): Flow<List<Anime>>

    /**
     * Get all anime as a list (not Flow) for sync operations
     */
    @Query("SELECT * FROM anime ORDER BY rank ASC")
    suspend fun getAllAnimeList(): List<Anime>

    /**
     * Get anime by MAL ID
     */
    @Query("SELECT * FROM anime WHERE malId = :malId")
    suspend fun getAnimeById(malId: Int): Anime?

    /**
     * Get anime by MAL ID as Flow for real-time updates
     */
    @Query("SELECT * FROM anime WHERE malId = :malId")
    fun getAnimeByIdFlow(malId: Int): Flow<Anime?>

    /**
     * Insert a single anime (replace if exists)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnime(anime: Anime)

    /**
     * Insert multiple anime (replace if exists)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAnime(animeList: List<Anime>)

    /**
     * Delete all anime from database
     */
    @Query("DELETE FROM anime")
    suspend fun deleteAllAnime()

    /**
     * Delete specific anime by ID
     */
    @Query("DELETE FROM anime WHERE malId = :malId")
    suspend fun deleteAnimeById(malId: Int)

    /**
     * Get count of anime in database
     */
    @Query("SELECT COUNT(*) FROM anime")
    suspend fun getAnimeCount(): Int

    /**
     * Search anime by title
     */
    @Query("SELECT * FROM anime WHERE title LIKE '%' || :query || '%' OR titleEnglish LIKE '%' || :query || '%' ORDER BY rank ASC")
    fun searchAnime(query: String): Flow<List<Anime>>
}
