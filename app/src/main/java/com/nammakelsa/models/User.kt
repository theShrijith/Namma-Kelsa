package com.nammakelsa.models

/**
 * Base User representation for Authentication and Role management.
 */
data class User(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.CUSTOMER,
    val createdAt: Long = System.currentTimeMillis()
)
