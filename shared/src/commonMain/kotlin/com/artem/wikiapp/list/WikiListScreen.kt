package com.artem.wikiapp.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.artem.wikiapp.data.WikiPage
import com.artem.wikiapp.ui.AppScaffold
import com.artem.wikiapp.ui.CategoryChip
import com.artem.wikiapp.ui.ErrorBox
import com.artem.wikiapp.ui.LoadingBox
import org.jetbrains.compose.resources.stringResource
import wikiapp.shared.generated.resources.Res
import wikiapp.shared.generated.resources.add_to_favorites
import wikiapp.shared.generated.resources.app_name
import wikiapp.shared.generated.resources.error_generic
import wikiapp.shared.generated.resources.favorites_title
import wikiapp.shared.generated.resources.loading_more
import wikiapp.shared.generated.resources.no_results
import wikiapp.shared.generated.resources.remove_from_favorites
import wikiapp.shared.generated.resources.retry
import wikiapp.shared.generated.resources.search_hint
import wikiapp.shared.generated.resources.switch_theme
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue


@Composable
fun WikiListScreen(
    state: WikiListState,
    onIntent: (WikiListIntent) -> Unit,
    onFavoritesClick: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val favoritesLabel = stringResource(Res.string.favorites_title)
    val themeLabel = stringResource(Res.string.switch_theme)
    val itemCount = state.items.size
    val focusManager = LocalFocusManager.current
    var lastFirstId by rememberSaveable { mutableStateOf(state.items.firstOrNull()?.pageid) }
    val shouldLoadMore by remember(itemCount) {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            itemCount > 0 && lastVisible >= itemCount - 3
        }
    }


    LaunchedEffect(shouldLoadMore, state.canLoadMore, state.isLoadingMore, state.loadMoreFailed) {
        if (shouldLoadMore && state.canLoadMore && !state.isLoadingMore && !state.loadMoreFailed) {
            onIntent(WikiListIntent.LoadMore)
        }
    }

    LaunchedEffect(state.items.firstOrNull()?.pageid) {
        val firstId = state.items.firstOrNull()?.pageid
        if (firstId != lastFirstId) {
            lastFirstId = firstId
            listState.scrollToItem(0)
        }
    }

    AppScaffold(
        title = stringResource(Res.string.app_name),
        actions = {
            Text(
                text = if (isDarkTheme) "☀" else "☾",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable { onThemeToggle() }
                    .semantics { contentDescription = themeLabel }
                    .padding(12.dp)
            )
            Text(
                text = "♥",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .clickable { onFavoritesClick() }
                    .semantics { contentDescription = favoritesLabel }
                    .padding(12.dp)
            )
        },
        modifier = modifier
    ) { innerModifier ->
        Column(
            modifier = innerModifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = state.query,
                onValueChange = { onIntent(WikiListIntent.QueryChanged(it)) },
                label = { Text(stringResource(Res.string.search_hint)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.items.isNotEmpty() -> LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = state.items,
                        key = { it.pageid }
                    ) { page ->
                        WikiItemCard(
                            page = page,
                            isFavorite = page.pageid in state.favoriteIds,
                            onClick = { onIntent(WikiListIntent.PageClicked(page.pageid)) },
                            onFavoriteClick = { onIntent(WikiListIntent.FavoriteToggled(page.pageid)) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (state.isLoadingMore) {
                        item(key = "loading-more") {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                                Text(
                                    text = stringResource(Res.string.loading_more),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (state.loadMoreFailed) {
                        item(key = "load-more-error") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = stringResource(Res.string.error_generic),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(onClick = { onIntent(WikiListIntent.RetryRequested) }) {
                                    Text(stringResource(Res.string.retry))
                                }
                            }
                        }
                    }
                }

                state.isLoading -> LoadingBox()

                state.hasError -> ErrorBox(onRetry = { onIntent(WikiListIntent.RetryRequested) })

                else -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_results),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WikiItemCard(
    page: WikiPage,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val favoriteLabel = stringResource(
        if (isFavorite) Res.string.remove_from_favorites else Res.string.add_to_favorites
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (isFavorite) "★" else "☆",
                    style = MaterialTheme.typography.titleLarge,
                    color = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clickable { onFavoriteClick() }
                        .semantics { contentDescription = favoriteLabel }
                        .padding(start = 8.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = page.extract,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (page.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    page.categories.take(2).forEach { category ->
                        val formattedCategory = category.title.removePrefix("Category:").trim()
                        CategoryChip(label = formattedCategory)
                    }
                }
            }
        }
    }
}