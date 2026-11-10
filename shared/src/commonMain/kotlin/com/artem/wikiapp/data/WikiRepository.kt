package com.artem.wikiapp.data

import kotlinx.coroutines.flow.StateFlow

data class PagedResult<T>(
    val items: List<T>,
    val hasMore: Boolean,
)

interface WikiRepository {
    val favoriteIds: StateFlow<Set<Long>>

    suspend fun searchPages(query: String, offset: Int, limit: Int): Result<PagedResult<WikiPage>>

    suspend fun getPage(pageId: Long): Result<WikiPage?>

    suspend fun findPageByTitle(title: String): Result<WikiPage?>

    suspend fun getPages(pageIds: Set<Long>): Result<List<WikiPage>>

    fun toggleFavorite(pageId: Long)
}