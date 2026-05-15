package com.nammakelsa.state

import com.nammakelsa.models.User

/**
 * Centralized Auth State for the application.
 */
sealed class AuthState {
    /** Initial state — session check has not completed yet. */
    object Initializing : AuthState()

    /** No authenticated user; navigate to Role Selection. */
    object Unauthenticated : AuthState()

    /** Authenticated customer. */
    data class AuthenticatedCustomer(val user: User) : AuthState()

    /** Authenticated worker. */
    data class AuthenticatedWorker(val user: User) : AuthState()
}
