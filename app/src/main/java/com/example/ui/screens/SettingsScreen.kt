package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onSignOut: () -> Unit,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val context = LocalContext.current
    val auth = runCatching {
        if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseAuth.getInstance()
    }.getOrNull()
    val currentUser = runCatching { auth?.currentUser }.getOrNull()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Settings & Account") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Account Information", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Email: ${currentUser?.email ?: "Offline / Guest User"}")
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "UID: ${currentUser?.uid ?: "N/A"}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                runCatching { auth?.signOut() }
                                onSignOut()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(imageVector = Icons.Default.Logout, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sign Out")
                        }
                    }
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(text = "Legal & About", style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(onClick = onPrivacyClick, modifier = Modifier.fillMaxWidth()) {
                            Icon(imageVector = Icons.Default.PrivacyTip, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Privacy Policy")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(onClick = onTermsClick, modifier = Modifier.fillMaxWidth()) {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Terms of Use")
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Manager 101 v1.0.0 (180 Management Models)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}
