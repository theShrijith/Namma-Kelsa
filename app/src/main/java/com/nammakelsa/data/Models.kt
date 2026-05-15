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

/**
 * Represents a customer user.
 */
data class Customer(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val profileImageUrl: String? = null
)

/**
 * Base User representation for Authentication and Role management.
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a work request sent by a customer to a worker.
 */
data class WorkRequest(
    val id: String = "",
    val customerId: String = "",
    val workerId: String = "",
    val customerName: String = "",
    val workType: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val budget: String = "",
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
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val message: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    val type: NotificationType = NotificationType.INFO
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
