package com.artem.wikiapp.detail

sealed interface WikiDetailIntent {
    data object FavoriteToggled : WikiDetailIntent
    data class LinkClicked(val title: String) : WikiDetailIntent
}
