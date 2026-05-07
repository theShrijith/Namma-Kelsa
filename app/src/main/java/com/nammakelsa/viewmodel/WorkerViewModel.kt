package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.nammakelsa.data.RequestStatus
import com.nammakelsa.data.WorkRequest

/**
 * ViewModel for Worker-specific UI state.
 * Manages availability, requests, profile, and registration.
 */
class WorkerViewModel : ViewModel() {

    // ── Auth State ──────────────────────────────────────────────────
    var loginWorkerId by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    fun onLoginWorkerIdChange(value: String) { loginWorkerId = value.uppercase() }
    fun onLoginPasswordChange(value: String) { loginPassword = value }

    // ── Registration State ──────────────────────────────────────────
    var registerName by mutableStateOf("")
        private set
    var registerPhone by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerLocation by mutableStateOf("")
        private set
    var registerDailyRate by mutableStateOf("")
        private set
    var registerSelectedSkills by mutableStateOf<Set<String>>(emptySet())
        private set
    var registrationComplete by mutableStateOf(false)
        private set
    var generatedWorkerId by mutableStateOf("")
        private set

    fun onRegisterNameChange(value: String) { registerName = value }
    fun onRegisterPhoneChange(value: String) { registerPhone = value.filter { it.isDigit() }.take(10) }
    fun onRegisterPasswordChange(value: String) { registerPassword = value }
    fun onRegisterLocationChange(value: String) { registerLocation = value }
    fun onRegisterDailyRateChange(value: String) { registerDailyRate = value.filter { it.isDigit() }.take(5) }

    fun toggleRegisterSkill(skill: String) {
        registerSelectedSkills = if (skill in registerSelectedSkills) {
            registerSelectedSkills - skill
        } else {
            registerSelectedSkills + skill
        }
    }

    fun completeRegistration() {
        val randomSuffix = (2000..9999).random()
        generatedWorkerId = "NK-WRK-$randomSuffix"
        registrationComplete = true
    }

    fun resetRegistration() {
        registerName = ""
        registerPhone = ""
        registerPassword = ""
        registerLocation = ""
        registerDailyRate = ""
        registerSelectedSkills = emptySet()
        registrationComplete = false
        generatedWorkerId = ""
    }

    // ── Worker Profile ──────────────────────────────────────────────
    var workerName by mutableStateOf("Raju Kumar")
        private set
    var workerId by mutableStateOf("NK-WRK-2041")
        private set
    var workerSkill by mutableStateOf("Painter")
        private set
    var workerDailyRate by mutableStateOf("800")
        private set
    var workerPhone by mutableStateOf("9876543210")
        private set

    // ── Availability ────────────────────────────────────────────────
    var isAvailable by mutableStateOf(true)
        private set

    fun toggleAvailability() {
        isAvailable = !isAvailable
    }

    // ── Work Requests (mock data) ───────────────────────────────────
    val incomingRequests: List<WorkRequest> = listOf(
        WorkRequest(
            id = 1,
            customerName = "Priya Sharma",
            workType = "Interior Painting",
            description = "Need full 2BHK interior painting with premium emulsion paint",
            location = "Koramangala, Bangalore",
            date = "May 12, 2026",
            budget = "₹15,000",
            status = RequestStatus.PENDING
        ),
        WorkRequest(
            id = 2,
            customerName = "Amit Patel",
            workType = "Wall Texture",
            description = "Decorative texture work for living room accent wall",
            location = "Indiranagar, Bangalore",
            date = "May 15, 2026",
            budget = "₹8,000",
            status = RequestStatus.PENDING
        ),
        WorkRequest(
            id = 3,
            customerName = "Deepa Nair",
            workType = "Exterior Painting",
            description = "Complete exterior painting for 2-story building with weather-proof paint",
            location = "HSR Layout, Bangalore",
            date = "May 20, 2026",
            budget = "₹25,000",
            status = RequestStatus.ACCEPTED
        )
    )

    var requestStatuses by mutableStateOf<Map<Int, RequestStatus>>(
        incomingRequests.associate { it.id to it.status }
    )
        private set

    fun acceptRequest(requestId: Int) {
        requestStatuses = requestStatuses + (requestId to RequestStatus.ACCEPTED)
    }

    fun rejectRequest(requestId: Int) {
        requestStatuses = requestStatuses + (requestId to RequestStatus.REJECTED)
    }

    fun getRequestStatus(requestId: Int): RequestStatus {
        return requestStatuses[requestId] ?: RequestStatus.PENDING
    }

    /** Today's pending requests count */
    val pendingRequestsCount: Int
        get() = requestStatuses.count { it.value == RequestStatus.PENDING }

    /** Accepted jobs count */
    val acceptedJobsCount: Int
        get() = requestStatuses.count { it.value == RequestStatus.ACCEPTED }
}
