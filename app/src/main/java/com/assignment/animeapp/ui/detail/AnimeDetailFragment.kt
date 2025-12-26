package com.assignment.animeapp.ui.detail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.assignment.animeapp.R
import com.assignment.animeapp.data.model.Anime
import com.assignment.animeapp.databinding.FragmentAnimeDetailBinding
import com.assignment.animeapp.util.AppConfig
import com.assignment.animeapp.util.loadImage
import com.assignment.animeapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying anime details with trailer playback
 */
@AndroidEntryPoint
class AnimeDetailFragment : Fragment() {

    private var _binding: FragmentAnimeDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnimeDetailViewModel by viewModels()

    private var isTrailerPlaying = false
    private var isSynopsisExpanded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRetryButton()
        observeUiState()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRetryButton() {
        binding.btnRetry.setOnClickListener {
            viewModel.retry()
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: AnimeDetailUiState) {
        binding.apply {
            when (state) {
                is AnimeDetailUiState.Loading -> {
                    progressBar.isVisible = true
                    contentLayout.isVisible = false
                    errorLayout.isVisible = false
                }
                is AnimeDetailUiState.Success -> {
                    progressBar.isVisible = false
                    contentLayout.isVisible = true
                    errorLayout.isVisible = false
                    bindAnimeData(state.data)

                    state.errorMessage?.let { message ->
                        requireContext().showToast(message)
                    }
                }
                is AnimeDetailUiState.Error -> {
                    progressBar.isVisible = false
                    contentLayout.isVisible = false
                    errorLayout.isVisible = true
                    tvErrorMessage.text = state.message
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun bindAnimeData(anime: Anime) {
        binding.apply {
            // Title
            tvTitle.text = anime.title

            // Japanese title
            if (!anime.titleJapanese.isNullOrBlank()) {
                tvTitleJapanese.isVisible = true
                tvTitleJapanese.text = anime.titleJapanese
            }

            // Poster/Trailer
            setupMedia(anime)

            // Score
            tvScore.text = anime.getFormattedScore()

            // Episodes
            tvEpisodes.text = anime.episodes?.toString() ?: "?"

            // Rank
            tvRank.text = anime.rank?.let { "#$it" } ?: "N/A"

            // Genres
            setupGenres(anime.getGenresList())

            // Synopsis
            setupSynopsis(anime.synopsis)

            // Additional Info
            tvStatus.text = anime.status ?: "Unknown"
            tvType.text = anime.type ?: "Unknown"
            tvDuration.text = anime.duration ?: "Unknown"
            tvStudios.text = anime.getStudiosList().joinToString(", ").ifBlank { "Unknown" }
            tvSource.text = anime.source ?: "Unknown"
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupMedia(anime: Anime) {
        binding.apply {
            // Load poster image
            if (AppConfig.showImages) {
                ivPoster.loadImage(anime.largeImageUrl ?: anime.imageUrl)
            }

            // Check if trailer is available
            val trailerUrl = anime.trailerEmbedUrl
            if (!trailerUrl.isNullOrBlank()) {
                ivPlayButton.isVisible = true
                ivPlayButton.setOnClickListener {
                    playTrailer(trailerUrl)
                }
            } else {
                ivPlayButton.isVisible = false
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun playTrailer(embedUrl: String) {
        if (isTrailerPlaying) return

        binding.apply {
            webviewTrailer.apply {
                isVisible = true
                settings.javaScriptEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.cacheMode = WebSettings.LOAD_DEFAULT
                settings.domStorageEnabled = true
                webChromeClient = WebChromeClient()
                webViewClient = WebViewClient()

                // Load YouTube embed URL
                val html = """
                    <!DOCTYPE html>
                    <html>
                    <head>
                        <meta name="viewport" content="width=device-width, initial-scale=1.0">
                        <style>
                            body { margin: 0; background: black; }
                            iframe { width: 100%; height: 100%; border: none; }
                        </style>
                    </head>
                    <body>
                        <iframe src="$embedUrl" 
                            allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture"
                            style="position:fixed; left:0; bottom:0; right:0; width:100%; height:100%; border:none;"
                            referrerpolicy="strict-origin-when-cross-origin" 
                        >
                        </iframe>
                    </body>
                    </html>
                """.trimIndent()

                loadDataWithBaseURL(embedUrl, html, "text/html", "UTF-8", null)
            }
            ivPlayButton.isVisible = false
            ivPoster.isVisible = false
        }
        isTrailerPlaying = true
    }

    private fun setupGenres(genres: List<String>) {
        binding.chipGroupGenres.removeAllViews()
        genres.forEach { genre ->
            val chip = Chip(requireContext()).apply {
                text = genre
                isClickable = false
                setChipBackgroundColorResource(R.color.surface_dark)
                setTextColor(resources.getColor(R.color.text_primary, null))
                chipStrokeColor = resources.getColorStateList(R.color.primary, null)
                chipStrokeWidth = 1f * resources.displayMetrics.density
            }
            binding.chipGroupGenres.addView(chip)
        }
    }

    private fun setupSynopsis(synopsis: String?) {
        binding.apply {
            if (synopsis.isNullOrBlank()) {
                tvSynopsis.text = getString(R.string.empty_synopsis)
                tvReadMore.isVisible = false
            } else {
                tvSynopsis.text = synopsis
                tvSynopsis.maxLines = AppConfig.SYNOPSIS_MAX_LINES

                // Add read more functionality
                tvSynopsis.post {
                    if (tvSynopsis.lineCount > AppConfig.SYNOPSIS_MAX_LINES) {
                        tvReadMore.isVisible = true
                        tvReadMore.setOnClickListener {
                            toggleSynopsis()
                        }
                    }
                }
            }
        }
    }

    private fun toggleSynopsis() {
        binding.apply {
            isSynopsisExpanded = !isSynopsisExpanded
            if (isSynopsisExpanded) {
                tvSynopsis.maxLines = Int.MAX_VALUE
                tvReadMore.text = getString(R.string.read_less)
            } else {
                tvSynopsis.maxLines = AppConfig.SYNOPSIS_MAX_LINES
                tvReadMore.text = getString(R.string.read_more)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // Pause webview when fragment is not visible
        binding.webviewTrailer.onPause()
    }

    override fun onResume() {
        super.onResume()
        binding.webviewTrailer.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.webviewTrailer.destroy()
        _binding = null
    }
}
