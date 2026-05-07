package com.nammakelsa.data

/**
 * Represents a worker in the marketplace.
 * UI-only model — no backend persistence.
 */
data class Worker(
    val id: Int,
    val name: String,
    val phone: String,
    val skill: String,
    val dailyRate: Int,
    val distance: String,
    val isAvailable: Boolean,
    val rating: Float = 4.5f,
    val workerId: String = "",
    val profileImageUrl: String? = null,
    val galleryImages: List<String> = emptyList(),
    val about: String = ""
)

/**
 * Represents a customer user.
 */
data class Customer(
    val id: Int = 0,
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)

/**
 * Represents a work request sent by a customer to a worker.
 */
data class WorkRequest(
    val id: Int,
    val customerName: String,
    val workType: String,
    val description: String,
    val location: String,
    val date: String,
    val budget: String,
    val notes: String = "",
    val status: RequestStatus = RequestStatus.PENDING
)

/**
 * Status of a work request.
 */
enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED
}

/**
 * User role selection.
 */
enum class UserRole {
    CUSTOMER,
    WORKER
}

/**
 * Represents a notification.
 */
data class Notification(
    val id: Int,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean,
    val type: NotificationType
)

/**
 * Types of notifications.
 */
enum class NotificationType {
    INFO,
    SUCCESS,
    WARNING
}

/**
 * Available skill categories.
 */
object Skills {
    val all = listOf(
        "Painter",
        "Plumber",
        "Electrician",
        "Gardener",
        "Carpenter",
        "Mason",
        "Cleaner",
        "Welder"
    )
}
