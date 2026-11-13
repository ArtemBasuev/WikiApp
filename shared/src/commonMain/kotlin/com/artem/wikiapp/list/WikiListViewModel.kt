package com.artem.wikiapp.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.WikiRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val PAGE_SIZE = 20
private const val SEARCH_DEBOUNCE_MS = 400L

class WikiListViewModel(
    private val repository: WikiRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiListState())
    val state: StateFlow<WikiListState> = _state.asStateFlow()

    private val _effects = Channel<WikiListEffect>(Channel.BUFFERED)
    val effects: Flow<WikiListEffect> = _effects.receiveAsFlow()

    private var searchJob: Job? = null
    private var loadMoreJob: Job? = null

    private var activeQuery = ""

    private var nextCursor: String? = null

    init {
        repository.favoriteIds
            .onEach { ids -> _state.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)

        search(query = "", debounce = false)
    }

    fun onIntent(intent: WikiListIntent) {
        when (intent) {
            is WikiListIntent.PageClicked ->
                _effects.trySend(WikiListEffect.NavigateToDetail(intent.pageId))

            is WikiListIntent.QueryChanged -> onQueryChanged(intent.value)
            WikiListIntent.LoadMore -> loadMore()
            WikiListIntent.RetryRequested ->
                if (_state.value.items.isEmpty()) search(activeQuery, debounce = false) else loadMore()

            is WikiListIntent.FavoriteToggled -> repository.toggleFavorite(intent.pageId)
        }
    }

    private fun onQueryChanged(value: String) {
        _state.update { it.copy(query = value) }
        val trimmed = value.trim()
        if (trimmed != activeQuery) search(trimmed, debounce = true)
    }

    private fun search(query: String, debounce: Boolean) {
        activeQuery = query
        searchJob?.cancel()
        loadMoreJob?.cancel()
        _state.update { it.copy(isLoadingMore = false, loadMoreFailed = false) }

        searchJob = viewModelScope.launch {
            if (debounce) delay(SEARCH_DEBOUNCE_MS)
            _state.update { it.copy(isLoading = true, hasError = false) }

            repository.searchPages(query, cursor = null, limit = PAGE_SIZE).fold(
                onSuccess = { page ->
                    nextCursor = page.nextCursor
                    _state.update {
                        it.copy(
                            items = page.items,
                            canLoadMore = page.hasMore,
                            isLoading = false,
                            hasError = false,
                        )
                    }
                },
                onFailure = {
                    nextCursor = null
                    _state.update {
                        it.copy(items = emptyList(), canLoadMore = false, isLoading = false, hasError = true)
                    }
                },
            )
        }
    }

    private fun loadMore() {
        val current = _state.value
        if (current.isLoading || current.isLoadingMore || !current.canLoadMore) return

        loadMoreJob = viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true, loadMoreFailed = false) }

            repository.searchPages(activeQuery, cursor = nextCursor, limit = PAGE_SIZE).fold(
                onSuccess = { page ->
                    val merged = withContext(Dispatchers.Default) {
                        (_state.value.items + page.items).distinctBy { it.pageid }
                    }
                    nextCursor = page.nextCursor
                    _state.update {
                        it.copy(items = merged, canLoadMore = page.hasMore, isLoadingMore = false)
                    }
                },
                onFailure = {
                    _state.update { it.copy(isLoadingMore = false, loadMoreFailed = true) }
                },
            )
        }
    }
}