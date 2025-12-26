package com.assignment.animeapp.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.assignment.animeapp.data.model.Anime
import com.assignment.animeapp.data.repository.AnimeRepository
import com.assignment.animeapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Anime Detail screen
 */
@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val repository: AnimeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val animeId: Int = savedStateHandle.get<Int>("animeId") ?: -1

    private val _uiState = MutableStateFlow<AnimeDetailUiState>(AnimeDetailUiState.Loading)
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    init {
        if (animeId != -1) {
            loadAnimeDetails()
        } else {
            _uiState.value = AnimeDetailUiState.Error("Invalid anime ID")
        }
    }

    /**
     * Load anime details from repository
     */
    fun loadAnimeDetails() {
        viewModelScope.launch {
            repository.getAnimeDetails(animeId).collectLatest { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        if (resource.data != null) {
                            _uiState.value = AnimeDetailUiState.Success(resource.data)
                        } else {
                            _uiState.value = AnimeDetailUiState.Loading
                        }
                    }
                    is Resource.Success -> {
                        resource.data?.let { anime ->
                            _uiState.value = AnimeDetailUiState.Success(anime)
                        } ?: run {
                            _uiState.value = AnimeDetailUiState.Error("Anime not found")
                        }
                    }
                    is Resource.Error -> {
                        if (resource.data != null) {
                            _uiState.value = AnimeDetailUiState.Success(
                                data = resource.data,
                                errorMessage = resource.message
                            )
                        } else {
                            _uiState.value = AnimeDetailUiState.Error(
                                message = resource.message ?: "Failed to load anime details"
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Retry loading details
     */
    fun retry() {
        loadAnimeDetails()
    }
}

/**
 * UI State for Anime Detail screen
 */
sealed class AnimeDetailUiState {
    object Loading : AnimeDetailUiState()
    data class Success(
        val data: Anime,
        val errorMessage: String? = null
    ) : AnimeDetailUiState()
    data class Error(val message: String) : AnimeDetailUiState()
}
