package com.read.myapplication.ui.features.posts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.read.myapplication.domain.model.Post
import com.read.myapplication.domain.usecase.GetCachedThenRefreshUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PostsIntent {
    object Load : PostsIntent
    data class PostClicked(val id: Int) : PostsIntent
}

data class PostsUiState(
    val loading: Boolean = false,
    val message: String = "",
    val posts: List<Post> = emptyList(),
    val error: String? = null
)

sealed interface PostsUiEvent {
    data class Snackbar(val text: String) : PostsUiEvent
    data class Navigate(val route: String) : PostsUiEvent
}

@HiltViewModel
class PostsListViewModel @Inject constructor(
    private val getCachedThenRefreshUseCase: GetCachedThenRefreshUseCase
) : ViewModel() {
    private val _ui = MutableStateFlow(PostsUiState())
    val uiState: StateFlow<PostsUiState> = _ui.asStateFlow()

    private val _events = MutableSharedFlow<PostsUiEvent>()
    val events = _events.asSharedFlow()

    private val intents = MutableSharedFlow<PostsIntent>(extraBufferCapacity = 32)

    init {
        viewModelScope.launch { intents.collect { handleIntent(it) } }
    }

    fun submit(intent: PostsIntent) = intents.tryEmit(intent)

    private suspend fun handleIntent(intent: PostsIntent) {
        when (intent) {
            PostsIntent.Load -> load()
            is PostsIntent.PostClicked -> onItemClicked(intent.id)
        }
    }

    private suspend fun load() {
        _ui.update { it.copy(loading = true) }
        val cached = getCachedThenRefreshUseCase()
        _ui.update {
            it.copy(loading = false, posts = cached)
        }
        return
    }

    private fun onItemClicked(id: Int) {
        viewModelScope.launch {
            _events.emit(PostsUiEvent.Navigate("detail/$id"))
        }
    }
}