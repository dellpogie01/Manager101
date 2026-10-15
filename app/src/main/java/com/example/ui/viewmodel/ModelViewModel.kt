package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ManagementRepository
import com.example.data.ReadingProgressDbHelper
import com.example.model.Category
import com.example.model.ManagementModel
import com.example.model.ProgressStats
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelViewModel(application: Application) : AndroidViewModel(application) {
    val categories: List<Category> = ManagementRepository.categories
    val allModels: List<ManagementModel> = ManagementRepository.models

    private val dbHelper = ReadingProgressDbHelper(application)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<ManagementModel>>(emptyList())
    val searchResults: StateFlow<List<ManagementModel>> = _searchResults.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites.asStateFlow()

    private val _completedModelIds = MutableStateFlow<Set<String>>(emptySet())
    val completedModelIds: StateFlow<Set<String>> = _completedModelIds.asStateFlow()

    private val _progressStats = MutableStateFlow(ProgressStats.calculate(allModels.size, 0))
    val progressStats: StateFlow<ProgressStats> = _progressStats.asStateFlow()

    private val firestore = try {
        val dbId = application.getString(
            application.resources.getIdentifier("firestore_database_id", "string", application.packageName)
        )
        FirebaseFirestore.getInstance(dbId)
    } catch (e: Exception) {
        try {
            FirebaseFirestore.getInstance()
        } catch (ex: Exception) {
            null
        }
    }

    init {
        loadFavorites()
        refreshProgress()

        if (FirebaseApp.getApps(application).isNotEmpty()) {
            runCatching {
                FirebaseAuth.getInstance().addAuthStateListener {
                    loadFavorites()
                    refreshProgress()
                }
            }
        }
    }

    fun getUserId(): String {
        val uid = if (FirebaseApp.getApps(getApplication()).isNotEmpty()) {
            runCatching { FirebaseAuth.getInstance().currentUser?.uid }.getOrNull()
        } else {
            null
        }
        return uid ?: "offline_guest"
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _searchResults.value = ManagementRepository.searchModels(query)
    }

    fun toggleFavorite(modelId: String) {
        val current = _favorites.value.toMutableSet()
        if (current.contains(modelId)) {
            current.remove(modelId)
        } else {
            current.add(modelId)
        }
        _favorites.value = current
        saveFavoriteToCloud(modelId, current.contains(modelId))
    }

    private fun loadFavorites() {
        val uid = if (FirebaseApp.getApps(getApplication()).isNotEmpty()) {
            runCatching { FirebaseAuth.getInstance().currentUser?.uid }.getOrNull()
        } else {
            null
        } ?: return
        firestore?.collection("users")?.document(uid)?.collection("favorites")
            ?.get()
            ?.addOnSuccessListener { snapshot ->
                val favs = snapshot.documents.mapNotNull { it.id }.toSet()
                _favorites.value = favs
            }
            ?.addOnFailureListener {
                // Ignore offline errors gracefully
            }
    }

    private fun saveFavoriteToCloud(modelId: String, isFavorite: Boolean) {
        val uid = if (FirebaseApp.getApps(getApplication()).isNotEmpty()) {
            runCatching { FirebaseAuth.getInstance().currentUser?.uid }.getOrNull()
        } else {
            null
        } ?: return
        val docRef = firestore?.collection("users")?.document(uid)?.collection("favorites")?.document(modelId) ?: return
        if (isFavorite) {
            docRef.set(mapOf("timestamp" to com.google.firebase.Timestamp.now()))
        } else {
            docRef.delete()
        }
    }

    fun refreshProgress() {
        viewModelScope.launch {
            val userId = getUserId()
            val completed = try {
                withContext(Dispatchers.IO) { dbHelper.getCompletedModelIds(userId) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.w("ModelViewModel", "Could not load local reading progress", exception)
                emptySet()
            }
            // Count only distinct eligible models present in allModels
            val eligibleCompleted = completed.filter { id -> allModels.any { it.id == id } }.toSet()
            _completedModelIds.value = eligibleCompleted
            _progressStats.value = ProgressStats.calculate(allModels.size, eligibleCompleted.size)
        }
    }

    fun markModelCompleted(modelId: String) {
        // Only mark if it is an eligible model and not already marked
        if (!allModels.any { it.id == modelId }) return
        if (_completedModelIds.value.contains(modelId)) return

        viewModelScope.launch {
            val userId = getUserId()
            val saved = try {
                withContext(Dispatchers.IO) { dbHelper.markCompleted(userId, modelId) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (exception: Exception) {
                Log.w("ModelViewModel", "Could not save local reading progress", exception)
                false
            }
            if (!saved) return@launch
            val updated = _completedModelIds.value + modelId
            _completedModelIds.value = updated
            _progressStats.value = ProgressStats.calculate(allModels.size, updated.size)
        }
    }

    fun isModelCompleted(modelId: String): Boolean {
        return _completedModelIds.value.contains(modelId)
    }

    fun getCompletedModels(): List<ManagementModel> {
        val completedSet = _completedModelIds.value
        return allModels.filter { completedSet.contains(it.id) }
    }

    fun getUnreadModels(): List<ManagementModel> {
        val completedSet = _completedModelIds.value
        return allModels.filter { !completedSet.contains(it.id) }
    }
}
