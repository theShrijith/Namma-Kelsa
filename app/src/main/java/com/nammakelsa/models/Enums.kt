package com.nammakelsa.models

/**
 * User role selection.
 */
enum class UserRole {
    CUSTOMER,
    WORKER
}

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
 * Types of notifications.
 */
enum class NotificationType {
    INFO,
    SUCCESS,
    WARNING
}
