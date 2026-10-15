package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.example.ui.components.ModelIllustration
import com.example.ui.viewmodel.ModelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDetailScreen(
    modelId: String,
    viewModel: ModelViewModel,
    onBackClick: () -> Unit,
    onRelatedModelClick: (String) -> Unit
) {
    val model = ManagementRepository.getModelById(modelId)
    val favorites by viewModel.favorites.collectAsState()
    val completedIds by viewModel.completedModelIds.collectAsState()
    val isFavorite = model != null && favorites.contains(model.id)
    val isCompleted = model != null && completedIds.contains(model.id)

    val listState = rememberLazyListState()

    // Detect when the user reaches the end of the content
    val isAtEnd by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            if (totalItems == 0) return@derivedStateOf false
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull() ?: return@derivedStateOf false
            lastVisibleItem.index == totalItems - 1
        }
    }

    LaunchedEffect(isAtEnd, isCompleted, model?.id) {
        if (model != null && isAtEnd && !isCompleted) {
            viewModel.markModelCompleted(model.id)
        }
    }

    if (model == null) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Model Not Found") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Requested management model could not be found.")
            }
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = model.title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isCompleted) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = Color(0xFFE8F5E9),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Read",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.toggleFavorite(model.id) }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info & Illustration Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = model.categoryName,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (!model.abbreviation.isNullOrBlank()) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                                ) {
                                    Text(text = model.abbreviation, modifier = Modifier.padding(horizontal = 4.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = model.definition,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Custom Vector Canvas Illustration
                        ModelIllustration(illustrationType = model.illustrationType)
                    }
                }
            }

            // Purpose & When to Use
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Purpose", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = model.purpose, style = MaterialTheme.typography.bodyMedium)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = "When to Use", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = model.whenToUse, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Step-by-Step Explanation
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Step-by-Step Explanation", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        model.steps.forEachIndexed { index, step ->
                            Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                Text(
                                    text = "${index + 1}. ",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(text = step, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            // Practical Workplace Example
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Practical Workplace Example", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = model.workplaceExample, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Advantages & Limitations
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Advantages", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            model.advantages.forEach { adv ->
                                Text(text = "• $adv", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "Limitations", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            model.limitations.forEach { lim ->
                                Text(text = "• $lim", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(bottom = 4.dp))
                            }
                        }
                    }
                }
            }

            // Key Takeaway
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Key Takeaway", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = model.keyTakeaway, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // Related Models
            if (model.relatedModelIds.isNotEmpty()) {
                item {
                    Text(text = "Related Models", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                items(model.relatedModelIds) { relId ->
                    val relModel = ManagementRepository.getModelById(relId)
                    if (relModel != null) {
                        OutlinedCard(
                            onClick = { onRelatedModelClick(relModel.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = relModel.title, fontWeight = FontWeight.SemiBold)
                                    Text(text = relModel.definition, style = MaterialTheme.typography.bodySmall, maxLines = 1)
                                }
                                Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null)
                            }
                        }
                    }
                }
            }

            // Reading Completion Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isCompleted) "Completed Reading" else "Reading in Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isCompleted) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isCompleted)
                                    "You have completed reading this management model."
                                else
                                    "Scroll to the end to mark this model as completed.",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isCompleted) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
