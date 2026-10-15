package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ManagementRepository
import com.example.model.ManagementModel
import com.example.ui.viewmodel.ModelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    viewModel: ModelViewModel,
    initialCategoryId: String? = null,
    onModelClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf(initialCategoryId) }

    val categories = viewModel.categories
    val allModels = viewModel.allModels
    val favorites by viewModel.favorites.collectAsState()

    val filteredModels = remember(searchQuery, selectedCategoryFilter) {
        var list = allModels
        if (!selectedCategoryFilter.isNullOrBlank()) {
            list = list.filter { it.categoryIds.contains(selectedCategoryFilter) }
        }
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.lowercase().trim()
            list = list.filter {
                it.title.lowercase().contains(q) ||
                        (it.abbreviation?.lowercase()?.contains(q) == true) ||
                        it.definition.lowercase().contains(q) ||
                        it.categoryName.lowercase().contains(q)
            }
        }
        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Manager101", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input with updated tip
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search by Name or Keyword...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.medium
            )

            // Category Filter Dropdown / Chips - "All Models" tab with no number
            ScrollableTabRow(
                selectedTabIndex = if (selectedCategoryFilter == null) 0 else categories.indexOfFirst { it.id == selectedCategoryFilter } + 1,
                edgePadding = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedCategoryFilter == null,
                    onClick = { selectedCategoryFilter = null },
                    text = { Text("All Models") }
                )
                categories.forEach { cat ->
                    Tab(
                        selected = selectedCategoryFilter == cat.id,
                        onClick = { selectedCategoryFilter = cat.id },
                        text = { Text("${cat.name.substringBefore(" &")} (${cat.modelCount})") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Model List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredModels) { model ->
                    ModelRowCard(
                        model = model,
                        isFavorite = favorites.contains(model.id),
                        onClick = { onModelClick(model.id) },
                        onFavoriteClick = { viewModel.toggleFavorite(model.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ModelRowCard(
    model: ManagementModel,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    val modelPinColors = listOf(
        Color(0xFFEF4444), // Coral Red
        Color(0xFF3B82F6), // Royal Blue
        Color(0xFF10B981), // Emerald
        Color(0xFFF59E0B), // Amber
        Color(0xFF8B5CF6), // Purple
        Color(0xFFEC4899), // Pink
        Color(0xFF06B6D4), // Cyan
        Color(0xFFF97316), // Orange
        Color(0xFF6366F1), // Indigo
        Color(0xFF14B8A6)  // Teal
    )
    val pinColor = modelPinColors[Math.floorMod(model.id.hashCode(), modelPinColors.size)]

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = pinColor.copy(alpha = 0.15f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.PushPin,
                        contentDescription = null,
                        tint = pinColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = model.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!model.abbreviation.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ) {
                        Text(
                            text = model.abbreviation,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = model.definition,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
