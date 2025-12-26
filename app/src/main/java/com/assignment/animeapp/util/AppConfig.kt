package com.assignment.animeapp.util

/**
 * Application-wide configuration settings.
 * Used to handle design constraints and feature toggles.
 */
object AppConfig {
    /**
     * Toggle for showing profile/poster images.
     * Set to false to hide images due to legal constraints.
     * When false, use colored placeholders instead.
     */
    var showImages: Boolean = true

    /**
     * Cache validity duration in milliseconds (1 hour)
     */
    const val CACHE_DURATION_MS = 60 * 60 * 1000L

    /**
     * API rate limit delay in milliseconds
     * Jikan API has rate limiting, add delay between requests
     */
    const val API_RATE_LIMIT_DELAY = 500L

    /**
     * Default page size for pagination
     */
    const val DEFAULT_PAGE_SIZE = 25

    /**
     * Max lines for synopsis preview
     */
    const val SYNOPSIS_MAX_LINES = 4
}
