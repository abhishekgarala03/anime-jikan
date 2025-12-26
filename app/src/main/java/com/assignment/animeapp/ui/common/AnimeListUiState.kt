package com.assignment.animeapp.ui.common

import com.assignment.animeapp.data.model.Anime

/**
 * UI State for Anime List screen
 */
sealed class AnimeListUiState {
    object Loading : AnimeListUiState()
    data class Success(
        val data: List<Anime>,
        val errorMessage: String? = null // For showing toast while displaying cached data
    ) : AnimeListUiState()
    data class Error(val message: String) : AnimeListUiState()
}