package com.artem.wikiapp.detail

sealed interface WikiDetailEffect {
    data class NavigateToDetail(val pageId: Long) : WikiDetailEffect
}
