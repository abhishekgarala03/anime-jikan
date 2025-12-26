package com.assignment.animeapp.data.repository

import com.assignment.animeapp.data.local.AnimeDao
import com.assignment.animeapp.data.model.Anime
import com.assignment.animeapp.data.model.toAnime
import com.assignment.animeapp.data.model.toAnimeList
import com.assignment.animeapp.data.remote.JikanApiService
import com.assignment.animeapp.util.NetworkMonitor
import com.assignment.animeapp.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository implementing offline-first data strategy
 * 1. Return cached data immediately if available
 * 2. Fetch from network when online
 * 3. Update cache with new data
 * 4. Handle errors gracefully
 */
@Singleton
class AnimeRepository @Inject constructor(
    private val apiService: JikanApiService,
    private val animeDao: AnimeDao,
    private val networkMonitor: NetworkMonitor
) {

    /**
     * Get top anime list with offline-first strategy
     */
    fun getTopAnime(forceRefresh: Boolean = false): Flow<Resource<List<Anime>>> = flow {
        emit(Resource.Loading())

        // Get cached data first
        val cachedAnime = animeDao.getAllAnimeList()
        
        // Emit cached data if available
        if (cachedAnime.isNotEmpty()) {
            emit(Resource.Loading(cachedAnime))
        }

        // Check if we should fetch from network
        val shouldFetch = forceRefresh || 
                          cachedAnime.isEmpty() || 
                          cachedAnime.firstOrNull()?.isStale() == true

        if (shouldFetch && networkMonitor.isConnected.value) {
            try {
                // Fetch from API
                val response = apiService.getTopAnime(page = 1)
                val animeList = response.data?.toAnimeList() ?: emptyList()
                
                if (animeList.isNotEmpty()) {
                    // Update cache
                    animeDao.deleteAllAnime()
                    animeDao.insertAllAnime(animeList)
                    
                    emit(Resource.Success(animeList))
                } else if (cachedAnime.isNotEmpty()) {
                    emit(Resource.Success(cachedAnime))
                } else {
                    emit(Resource.Error("No anime found"))
                }
            } catch (e: Exception) {
                // Network error - return cached data with error message
                if (cachedAnime.isNotEmpty()) {
                    emit(Resource.Error(e.message ?: "Network error", cachedAnime))
                } else {
                    emit(Resource.Error(e.message ?: "Failed to load anime"))
                }
            }
        } else if (!networkMonitor.isConnected.value && cachedAnime.isEmpty()) {
            emit(Resource.Error("No internet connection and no cached data"))
        } else if (cachedAnime.isNotEmpty()) {
            emit(Resource.Success(cachedAnime))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Get anime details with caching
     */
    fun getAnimeDetails(malId: Int): Flow<Resource<Anime>> = flow {
        emit(Resource.Loading())

        // Check cache first
        val cachedAnime = animeDao.getAnimeById(malId)
        
        if (cachedAnime != null) {
            emit(Resource.Loading(cachedAnime))
        }

        // Fetch fresh data if online
        if (networkMonitor.isConnected.value) {
            try {
                val response = apiService.getAnimeDetails(malId)
                val anime = response.data?.toAnime()
                
                if (anime != null) {
                    animeDao.insertAnime(anime)
                    emit(Resource.Success(anime))
                } else if (cachedAnime != null) {
                    emit(Resource.Success(cachedAnime))
                } else {
                    emit(Resource.Error("Anime not found"))
                }
            } catch (e: Exception) {
                if (cachedAnime != null) {
                    emit(Resource.Error(e.message ?: "Network error", cachedAnime))
                } else {
                    emit(Resource.Error(e.message ?: "Failed to load anime details"))
                }
            }
        } else if (cachedAnime != null) {
            emit(Resource.Success(cachedAnime))
        } else {
            emit(Resource.Error("No internet connection"))
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Observe anime list from database as Flow
     */
    fun observeAnimeList(): Flow<List<Anime>> = animeDao.getAllAnime()

    /**
     * Observe specific anime from database
     */
    fun observeAnime(malId: Int): Flow<Anime?> = animeDao.getAnimeByIdFlow(malId)

    /**
     * Search anime in local database
     */
    fun searchLocalAnime(query: String): Flow<List<Anime>> = animeDao.searchAnime(query)

    /**
     * Check if we have cached data
     */
    suspend fun hasCachedData(): Boolean = animeDao.getAnimeCount() > 0
}
