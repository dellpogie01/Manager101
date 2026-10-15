package com.example.model

data class Category(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val modelCount: Int
)

data class ManagementModel(
    val id: String,
    val categoryIds: List<String>,
    val title: String,
    val abbreviation: String?,
    val categoryName: String,
    val definition: String,
    val purpose: String,
    val whenToUse: String,
    val steps: List<String>,
    val workplaceExample: String,
    val advantages: List<String>,
    val limitations: List<String>,
    val keyTakeaway: String,
    val relatedModelIds: List<String>,
    val illustrationType: String
)
