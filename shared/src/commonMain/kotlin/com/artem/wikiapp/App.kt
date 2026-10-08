package com.artem.wikiapp

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.artem.wikiapp.data.WikiRepository
import com.artem.wikiapp.detail.WikiDetailEffect
import com.artem.wikiapp.detail.WikiDetailScreen
import com.artem.wikiapp.detail.WikiDetailViewModel
import com.artem.wikiapp.ui.AppContainer
import com.artem.wikiapp.favorites.WikiFavoritesEffect
import com.artem.wikiapp.favorites.WikiFavoritesScreen
import com.artem.wikiapp.favorites.WikiFavoritesViewModel
import com.artem.wikiapp.list.WikiListEffect
import com.artem.wikiapp.list.WikiListScreen
import com.artem.wikiapp.list.WikiListViewModel
import com.artem.wikiapp.navigation.WikiRoute
import com.artem.wikiapp.ui.SimpleViewModelFactory

@Composable
fun App(repository: WikiRepository = AppContainer.wikiRepository) {
    val systemInDarkTheme = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(systemInDarkTheme) }
    val colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()

    MaterialTheme(colorScheme = colorScheme) {
        val backStack = remember { mutableStateListOf<WikiRoute>(WikiRoute.List) }

        val listViewModel: WikiListViewModel = viewModel(
            factory = SimpleViewModelFactory { WikiListViewModel(repository) }
        )
        val listState by listViewModel.state.collectAsState()

        LaunchedEffect(listViewModel) {
            listViewModel.effects.collect { effect ->
                when (effect) {
                    is WikiListEffect.NavigateToDetail -> backStack.add(WikiRoute.Detail(effect.pageId))
                }
            }
        }

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
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
                    val viewModel: WikiDetailViewModel = viewModel(
                        factory = SimpleViewModelFactory { WikiDetailViewModel(route.pageId, repository) }
                    )
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(viewModel) {
                        viewModel.effects.collect { effect ->
                            when (effect) {
                                is WikiDetailEffect.NavigateToDetail ->
                                    backStack.add(WikiRoute.Detail(effect.pageId))
                            }
                        }
                    }

                    WikiDetailScreen(
                        state = state,
                        onIntent = viewModel::onIntent,
                        onBackClick = { backStack.removeLastOrNull() },
                    )
                }

                entry<WikiRoute.Favorites> {
                    val viewModel: WikiFavoritesViewModel = viewModel(
                        factory = SimpleViewModelFactory { WikiFavoritesViewModel(repository) }
                    )
                    val state by viewModel.state.collectAsState()

                    LaunchedEffect(viewModel) {
                        viewModel.effects.collect { effect ->
                            when (effect) {
                                is WikiFavoritesEffect.NavigateToDetail ->
                                    backStack.add(WikiRoute.Detail(effect.pageId))
                            }
                        }
                    }

                    WikiFavoritesScreen(
                        state = state,
                        onIntent = viewModel::onIntent,
                        onBackClick = { backStack.removeLastOrNull() },
                    )
                }
            }
        )
    }
}