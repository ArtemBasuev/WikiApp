package com.artem.wikiapp.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.artem.wikiapp.data.FavoritesStore
import com.artem.wikiapp.data.WikiPage
import com.artem.wikiapp.data.mockWikiPages
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 5

class WikiListViewModel(
    private val onNavigateToDetail: (Long) -> Unit,
) : ViewModel() {

    private val _state = MutableStateFlow(WikiListState())
    val state: StateFlow<WikiListState> = _state.asStateFlow()

    private var searchJob: Job? = null
    private var filtered: List<WikiPage> = mockWikiPages
    private var loadedCount = 0

    init {
        FavoritesStore.favoriteIds
            .onEach { ids -> _state.update { it.copy(favoriteIds = ids) } }
            .launchIn(viewModelScope)

        search(filter = "")
    }

    fun onIntent(intent: WikiListIntent) {
        when (intent) {
            is WikiListIntent.PageClicked -> onNavigateToDetail(intent.pageId)
            is WikiListIntent.QueryChanged -> {
                _state.update { it.copy(query = intent.value) }
                search(filter = intent.value)
            }
            is WikiListIntent.LoadMore -> loadMore()
            is WikiListIntent.FavoriteToggled -> FavoritesStore.toggle(intent.pageId)
        }
    }

    private fun search(filter: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            filtered = if (filter.isBlank()) {
                mockWikiPages
            } else {
                mockWikiPages.filter { page ->
                    page.title.contains(filter, ignoreCase = true) ||
                            page.extract.contains(filter, ignoreCase = true)
                }
            }
            loadedCount = 0
            appendNextPage()
        }
    }

    private fun loadMore() {
        if (_state.value.isLoadingMore || !_state.value.canLoadMore) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMore = true) }
            delay(400)
            appendNextPage()
            _state.update { it.copy(isLoadingMore = false) }
        }
    }

    private fun appendNextPage() {
        val nextCount = (loadedCount + PAGE_SIZE).coerceAtMost(filtered.size)
        loadedCount = nextCount
        _state.update {
            it.copy(
                items = filtered.take(nextCount),
                canLoadMore = nextCount < filtered.size,
            )
        }
    }
}