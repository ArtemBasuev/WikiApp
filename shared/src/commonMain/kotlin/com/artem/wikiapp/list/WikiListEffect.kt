package com.artem.wikiapp.list

sealed interface WikiListEffect {
    data class NavigateToDetail(val pageId: Long) : WikiListEffect
}