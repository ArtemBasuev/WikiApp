package com.artem.wikiapp.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.WikiPage
import com.artem.wikiapp.data.WikiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class WikiFavoritesState(
    val pages: List<WikiPage> = emptyList(),
)

sealed interface WikiFavoritesIntent {
    data class PageClicked(val pageId: Long) : WikiFavoritesIntent
}

sealed interface WikiFavoritesEffect {
    data class NavigateToDetail(val pageId: Long) : WikiFavoritesEffect
}

class WikiFavoritesViewModel(
    private val repository: WikiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiFavoritesState())
    val state: StateFlow<WikiFavoritesState> = _state.asStateFlow()

    private val _effects = Channel<WikiFavoritesEffect>(Channel.BUFFERED)
    val effects: Flow<WikiFavoritesEffect> = _effects.receiveAsFlow()

    init {
        repository.favoriteIds
            .map { ids -> repository.getPages(ids) }
            .onEach { pages -> _state.update { it.copy(pages = pages) } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: WikiFavoritesIntent) {
        when (intent) {
            is WikiFavoritesIntent.PageClicked ->
                _effects.trySend(WikiFavoritesEffect.NavigateToDetail(intent.pageId))
        }
    }
}