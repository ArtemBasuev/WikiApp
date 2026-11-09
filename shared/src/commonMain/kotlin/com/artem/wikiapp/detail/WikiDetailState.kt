package com.artem.wikiapp.detail

import com.artem.wikiapp.data.WikiPage

data class WikiDetailState(
    val isLoading: Boolean = true,
    val page: WikiPage? = null,
    val isFavorite: Boolean = false,
)
