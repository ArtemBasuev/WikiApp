package com.artem.wikiapp.favorites

import com.artem.wikiapp.data.WikiPage

data class WikiFavoritesState(
    val pages: List<WikiPage> = emptyList(),
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
)

sealed interface WikiFavoritesIntent {
    data class PageClicked(val pageId: Long) : WikiFavoritesIntent
    data object RetryRequested : WikiFavoritesIntent
}

sealed interface WikiFavoritesEffect {
    data class NavigateToDetail(val pageId: Long) : WikiFavoritesEffect
}