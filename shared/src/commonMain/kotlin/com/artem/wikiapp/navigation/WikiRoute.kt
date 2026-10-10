package com.artem.wikiapp.navigation

sealed interface WikiRoute {
    data object List : WikiRoute
    data class Detail(val pageId: Long) : WikiRoute
    data object Favorites : WikiRoute
}