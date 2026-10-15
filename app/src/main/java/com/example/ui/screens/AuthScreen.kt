package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.google.firebase.FirebaseApp
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onContinueOffline: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Silent auto-sign-in on startup for returning users
    LaunchedEffect(Unit) {
        val auth = getFirebaseAuthOrNull(context) ?: return@LaunchedEffect
        if (auth.currentUser != null) {
            onAuthSuccess()
            return@LaunchedEffect
        }
        attemptSilentSignIn(
            context = context,
            scope = coroutineScope,
            onSuccess = onAuthSuccess
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Vertical phone background image matching attached reference
        Image(
            painter = painterResource(id = R.drawable.signin_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Subtle gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x220A1128))
        )

        // Centered Sign-in card
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // App Logo with yellow lightbulb badge
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Manager101 Logo",
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(22.dp))
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Manager 101",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Executive Reference Guide to 180 Management Models & Strategies",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    if (errorMessage != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = errorMessage.orEmpty(),
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // One-time Google Sign-In Button
                    Button(
                        onClick = {
                            isLoading = true
                            errorMessage = null
                            performGoogleSignIn(
                                context = context,
                                scope = coroutineScope,
                                onSuccess = {
                                    isLoading = false
                                    onAuthSuccess()
                                },
                                onError = { err ->
                                    isLoading = false
                                    errorMessage = err
                                },
                                onCancelled = {
                                    isLoading = false
                                }
                            )
                        },
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sign in with Google",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onContinueOffline,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("Continue Offline")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "One-time sign in. Your account and favorites will be permanently preserved.",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun attemptSilentSignIn(
    context: Context,
    scope: CoroutineScope,
    onSuccess: () -> Unit
) {
    if (getFirebaseAuthOrNull(context) == null) return
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        return
    }

    if (clientId.isBlank() || clientId == "placeholder_client_id") return

    scope.launch {
        try {
            val credentialManager = CredentialManager.create(context)
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(true)
                .build()
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            val result = credentialManager.getCredential(context, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                getFirebaseAuthOrNull(context)?.signInWithCredential(authCredential)?.await() ?: return@launch
                onSuccess()
            }
        } catch (e: Exception) {
            // Silent sign-in not available, await user tap
        }
    }
}

private fun performGoogleSignIn(
    context: Context,
    scope: CoroutineScope,
    onSuccess: () -> Unit,
    onError: (String) -> Unit,
    onCancelled: () -> Unit
) {
    if (getFirebaseAuthOrNull(context) == null) {
        onError("Sign-in is unavailable because Firebase is not configured. Continue offline to browse the library.")
        return
    }
    val clientId = try {
        context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
        onError("Google Sign-In configuration missing: default_web_client_id")
        return
    }

    scope.launch {
        try {
            val credentialManager = CredentialManager.create(context)
            val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
            val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()
            val result = credentialManager.getCredential(context as Activity, request)
            val credential = result.credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val googleIdToken = googleIdTokenCredential.idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                getFirebaseAuthOrNull(context)?.signInWithCredential(authCredential)?.await()
                    ?: throw IllegalStateException("Firebase sign-in is unavailable.")
                onSuccess()
            } else {
                onError("Unexpected credential type received.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.w("Auth", "Google Sign-In cancelled: ${e.message}")
            onCancelled()
        } catch (e: Exception) {
            Log.e("Auth", "Google Sign-In failed", e)
            onError(e.localizedMessage ?: "Sign in failed. Please check internet connection.")
        }
    }
}

private fun getFirebaseAuthOrNull(context: Context): FirebaseAuth? =
    runCatching {
        if (FirebaseApp.getApps(context).isEmpty()) null else FirebaseAuth.getInstance()
    }.getOrNull()
