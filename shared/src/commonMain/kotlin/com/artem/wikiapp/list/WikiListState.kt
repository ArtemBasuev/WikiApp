package com.artem.wikiapp.list

import com.artem.wikiapp.data.WikiPage

data class WikiListState(
    val query: String = "",
    val items: List<WikiPage> = emptyList(),
    val favoriteIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val isLoadingMore: Boolean = false,
    val loadMoreFailed: Boolean = false,
    val canLoadMore: Boolean = false,
)