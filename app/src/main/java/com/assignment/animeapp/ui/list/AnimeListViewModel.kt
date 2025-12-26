package com.assignment.animeapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.animeapp.data.repository.AnimeRepository
import com.assignment.animeapp.ui.common.AnimeListUiState
import com.assignment.animeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Anime List screen
 * Manages UI state using StateFlow for reactive updates
 */
@HiltViewModel
class AnimeListViewModel @Inject constructor(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AnimeListUiState>(AnimeListUiState.Loading)
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadAnimeList()
    }

    /**
     * Load anime list from repository
     * @param forceRefresh Whether to force refresh from network
     */
    fun loadAnimeList(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            repository.getTopAnime(forceRefresh).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (resource.data != null) {
                            _uiState.value = AnimeListUiState.Success(resource.data)
                        } else {
                            _uiState.value = AnimeListUiState.Loading
                        }
                    }
                    is Resource.Success -> {
                        _isRefreshing.value = false
                        _uiState.value = AnimeListUiState.Success(resource.data ?: emptyList())
                    }
                    is Resource.Error -> {
                        _isRefreshing.value = false
                        if (resource.data != null && resource.data.isNotEmpty()) {
                            // Show cached data with error message
                            _uiState.value = AnimeListUiState.Success(
                                data = resource.data,
                                errorMessage = resource.message
                            )
                        } else {
                            _uiState.value = AnimeListUiState.Error(
                                message = resource.message ?: "Unknown error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Refresh anime list (called from pull-to-refresh)
     */
    fun refresh() {
        _isRefreshing.value = true
        loadAnimeList(forceRefresh = true)
    }

    /**
     * Retry loading after error
     */
    fun retry() {
        loadAnimeList(forceRefresh = true)
    }
}
