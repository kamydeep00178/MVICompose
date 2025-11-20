package com.read.myapplication.ui.features.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.read.myapplication.domain.model.Post
import com.read.myapplication.domain.usecase.GetPostUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(val loading: Boolean = true, val detail: Post? = null, val error: String? = null)

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _ui = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _ui

    private val argId: Int = savedStateHandle.get<Int>("itemId") ?: -1

    init {
        if (argId >= 0) load(argId)
    }

    private fun load(id: Int) {
        viewModelScope.launch {
            _ui.value = DetailUiState(loading = true)
            try {
                val detail = getPostUseCase(id)
                _ui.value = DetailUiState(loading = false, detail = detail)
            } catch (t: Throwable) {
                _ui.value = DetailUiState(loading = false, detail = null, error = t.message)
            }
        }
    }
}