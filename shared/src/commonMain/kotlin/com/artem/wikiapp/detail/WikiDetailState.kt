package com.artem.wikiapp.detail

import com.artem.wikiapp.data.WikiPage

enum class LinkLookupError { NotFound, Failed }

data class WikiDetailState(
    val isLoading: Boolean = true,
    val hasError: Boolean = false,
    val page: WikiPage? = null,
    val isFavorite: Boolean = false,
    val linkError: LinkLookupError? = null,
)
