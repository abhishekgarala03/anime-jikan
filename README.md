# anime-jikan
Android app that fetches anime data from the Jikan API, displays a list of top anime with details pages, supports offline viewing via Room database, and follows MVVM architecture.

## Download Release APK
Download Link: v7.4-Release [Latest Release APK](https://github.com/abhishekgarala03/anime-jikan/releases/download/v7.4/AnimeXone-221430.apk)

## Project Structure
```
anime-jikan/
├── app/
│   └── src/main/
│       ├── java/com/seekho/animeapp/
│       │   ├── data/
│       │   │   ├── local/          # Room database
│       │   │   ├── remote/         # Retrofit API
│       │   │   ├── model/          # Data models
│       │   │   └── repository/     # Repository pattern
│       │   ├── di/                 # Dependency injection (Hilt)
│       │   ├── ui/
│       │   │   ├── list/           # Anime list screen
│       │   │   ├── detail/         # Anime detail screen
│       │   │   └── common/         # Shared components
│       │   ├── util/               # Utilities
│       │   └── AnimeApplication.kt
│       └── res/
│           ├── layout/
│           ├── values/
│           └── drawable/
```

## Core Dependencies

### `build.gradle.kts`

Key dependencies:
- **Retrofit 2.9.0** - API calls
- **Glide 4.16.0** - Image loading
- **Room 2.6.1** - Local database
- **Lifecycle ViewModel + StateFlow** - Reactive data
- **Hilt 2.50** - Dependency injection
- **Navigation Component** - Screen navigation

## Data Layer

### `Anime.kt`

Room entity and data model containing:
- `malId` (Primary Key)
- `title`, `titleEnglish`
- `episodes`, `score`, `rating`
- `synopsis`, `imageUrl`, `trailerUrl`
- `genres` (stored as JSON string)
- `status`, `duration`
- `lastUpdated` (for cache invalidation)

### `ApiResponse.kt`

API response wrapper matching Jikan API structure.

### `JikanApiService.kt`
```kotlin
interface JikanApiService {
    @GET("top/anime")
    suspend fun getTopAnime(@Query("page") page: Int = 1): TopAnimeResponse
    
    @GET("anime/{id}/full")
    suspend fun getAnimeDetails(@Path("id") id: Int): AnimeDetailResponse
}
```

### `AnimeDatabase.kt`

Room database with AnimeDao for CRUD operations.

### `AnimeRepository.kt`

Implements offline-first strategy:
1. Return cached data immediately (if available)
2. Fetch fresh data from API when online
3. Update cache with new data
4. Handle errors gracefully

## UI Layer - Anime List Screen

### `AnimeListFragment.kt`

Features:
- RecyclerView with grid layout (2 columns)
- Pull-to-refresh functionality
- Loading shimmer placeholder
- Error state with retry button

### `AnimeListViewModel.kt`
```kotlin
sealed class UiState<T> {
    data class Loading<T> : UiState<T>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error<T>(val message: String) : UiState<T>()
}
```

### `item_anime.xml`

Card layout displaying:
- Poster image (or placeholder based on config)
- Title (2 lines max)
- Episode count badge
- Rating with star icon

## UI Layer - Anime Detail Screen

### `AnimeDetailFragment.kt`

Features:
- WebView for YouTube trailer playback
- Fallback to poster image if no trailer
- Collapsing toolbar with poster
- Synopsis with "Read More" expansion
- Genre chips
- Episode/rating info cards

### `fragment_anime_detail.xml`

CoordinatorLayout with:
- CollapsingToolbarLayout (trailer/poster)
- NestedScrollView with details
- Material Design components

## Offline Support & Network Monitoring

### `NetworkMonitor.kt`
```kotlin
class NetworkMonitor @Inject constructor(context: Context) {
    val isConnected: StateFlow<Boolean>
    // Uses ConnectivityManager.NetworkCallback
}
```

Features:
- Emits connectivity state changes
- Triggers sync when connection restored
- Shows offline banner in UI

## Error Handling Strategy

| Error Type | Handling |
|------------|----------|
| No Network | Show cached data + offline banner |
| API Error (4xx/5xx) | Show error toast, allow retry |
| Empty Response | Show "No results" placeholder |
| Database Error | Log error, fallback to API |
| Timeout | Retry with exponential backoff |

## Design Constraint - Image Toggle

### `AppConfig.kt`
```kotlin
object AppConfig {
    var showImages: Boolean = true  // Toggle for legal compliance
}
```

When `showImages = false`:
- Replace poster with colored placeholder (using anime title hash for color)
- Show anime initial letter in placeholder
- Maintain card dimensions for layout consistency

## Key Files

### Data Layer
- **`Anime.kt`** - Room entity + domain model
- **`AnimeRepository.kt`** - Offline-first data strategy
- **`JikanApiService.kt`** - Retrofit API

### UI Layer
- **`AnimeListFragment.kt`** - Home screen
- **`AnimeDetailFragment.kt`** - Detail with trailer

### Utilities
- **`NetworkMonitor.kt`** - Connectivity observer
- **`AppConfig.kt`** - Feature toggles
