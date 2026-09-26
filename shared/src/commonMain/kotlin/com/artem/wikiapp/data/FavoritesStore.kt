package com.artem.wikiapp.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object FavoritesStore {
    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    val favoriteIds: StateFlow<Set<Long>> = _favoriteIds.asStateFlow()

    fun toggle(pageId: Long) {
        _favoriteIds.update { current ->
            if (pageId in current) current - pageId else current + pageId
        }
    }

    fun isFavorite(pageId: Long): Boolean = pageId in _favoriteIds.value
}