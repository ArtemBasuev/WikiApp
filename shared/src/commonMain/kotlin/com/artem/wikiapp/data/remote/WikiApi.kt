package com.artem.wikiapp.data.remote

import com.artem.wikiapp.core.suspendRunCatching
import com.artem.wikiapp.data.remote.dto.WikiResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WikiApiException(message: String) : Exception(message)

class WikiApi(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {

    suspend fun search(query: String, offset: Int, limit: Int): Result<WikiResponseDto> = request {
        parameter("generator", "search")
        parameter("gsrsearch", query)
        parameter("gsrnamespace", 0)
        parameter("gsrlimit", limit)
        parameter("gsroffset", offset)
        pageProps(withLinks = false)
    }

    suspend fun browse(cursor: String?, limit: Int): Result<WikiResponseDto> = request {
        parameter("generator", "allpages")
        parameter("gapnamespace", 0)
        parameter("gapfilterredir", "nonredirects")
        parameter("gaplimit", limit)
        if (cursor != null) parameter("gapcontinue", cursor)
        pageProps(withLinks = false)
    }

    suspend fun fetchById(pageId: Long): Result<WikiResponseDto> = request {
        parameter("pageids", pageId)
        pageProps(withLinks = true)
    }

    suspend fun fetchByTitle(title: String): Result<WikiResponseDto> = request {
        parameter("titles", title)
        parameter("redirects", 1)
        pageProps(withLinks = true)
    }

    suspend fun fetchByIds(pageIds: Collection<Long>): Result<WikiResponseDto> = request {
        parameter("pageids", pageIds.joinToString("|"))
        pageProps(withLinks = false)
    }

    private suspend fun request(configure: HttpRequestBuilder.() -> Unit): Result<WikiResponseDto> =
        withContext(dispatcher) {
            suspendRunCatching {
                val dto: WikiResponseDto = client.get("api.php") {
                    parameter("action", "query")
                    parameter("format", "json")
                    parameter("formatversion", 2)
                    parameter("origin", "*")
                    configure()
                }.body()
                dto.error?.let { throw WikiApiException(it.info.ifBlank { it.code }) }
                dto
            }
        }

    private fun HttpRequestBuilder.pageProps(withLinks: Boolean) {
        parameter("prop", if (withLinks) "extracts|info|categories|links" else "extracts|info|categories")
        parameter("exintro", 1)
        parameter("explaintext", 1)
        parameter("exlimit", "max")
        parameter("inprop", "url")
        parameter("clshow", "!hidden")
        parameter("cllimit", "max")
        if (withLinks) {
            parameter("plnamespace", 0)
            parameter("pllimit", LINKS_LIMIT)
        }
    }

    private companion object {
        const val LINKS_LIMIT = 20
    }
}
