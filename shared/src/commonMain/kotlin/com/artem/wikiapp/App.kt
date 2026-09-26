package com.artem.wikiapp

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.artem.wikiapp.data.FavoritesStore
import com.artem.wikiapp.data.mockWikiPages
import com.artem.wikiapp.detail.WikiDetailScreen
import com.artem.wikiapp.favorites.WikiFavoritesScreen
import com.artem.wikiapp.list.WikiListScreen
import com.artem.wikiapp.list.WikiListViewModel
import com.artem.wikiapp.list.WikiListViewModelFactory
import com.artem.wikiapp.navigation.WikiRoute

@Composable
fun App() {
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    // Единственное место в дереве композиции, где мы подписываемся
    // на глобальный FavoritesStore. Дальше favoriteIds передаётся
    // вниз как обычный параметр состояния — как и всё остальное состояние.
    val favoriteIds by FavoritesStore.favoriteIds.collectAsState()

    MaterialTheme(colorScheme = colorScheme) {
        val backStack = remember { mutableStateListOf<WikiRoute>(WikiRoute.List) }

        val listViewModel: WikiListViewModel = viewModel(
            factory = WikiListViewModelFactory(
                onNavigateToDetail = { pageId ->
                    backStack.add(WikiRoute.Detail(pageId))
                }
            )
        )
        val listState by listViewModel.state.collectAsState()

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            transitionSpec = {
                slideInHorizontally(initialOffsetX = { it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { -it })
            },
            popTransitionSpec = {
                slideInHorizontally(initialOffsetX = { -it }) togetherWith
                        slideOutHorizontally(targetOffsetX = { it })
            },
            entryProvider = entryProvider {
                entry<WikiRoute.List> {
                    WikiListScreen(
                        state = listState,
                        onIntent = listViewModel::onIntent,
                        onFavoritesClick = { backStack.add(WikiRoute.Favorites) },
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }

                entry<WikiRoute.Detail> { route ->
                    val page = mockWikiPages.find { it.pageid == route.pageId }

                    WikiDetailScreen(
                        page = page,
                        isFavorite = page != null && page.pageid in favoriteIds,
                        onFavoriteToggle = { page?.let { FavoritesStore.toggle(it.pageid) } },
                        onBackClick = { backStack.removeLastOrNull() },
                        onLinkClick = { linkedTitle ->
                            val linkedPage = mockWikiPages.find {
                                it.title.equals(linkedTitle, ignoreCase = true)
                            }
                            if (linkedPage != null) {
                                backStack.add(WikiRoute.Detail(linkedPage.pageid))
                            }
                        }
                    )
                }

                entry<WikiRoute.Favorites> {
                    WikiFavoritesScreen(
                        favoriteIds = favoriteIds,
                        onBackClick = { backStack.removeLastOrNull() },
                        onPageClick = { pageId -> backStack.add(WikiRoute.Detail(pageId)) }
                    )
                }
            }
        )
    }
}