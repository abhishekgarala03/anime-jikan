# anime-jikan
Android app that fetches anime data from the Jikan API, displays a list of top anime with details pages, supports offline viewing via Room database, and follows MVVM architecture.

# Project Structure
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
