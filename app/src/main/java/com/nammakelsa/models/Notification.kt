package com.nammakelsa.models

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
