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
import org.jetbrains.compose.resources.stringResource
import wikiapp.shared.generated.resources.Res
import wikiapp.shared.generated.resources.add_to_favorites
import wikiapp.shared.generated.resources.app_name
import wikiapp.shared.generated.resources.favorites_title
import wikiapp.shared.generated.resources.loading_more
import wikiapp.shared.generated.resources.no_results
import wikiapp.shared.generated.resources.remove_from_favorites
import wikiapp.shared.generated.resources.search_hint
import wikiapp.shared.generated.resources.switch_theme

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
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            state.items.isNotEmpty() && lastVisible >= state.items.size - 3
        }
    }

    LaunchedEffect(shouldLoadMore, state.canLoadMore, state.isLoadingMore) {
        if (shouldLoadMore && state.canLoadMore && !state.isLoadingMore) {
            onIntent(WikiListIntent.LoadMore)
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
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_results),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
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
                        item {
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
                    }
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
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