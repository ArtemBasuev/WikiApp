package com.artem.wikiapp.data

data class WikiPage(
    val pageid: Long,
    val title: String,
    val extract: String,
    val fullurl: String,
    val length: Int,
    val touched: String,
    val categories: List<Category>,
    val links: List<PageLink>,
)

data class Category(
    val title: String
)

data class PageLink(
    val title: String
)