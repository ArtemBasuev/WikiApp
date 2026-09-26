package com.artem.wikiapp.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.artem.wikiapp.data.WikiPage
import com.artem.wikiapp.ui.CategoryChip
import org.jetbrains.compose.resources.stringResource

import wikiapp.shared.generated.resources.Res
import wikiapp.shared.generated.resources.add_to_favorites
import wikiapp.shared.generated.resources.article_meta
import wikiapp.shared.generated.resources.article_not_found
import wikiapp.shared.generated.resources.article_source
import wikiapp.shared.generated.resources.back
import wikiapp.shared.generated.resources.details_title
import wikiapp.shared.generated.resources.links_title
import wikiapp.shared.generated.resources.remove_from_favorites

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WikiDetailScreen(
    page: WikiPage?,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onBackClick: () -> Unit,
    onLinkClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backLabel = stringResource(Res.string.back)
    val favoriteLabel = stringResource(
        if (isFavorite) Res.string.remove_from_favorites else Res.string.add_to_favorites
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text(
                            text = "←",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.semantics { contentDescription = backLabel }
                        )
                    }
                },
                actions = {
                    if (page != null) {
                        IconButton(onClick = onFavoriteToggle) {
                            Text(
                                text = if (isFavorite) "★" else "☆",
                                style = MaterialTheme.typography.titleLarge,
                                color = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics { contentDescription = favoriteLabel }
                            )
                        }
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (page == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    text = stringResource(Res.string.article_not_found),
                    modifier = Modifier.padding(16.dp)
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(Res.string.article_meta, page.length, formatTouched(page.touched)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (page.categories.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    page.categories.forEach { category ->
                        val formattedCategory = category.title.removePrefix("Category:").trim()
                        CategoryChip(label = formattedCategory)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text(
                text = page.extract,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (page.links.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(Res.string.links_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                page.links.forEach { link ->
                    Text(
                        text = "• ${link.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLinkClick(link.title) }
                            .padding(vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.article_source, page.fullurl),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

private fun formatTouched(touched: String): String {
    val datePart = touched.substringBefore("T")
    val timePart = touched.substringAfter("T").removeSuffix("Z").substringBeforeLast(":")
    val (year, month, day) = datePart.split("-")
    return "$day.$month.$year $timePart"
}