package com.artem.wikiapp.detail

import com.artem.wikiapp.data.WikiPage

data class WikiDetailState(
    val isLoading: Boolean = true,
    val page: WikiPage? = null,
    val isFavorite: Boolean = false,
)

sealed interface WikiDetailIntent {
    data object FavoriteToggled : WikiDetailIntent
    data class LinkClicked(val title: String) : WikiDetailIntent
}

sealed interface WikiDetailEffect {
    data class NavigateToDetail(val pageId: Long) : WikiDetailEffect
}