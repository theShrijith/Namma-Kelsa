package com.nammakelsa.models

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
