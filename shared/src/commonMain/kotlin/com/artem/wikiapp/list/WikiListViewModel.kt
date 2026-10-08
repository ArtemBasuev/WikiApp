package com.artem.wikiapp.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.WikiRepository
import kotlinx.coroutines.Job
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

private const val PAGE_SIZE = 5

class WikiListViewModel(
    private val repository: WikiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiListState())
    val state: StateFlow<WikiListState> = _state.asStateFlow()

    private val _effects = Channel<WikiListEffect>(Channel.BUFFERED)
    val effects: Flow<WikiListEffect> = _effects.receiveAsFlow()

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    init {
        repository.favoriteIds
            .onEach { ids -> _state.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)

        search(query = "")
    }

    fun onIntent(intent: WikiListIntent) {
        when (intent) {
            is WikiListIntent.PageClicked ->
                _effects.trySend(WikiListEffect.NavigateToDetail(intent.pageId))

            is WikiListIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.value) }
                search(query = intent.value)
            }

            is WikiListIntent.LoadMore -> loadMore()
            is WikiListIntent.FavoriteToggled -> repository.toggleFavorite(intent.pageId)
        }
    }

    private fun search(query: String) {
        searchJob?.cancel()
        loadMoreJob?.cancel()
        searchJob = viewModelScope.launch {
            val result = repository.searchPages(query, offset = 0, limit = PAGE_SIZE)
            _state.update {
                it.copy(
                    items = result.items,
                    canLoadMore = result.hasMore,
                    isLoadingMore = false,
                )
            }
        }
    }

    private fun loadMore() {
        val current = _state.value
        if (current.isLoadingMore || !current.canLoadMore) return

        loadMoreJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            val result = repository.searchPages(
                query = current.query,
                offset = current.items.size,
                limit = PAGE_SIZE,
            )
            _state.update {
                it.copy(
                    items = it.items + result.items,
                    canLoadMore = result.hasMore,
                    isLoadingMore = false,
                )
            }
        }
    }
}