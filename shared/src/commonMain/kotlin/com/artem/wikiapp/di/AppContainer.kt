package com.artem.wikiapp.di

import com.artem.wikiapp.data.MockWikiRepository
import com.artem.wikiapp.data.WikiRepository

object AppContainer {
    val wikiRepository: WikiRepository by lazy { MockWikiRepository() }
}