
# MVICompose — README

## Overview
This repository demonstrates an MVI (Model–View–Intent) architecture implemented with Jetpack Compose and a Clean Architecture layering.  
The code separates responsibilities into **data**, **domain**, and **ui** layers, uses **ViewModels** as the MVI state holders, and relies on **UseCases** for business logic. 
The app fetches posts from a REST API, caches them in Room, and displays lists and details.

---

## High-level architecture (Clean Architecture + MVI)
- **Presentation (ui)**
  - Composables (`UIScreen` files) render the UI and expose user intents (events).
  - `ViewModel` holds the UI state (single source of truth) and processes intents -> updates state.
  - This layer depends only on the **domain** layer.

- **Domain**
  - Contains business models (`domain/model`), repository interfaces (`domain/repository`), and `UseCase`s for application logic.
  - Pure Kotlin, no Android framework dependencies — easy to unit test.

- **Data**
  - Implements repository interfaces, handles data sources (network via Retrofit, local via Room).
  - Contains API definitions, DAOs, Entities, and mappers that convert between Data <-> Domain models.

Dependency direction: `ui -> domain -> data`. Data layer can depend on external libraries (Retrofit, Room), domain should be library-agnostic.

---

## MVI mapping (how MVI concepts are used in this project)
- **Intent (Action)**: user events and lifecycle events originate in Composables (e.g., `onRefresh`, `onItemClicked`) and are forwarded to the ViewModel.
- **Model (State)**: `ViewModel` exposes an immutable `State` (likely a `data class`) wrapped in `StateFlow`/`LiveData`. UI observes this state and re-renders.
- **View**: Composable functions (e.g., `PostsUIScreen`, `DetailUIScreen`) render UI from the ViewModel state and emit intents.
- **Update / Reducer**: ViewModel handles intents, invokes UseCases, then reduces results into a new UI state (keeping loading, data, and error).
- **Effects / Side-effects**: One-off events (navigation, toasts) are handled as single-shot events from the ViewModel (e.g., `SharedFlow` or `Channel`), separate from persistent UI state.

This project uses `ViewModel` + `StateFlow` + UseCases to implement MVI-style unidirectional data flow.

---

## How data flows (example: load posts)
1. User opens `PostsUIScreen` (View emits `LoadPosts` intent).
2. `PostsListViewModel` receives the intent and updates `uiState` to `loading`.
3. ViewModel calls `GetCachedThenRefreshUseCase` (domain).
4. UseCase coordinates:
   - Calls `PostRepository` (domain interface).
   - Repository implementation (`PostRepositoryImpl`) fetches cached posts from Room via `PostDao`, maps `Entity -> Domain` using mapper.
   - Optionally fetches fresh posts from network via `JsonPlaceHolderApi` (Retrofit) and updates DB.
5. UseCase returns result (list of `domain.model.Post`) to ViewModel.
6. ViewModel reduces result into `uiState` (posts + loading = false).
7. UI observes `StateFlow` and recomposes to show posts.

---

## Components in the repo (mapped to files)
(These are taken from the project structure)

### App / Entry
- `App.kt` — Application class (DI initialization).
- `MainActivity.kt` — Hosts Compose and Navigation.

### DI
- `di/NetworkModule.kt` — Provides Retrofit and API.
- `di/DatabaseModule.kt` — Provides Room DB and DAOs.
- `di/RepositoryModule.kt` — Binds repository implementations to domain interfaces.
- `di/IoDispatcher.kt` — Coroutine dispatcher binding.

### Data layer (`data/*`)
- `data/api/JsonPlaceHolderApi.kt` — Retrofit API definitions.
- `data/db/AppDatabase.kt` — Room database.
- `data/db/PostDao.kt` — DAO for posts.
- `data/db/PostEntity.kt` — Room entity mapping for posts.
- `data/mapper/PostMappers.kt` — Convert between `PostEntity` / `PostDto` / `Post` domain model.
- `data/repository/PostRepositoryImpl.kt` — Concrete implementation of `domain.repository.PostRepository`.

### Domain layer (`domain/*`)
- `domain/model/Post.kt` — Domain model.
- `domain/repository/PostRepository.kt` — Repository interface (abstraction).
- `domain/usecase/*` — UseCases:
  - `GetPostsUseCase.kt`
  - `GetPostUseCase.kt`
  - `GetCachedPostsUseCase.kt`
  - `GetCachedThenRefreshUseCase.kt` — orchestrates 'show cached then refresh' pattern.

### Presentation/UI (`ui/*`)
- `ui/features/posts/PostsUIScreen.kt` — Composable screen that lists posts.
- `ui/features/posts/PostsListViewModel.kt` — ViewModel for posts screen (MVI state holder).
- `ui/features/details/DetailUIScreen.kt` — Composable details screen.
- `ui/features/details/DetailViewModel.kt` — ViewModel for details.
- `ui/nav/*` — Navigation graph & destination definitions.
- `ui/theme/*` — Theme, colors, typography.

---

## Project file structure (recommended and matches repo)
```
app/
├─ src/
│  ├─ main/
│  │  ├─ java/com/read/myapplication/
│  │  │  ├─ App.kt
│  │  │  ├─ MainActivity.kt
│  │  │  ├─ di/
│  │  │  │  ├─ IoDispatcher.kt
│  │  │  │  ├─ NetworkModule.kt
│  │  │  │  ├─ DatabaseModule.kt
│  │  │  │  └─ RepositoryModule.kt
│  │  │  ├─ data/
│  │  │  │  ├─ api/JsonPlaceHolderApi.kt
│  │  │  │  ├─ db/
│  │  │  │  │  ├─ AppDatabase.kt
│  │  │  │  │  ├─ PostDao.kt
│  │  │  │  │  └─ PostEntity.kt
│  │  │  │  ├─ mapper/PostMappers.kt
│  │  │  │  └─ repository/PostRepositoryImpl.kt
│  │  │  ├─ domain/
│  │  │  │  ├─ model/Post.kt
│  │  │  │  ├─ repository/PostRepository.kt
│  │  │  │  └─ usecase/
│  │  │  │     ├─ GetPostsUseCase.kt
│  │  │  │     ├─ GetPostUseCase.kt
│  │  │  │     ├─ GetCachedPostsUseCase.kt
│  │  │  │     └─ GetCachedThenRefreshUseCase.kt
│  │  │  ├─ ui/
│  │  │  │  ├─ features/
│  │  │  │  │  ├─ posts/
│  │  │  │  │  │  ├─ PostsUIScreen.kt
│  │  │  │  │  │  └─ PostsListViewModel.kt
│  │  │  │  │  └─ details/
│  │  │  │  │     ├─ DetailUIScreen.kt
│  │  │  │  │     └─ DetailViewModel.kt
│  │  │  │  ├─ nav/
│  │  │  │  │  ├─ AppNavGraph.kt
│  │  │  │  │  └─ Destination.kt
│  │  │  │  └─ theme/
│  │  │  │     ├─ Theme.kt
│  │  │  │     ├─ Color.kt
│  │  │  │     └─ Type.kt
```

---

## Why this layout is good for MVI + Clean Code
- **Clear separation of concerns**: UI only talks to ViewModel; ViewModel only talks to UseCases; UseCases only talk to repository interfaces.
- **Testability**: Domain and ViewModel can be unit-tested by stubbing repository/usecase outputs.
- **Single source of truth**: ViewModel's `StateFlow` stores the UI state, preventing scattered mutable state.
- **Offline-first pattern**: `GetCachedThenRefreshUseCase` implements "show cached data quickly, then refresh" which makes UX snappy and robust.
- **Swap implementations easily**: Because domain depend on interfaces, you can replace `PostRepositoryImpl` with fake implementations for tests.

---

## Patterns & Recommendations
- **State data class**: Keep a `data class PostsUiState(val loading: Boolean = false, val posts: List<Post> = emptyList(), val error: String? = null)` in ViewModel file or a shared `ui/state` package.
- **Intents / Events**: Define sealed classes for UI events (e.g., `PostsEvent`) and one for UI effects (e.g., `PostsEffect`) to separate persistent state from one-off actions.
- **ViewModel concurrency**: Use `viewModelScope` + injected `IoDispatcher` for IO. Keep UI state updates on `Main` but use dispatchers for heavy work.
- **Error handling**: Map network/db exceptions to friendly messages at the UseCase boundary or in ViewModel.
- **Mapping**: Keep mappers in `data/mapper` for conversion; domain objects should be clean and framework-free.
- **Navigation**: Keep destinations typed (e.g., sealed `Destination`) and use a single nav graph.

---

## Small examples

### Example: `PostsEvent` (sealed)
```kotlin
sealed class PostsEvent {
  object Load : PostsEvent()
  object Refresh: PostsEvent()
  data class OpenPost(val id: Int) : PostsEvent()
}
```

### Example: simple `UiState`
```kotlin
data class PostsUiState(
  val isLoading: Boolean = false,
  val posts: List<Post> = emptyList(),
  val error: String? = null
)
```

### Example: ViewModel sketch
```kotlin
class PostsListViewModel @Inject constructor(
  private val getCachedThenRefresh: GetCachedThenRefreshUseCase,
  @IoDispatcher private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

  private val _uiState = MutableStateFlow(PostsUiState())
  val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

  init {
    load()
  }

  fun onEvent(event: PostsEvent) {
    when(event) {
      PostsEvent.Load -> load()
      PostsEvent.Refresh -> refresh()
      is PostsEvent.OpenPost -> navigate(event.id) // via effect
    }
  }

  private fun load() {
    viewModelScope.launch(ioDispatcher) {
      _uiState.update { it.copy(isLoading = true) }
      val posts = getCachedThenRefresh()
      _uiState.update { it.copy(isLoading = false, posts = posts) }
    }
  }
}
```

---
##Follow me :

**Medium:** https://kamaldeepkakkar.medium.com/
**LinkedIn:** https://www.linkedin.com/in/kamal-kakkar-13ab2773/

