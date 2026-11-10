package com.artem.wikiapp.data

import com.artem.wikiapp.data.remote.WikiApi
import com.artem.wikiapp.data.remote.dto.WikiResponseDto
import com.artem.wikiapp.data.remote.toWikiPage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

private const val DEFAULT_QUERY = "programming language"
private const val IDS_CHUNK = 20

class WikiRepositoryImpl(
    private val api: WikiApi,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) : WikiRepository {

    private val _favoriteIds = MutableStateFlow<Set<Long>>(emptySet())
    override val favoriteIds: StateFlow<Set<Long>> = _favoriteIds.asStateFlow()

    // Кэш только в памяти. Из списка приходят страницы без links, из detail-запроса: с ними.
    private val summaries = MutableStateFlow<Map<Long, WikiPage>>(emptyMap())
    private val details = MutableStateFlow<Map<Long, WikiPage>>(emptyMap())

    override suspend fun searchPages(
        query: String,
        offset: Int,
        limit: Int,
    ): Result<PagedResult<WikiPage>> = withContext(dispatcher) {
        api.search(query.trim().ifBlank { DEFAULT_QUERY }, offset, limit).map { dto ->
            val items = dto.query?.pages.orEmpty()
                .filter { !it.missing }
                .sortedBy { it.index }
                .map { it.toWikiPage() }
            summaries.update { it + items.associateBy(WikiPage::pageid) }
            PagedResult(items = items, hasMore = dto.continuation?.gsroffset != null)
        }
    }

    override suspend fun getPage(pageId: Long): Result<WikiPage?> {
        details.value[pageId]?.let { return Result.success(it) }
        return withContext(dispatcher) { api.fetchById(pageId).map { it.firstPageCached() } }
    }

    override suspend fun findPageByTitle(title: String): Result<WikiPage?> =
        withContext(dispatcher) { api.fetchByTitle(title).map { it.firstPageCached() } }

    override suspend fun getPages(pageIds: Set<Long>): Result<List<WikiPage>> =
        withContext(dispatcher) {
            val known = summaries.value + details.value
            val missing = pageIds.filter { it !in known }
            val fetched = mutableListOf<WikiPage>()
            for (chunk in missing.chunked(IDS_CHUNK)) {
                val result = api.fetchByIds(chunk)
                val error = result.exceptionOrNull()
                if (error != null) return@withContext Result.failure(error)
                fetched += result.getOrThrow().query?.pages.orEmpty()
                    .filter { !it.missing }
                    .map { it.toWikiPage() }
            }
            summaries.update { it + fetched.associateBy(WikiPage::pageid) }
            val all = known + fetched.associateBy(WikiPage::pageid)
            Result.success(pageIds.mapNotNull { all[it] })
        }

    override fun toggleFavorite(pageId: Long) {
        _favoriteIds.update { if (pageId in it) it - pageId else it + pageId }
    }

    private fun WikiResponseDto.firstPageCached(): WikiPage? =
        query?.pages.orEmpty()
            .firstOrNull { !it.missing }
            ?.toWikiPage()
            ?.also { page -> details.update { it + (page.pageid to page) } }
}
