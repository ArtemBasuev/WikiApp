package com.artem.wikiapp.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import wikiapp.shared.generated.resources.Res
import wikiapp.shared.generated.resources.back
import wikiapp.shared.generated.resources.favorites_title
import wikiapp.shared.generated.resources.no_favorites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiFavoritesScreen(
    state: WikiFavoritesState,
    onIntent: (WikiFavoritesIntent) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val favoritePages = state.pages
    val backLabel = stringResource(Res.string.back)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.favorites_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.semantics { contentDescription = backLabel }
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (favoritePages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    text = stringResource(Res.string.no_favorites),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            items(items = favoritePages, key = { it.pageid }) { page ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onIntent(WikiFavoritesIntent.PageClicked(page.pageid)) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = page.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = page.extract,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}