package com.nammakelsa.models

/**
 * Represents a worker in the marketplace.
 */
data class Worker(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val skill: String = "",
    val dailyRate: Int = 0,
    val distance: String = "",
    val isAvailable: Boolean = false,
    val rating: Float = 4.5f,
    val workerId: String = "",
    val profileImageUrl: String? = null,
    val galleryImages: List<String> = emptyList(),
    val about: String = ""
)
