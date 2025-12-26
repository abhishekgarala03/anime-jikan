package com.assignment.animeapp.ui.list

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.assignment.animeapp.data.model.Anime
import com.assignment.animeapp.databinding.ItemAnimeBinding
import com.assignment.animeapp.util.AppConfig
import com.assignment.animeapp.util.getInitial
import com.assignment.animeapp.util.loadImage
import com.assignment.animeapp.util.toPlaceholderColor

/**
 * RecyclerView Adapter for anime list using ListAdapter with DiffUtil
 */
class AnimeAdapter(
    private val onItemClick: (Anime) -> Unit
) : ListAdapter<Anime, AnimeAdapter.AnimeViewHolder>(AnimeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val binding = ItemAnimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnimeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AnimeViewHolder(
        private val binding: ItemAnimeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(getItem(position))
                }
            }
        }

        fun bind(anime: Anime) {
            binding.apply {
                // Title
                tvTitle.text = anime.title

                // Episodes
                tvEpisodes.text = anime.getFormattedEpisodes()

                // Rating
                tvRating.text = anime.getFormattedScore()

                // Type (TV, Movie, etc.)
                tvType.text = anime.type ?: "Unknown"

                // Image handling with AppConfig toggle
                if (AppConfig.showImages) {
                    ivPoster.loadImage(anime.largeImageUrl ?: anime.imageUrl)
                    tvPlaceholderInitial.visibility = android.view.View.GONE
                } else {
                    // Show placeholder with initial letter
                    val color = anime.title.toPlaceholderColor(root.context)
                    val drawable = GradientDrawable().apply {
                        setColor(color)
                        cornerRadius = 16f * root.context.resources.displayMetrics.density
                    }
                    ivPoster.setImageDrawable(drawable)
                    tvPlaceholderInitial.visibility = android.view.View.VISIBLE
                    tvPlaceholderInitial.text = anime.title.getInitial()
                }

                // Rank badge
                if (anime.rank != null && anime.rank <= 100) {
                    tvRank.visibility = android.view.View.VISIBLE
                    tvRank.text = "#${anime.rank}"
                } else {
                    tvRank.visibility = android.view.View.GONE
                }
            }
        }
    }

    /**
     * DiffUtil callback for efficient list updates
     */
    class AnimeDiffCallback : DiffUtil.ItemCallback<Anime>() {
        override fun areItemsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem.malId == newItem.malId
        }

        override fun areContentsTheSame(oldItem: Anime, newItem: Anime): Boolean {
            return oldItem == newItem
        }
    }
}
