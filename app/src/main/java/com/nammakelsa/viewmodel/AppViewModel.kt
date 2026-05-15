package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nammakelsa.models.UserRole

/**
 * Shared ViewModel — holds global UI state such as theme and role selection.
 * No backend logic — purely drives the composable screens.
 */
class AppViewModel : ViewModel() {

    // ── Theme / Settings ────────────────────────────────────────────
    var isDarkMode by mutableStateOf(false)
        private set

    fun toggleDarkMode() {
        isDarkMode = !isDarkMode
    }

    fun updateDarkMode(enabled: Boolean) {
        isDarkMode = enabled
    }

    // ── Role Selection ──────────────────────────────────────────────
    var selectedRole by mutableStateOf<UserRole?>(null)
        private set

    fun selectRole(role: UserRole) {
        selectedRole = role
    }

    fun clearRole() {
        selectedRole = null
    }
}
