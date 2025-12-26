package com.assignment.animeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.assignment.animeapp.data.model.Anime

/**
 * Room Database for anime data persistence
 */
@Database(
    entities = [Anime::class],
    version = 1,
    exportSchema = false
)
abstract class AnimeDatabase : RoomDatabase() {

    abstract fun animeDao(): AnimeDao

    companion object {
        const val DATABASE_NAME = "anime_database"
    }
}
