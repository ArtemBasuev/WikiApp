package com.artem.wikiapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WikiResponseDto(
    val query: WikiQueryDto? = null,
    @SerialName("continue")
    val continuation: WikiContinueDto? = null,
    val error: WikiErrorDto? = null,
)

@Serializable
data class WikiQueryDto(val pages: List<WikiPageDto> = emptyList())

@Serializable
data class WikiContinueDto(val gsroffset: Int? = null)

@Serializable
data class WikiErrorDto(val code: String = "", val info: String = "")

@Serializable
data class WikiPageDto(
    val pageid: Long = 0,
    val title: String = "",
    val index: Int = 0,
    val missing: Boolean = false,
    val extract: String = "",
    val fullurl: String = "",
    val length: Int = 0,
    val touched: String = "",
    val categories: List<WikiTitleDto> = emptyList(),
    val links: List<WikiTitleDto> = emptyList(),
)

@Serializable
data class WikiTitleDto(val title: String)
