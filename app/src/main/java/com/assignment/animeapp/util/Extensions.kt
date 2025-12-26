package com.assignment.animeapp.util

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.assignment.animeapp.R

/**
 * Extension function to load image with Glide
 * Respects AppConfig.showImages setting
 */
fun ImageView.loadImage(
    url: String?,
    placeholder: Int = R.drawable.ic_placeholder
) {
    if (AppConfig.showImages && !url.isNullOrBlank()) {
        Glide.with(context)
            .load(url)
            .placeholder(placeholder)
            .error(placeholder)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } else {
        // Show placeholder when images are hidden
        setImageResource(placeholder)
    }
}

/**
 * Extension function to set visibility
 */
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

/**
 * Extension function to show toast
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

/**
 * Get a color based on anime title hash for placeholder
 */
fun String.toPlaceholderColor(context: Context): Int {
    val colors = listOf(
        R.color.primary,
        R.color.secondary,
        R.color.teal_700,
        R.color.purple_500
    )
    val index = kotlin.math.abs(this.hashCode()) % colors.size
    return ContextCompat.getColor(context, colors[index])
}

/**
 * Get first letter for placeholder
 */
fun String.getInitial(): String {
    return this.firstOrNull()?.uppercase() ?: "?"
}
