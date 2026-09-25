package com.artem.wikiapp.list


sealed interface WikiListIntent {
    data class PageClicked(val pageId: Long) : WikiListIntent
    data class QueryChanged(val value: String) : WikiListIntent

    data object LoadMore : WikiListIntent

    data class FavoriteToggled(val pageId: Long) : WikiListIntent
}