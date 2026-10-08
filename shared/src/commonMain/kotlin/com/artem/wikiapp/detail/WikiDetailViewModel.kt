package com.artem.wikiapp.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.WikiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WikiDetailViewModel(
    private val pageId: Long,
    private val repository: WikiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiDetailState())
    val state: StateFlow<WikiDetailState> = _state.asStateFlow()

    private val _effects = Channel<WikiDetailEffect>(Channel.BUFFERED)
    val effects: Flow<WikiDetailEffect> = _effects.receiveAsFlow()

    init {
        viewModelScope.launch {
            val page = repository.getPage(pageId)
            _state.update { it.copy(page = page, isLoading = false) }
        }

        repository.favoriteIds
            .onEach { ids -> _state.update { it.copy(isFavorite = pageId in ids) } }
            .launchIn(viewModelScope)
    }

    fun onIntent(intent: WikiDetailIntent) {
        when (intent) {
            WikiDetailIntent.FavoriteToggled -> {
                if (_state.value.page != null) repository.toggleFavorite(pageId)
            }

            is WikiDetailIntent.LinkClicked -> viewModelScope.launch {
                repository.findPageByTitle(intent.title)?.let {
                    _effects.send(WikiDetailEffect.NavigateToDetail(it.pageid))
                }
            }
        }
    }
}