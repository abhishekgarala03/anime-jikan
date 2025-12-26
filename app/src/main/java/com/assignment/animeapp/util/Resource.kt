package com.assignment.animeapp.util

/**
 * A generic class that holds a value with its loading status.
 * Used to wrap data with loading, success, or error states.
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    /**
     * Check if the resource is in loading state
     */
    val isLoading: Boolean get() = this is Loading

    /**
     * Check if the resource is in success state
     */
    val isSuccess: Boolean get() = this is Success

    /**
     * Check if the resource is in error state
     */
    val isError: Boolean get() = this is Error
}
