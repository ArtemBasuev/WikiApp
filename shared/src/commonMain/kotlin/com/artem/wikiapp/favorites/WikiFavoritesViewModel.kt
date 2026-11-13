package com.artem.wikiapp.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.WikiRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

class WikiFavoritesViewModel(
    private val repository: WikiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiFavoritesState())
    val state: StateFlow<WikiFavoritesState> = _state.asStateFlow()

    private val _effects = Channel<WikiFavoritesEffect>(Channel.BUFFERED)
    val effects: Flow<WikiFavoritesEffect> = _effects.receiveAsFlow()

    private val retryTick = MutableStateFlow(0)

    init {
        loadFavorites()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadFavorites() {
        combine(repository.favoriteIds, retryTick) { ids, _ -> ids }
            .mapLatest { ids ->
                _state.update { it.copy(isLoading = it.pages.isEmpty() && ids.isNotEmpty(), hasError = false) }
                repository.getPages(ids)
            }
            .onEach { result ->
                result.fold(
                    onSuccess = { pages ->
                        _state.update { it.copy(pages = pages, isLoading = false, hasError = false) }
                    },
                    onFailure = { _state.update { it.copy(isLoading = false, hasError = true) } },
                )
            }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: WikiFavoritesIntent) {
        when (intent) {
            is WikiFavoritesIntent.PageClicked ->
                _effects.trySend(WikiFavoritesEffect.NavigateToDetail(intent.pageId))

            WikiFavoritesIntent.RetryRequested -> retryTick.update { it + 1 }
        }
    }
}