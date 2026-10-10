package com.artem.wikiapp.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MockWikiRepository(
    private val pages: List<WikiPage> = mockWikiPages,
    private val loadMoreLatencyMs: Long = 400,
) : WikiRepository {

    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    override val favoriteIds: StateFlow<Set<Long>> = _favoriteIds.asStateFlow()

    override suspend fun searchPages(query: String, offset: Int, limit: Int): PagedResult<WikiPage> {
        if (offset > 0) delay(loadMoreLatencyMs)

        val matched = if (query.isBlank()) {
            pages
        } else {
            pages.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.extract.contains(query, ignoreCase = true)
            }
        }
        val items = matched.drop(offset).take(limit)
        return PagedResult(items = items, hasMore = offset + items.size < matched.size)
    }

    override suspend fun getPage(pageId: Long): WikiPage? =
        pages.find { it.pageid == pageId }

    override suspend fun findPageByTitle(title: String): WikiPage? =
        pages.find { it.title.equals(title, ignoreCase = true) }

    override suspend fun getPages(pageIds: Set<Long>): List<WikiPage> =
        pages.filter { it.pageid in pageIds }

    override fun toggleFavorite(pageId: Long) {
        _favoriteIds.update { current ->
            if (pageId in current) current - pageId else current + pageId
        }
    }
}