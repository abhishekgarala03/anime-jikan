package com.assignment.animeapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Anime Hub app
 * Annotated with @HiltAndroidApp for dependency injection
 */
@HiltAndroidApp
class AnimeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // App initialization can be done here
    }
}
