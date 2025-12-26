package com.assignment.animeapp.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.assignment.animeapp.R
import com.assignment.animeapp.databinding.FragmentAnimeListBinding
import com.assignment.animeapp.ui.common.AnimeListUiState
import com.assignment.animeapp.util.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment displaying the list of top anime
 */
@AndroidEntryPoint
class AnimeListFragment : Fragment() {

    private var _binding: FragmentAnimeListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnimeListViewModel by viewModels()
    
    private lateinit var animeAdapter: AnimeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnimeListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSwipeRefresh()
        setupRetryButton()
        observeUiState()
    }

    private fun setupRecyclerView() {
        animeAdapter = AnimeAdapter { anime ->
            // Navigate to detail screen
            val action = AnimeListFragmentDirections
                .actionAnimeListFragmentToAnimeDetailFragment(
                    animeId = anime.malId,
                    animeTitle = anime.title
                )
            findNavController().navigate(action)
        }

        binding.rvAnimeList.apply {
            adapter = animeAdapter
            //layoutManager = GridLayoutManager(requireContext(), 2)
            setHasFixedSize(true)
        }
    }

    private fun setupSwipeRefresh() {
        binding.swipeRefresh.apply {
            setColorSchemeResources(R.color.error)
            setProgressBackgroundColorSchemeResource(R.color.surface_dark)
            setOnRefreshListener {
                viewModel.refresh()
            }
        }

        // Observe refreshing state
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isRefreshing.collect { isRefreshing ->
                    binding.swipeRefresh.isRefreshing = isRefreshing
                }
            }
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

    private fun updateUi(state: AnimeListUiState) {
        binding.apply {
            when (state) {
                is AnimeListUiState.Loading -> {
                    shimmerLayout.isVisible = true
                    shimmerLayout.startShimmer()
                    rvAnimeList.isVisible = false
                    errorLayout.isVisible = false
                }
                is AnimeListUiState.Success -> {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.isVisible = false
                    rvAnimeList.isVisible = true
                    errorLayout.isVisible = false
                    
                    animeAdapter.submitList(state.data)
                    
                    // Show error toast if there's a message (cached data with network error)
                    state.errorMessage?.let { message ->
                        requireContext().showToast(message)
                    }
                }
                is AnimeListUiState.Error -> {
                    shimmerLayout.stopShimmer()
                    shimmerLayout.isVisible = false
                    rvAnimeList.isVisible = false
                    errorLayout.isVisible = true
                    tvErrorMessage.text = state.message
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
