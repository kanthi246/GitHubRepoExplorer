package com.kanthi.githubrepoexplorer.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.kanthi.githubrepoexplorer.R
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.presentation.components.EmptyState
import com.kanthi.githubrepoexplorer.presentation.components.FullScreenError
import com.kanthi.githubrepoexplorer.presentation.components.FullScreenLoading
import com.kanthi.githubrepoexplorer.presentation.components.PaginationErrorRow
import com.kanthi.githubrepoexplorer.presentation.components.PaginationLoadingRow
import com.kanthi.githubrepoexplorer.presentation.components.RepositoryListItem

/**
 * The Search screen — the app's home/start destination (see AppNavGraph.kt). Lets the user type a
 * search query, filter by sort order and language, browse recent search history when the query is
 * empty, and scroll through paginated results.
 *
 * Note this screen uses `LazyPagingItems`/`collectAsLazyPagingItems()` (from Paging3) instead of
 * a plain list — see data/paging/RepositorySearchPagingSource.kt for why. See DetailsScreen for
 * how screen + ViewModel pairs work in general.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onRepositoryClick: (Repository) -> Unit,
    onFavoritesClick: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val query by viewModel.query.collectAsStateWithLifecycle()
    val sortOption by viewModel.sortOption.collectAsStateWithLifecycle()
    val languageFilter by viewModel.languageFilter.collectAsStateWithLifecycle()
    val searchHistory by viewModel.searchHistory.collectAsStateWithLifecycle()
    val searchKey by viewModel.searchKey.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.app_name)) },
                actions = {
                    IconButton(onClick = onFavoritesClick) {
                        Icon(Icons.Filled.Favorite, contentDescription = stringResource(R.string.favorites))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
            )
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            SearchBar(
                query = query,
                onQueryChanged = viewModel::onQueryChanged,
                onSearchSubmitted = viewModel::onSearchSubmitted,
            )
            FilterBar(
                sortOption = sortOption,
                onSortSelected = viewModel::onSortSelected,
                languageFilter = languageFilter,
                onLanguageSelected = viewModel::onLanguageSelected,
            )
            HorizontalDivider()

            when {
                query.isBlank() -> SearchHistoryList(
                    history = searchHistory,
                    onHistoryItemClick = viewModel::onHistoryItemSelected,
                    onClearHistory = viewModel::onClearHistory,
                )

                else -> key(searchKey) {
                    val repositories = viewModel.repositories.collectAsLazyPagingItems()
                    RepositoryResultsList(
                        repositories = repositories,
                        onRepositoryClick = onRepositoryClick,
                        onToggleFavorite = viewModel::onToggleFavorite,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchSubmitted: () -> Unit,
) {
    TextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(Icons.Filled.Clear, contentDescription = null)
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearchSubmitted() }),
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
        ),
    )
}

@Composable
private fun FilterBar(
    sortOption: SortOption,
    onSortSelected: (SortOption) -> Unit,
    languageFilter: String?,
    onLanguageSelected: (String?) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SortDropdown(sortOption, onSortSelected)
        LanguageDropdown(languageFilter, onLanguageSelected)
    }
}

@Composable
private fun SortDropdown(selected: SortOption, onSelected: (SortOption) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text("${stringResource(R.string.sort_by)}: ${selected.label}")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            SortOption.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.label) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

private val commonLanguages = listOf("Kotlin", "Java", "Python", "JavaScript", "TypeScript", "Swift", "Go", "Rust", "C++")

@Composable
private fun LanguageDropdown(selected: String?, onSelected: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        TextButton(onClick = { expanded = true }) {
            Text(selected ?: stringResource(R.string.all_languages))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.all_languages)) },
                onClick = {
                    onSelected(null)
                    expanded = false
                },
            )
            commonLanguages.forEach { language ->
                DropdownMenuItem(
                    text = { Text(language) },
                    onClick = {
                        onSelected(language)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchHistoryList(
    history: List<String>,
    onHistoryItemClick: (String) -> Unit,
    onClearHistory: () -> Unit,
) {
    if (history.isEmpty()) {
        EmptyState(message = stringResource(R.string.search_empty_query))
        return
    }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(stringResource(R.string.recent_searches), style = MaterialTheme.typography.labelMedium)
            TextButton(onClick = onClearHistory) { Text(stringResource(R.string.clear_history)) }
        }
        LazyColumn {
            items(history) { historyQuery ->
                ListItem(
                    headlineContent = { Text(historyQuery, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                    leadingContent = { Icon(Icons.Filled.History, contentDescription = null) },
                    modifier = Modifier.clickable { onHistoryItemClick(historyQuery) },
                )
            }
        }
    }
}

@Composable
private fun RepositoryResultsList(
    repositories: LazyPagingItems<Repository>,
    onRepositoryClick: (Repository) -> Unit,
    onToggleFavorite: (Repository) -> Unit,
) {
    val refreshState = repositories.loadState.refresh

    when {
        refreshState is LoadState.Loading && repositories.itemCount == 0 -> FullScreenLoading()

        refreshState is LoadState.Error && repositories.itemCount == 0 -> FullScreenError(
            message = refreshState.error.localizedMessage ?: stringResource(R.string.error_generic),
            onRetry = { repositories.retry() },
        )

        repositories.itemCount == 0 && refreshState is LoadState.NotLoading -> EmptyState(
            message = stringResource(R.string.search_no_results),
        )

        else -> LazyColumn(contentPadding = PaddingValues(vertical = 4.dp)) {
            items(count = repositories.itemCount, key = repositories.itemKey { it.id }) { index ->
                val repository = repositories[index]
                if (repository != null) {
                    RepositoryListItem(
                        repository = repository,
                        onClick = { onRepositoryClick(repository) },
                        onToggleFavorite = { onToggleFavorite(repository) },
                    )
                }
            }

            val appendState = repositories.loadState.append
            if (appendState is LoadState.Loading) {
                item { PaginationLoadingRow() }
            }
            if (appendState is LoadState.Error) {
                item {
                    PaginationErrorRow(
                        message = appendState.error.localizedMessage ?: stringResource(R.string.error_generic),
                        onRetry = { repositories.retry() },
                    )
                }
            }
        }
    }
}
