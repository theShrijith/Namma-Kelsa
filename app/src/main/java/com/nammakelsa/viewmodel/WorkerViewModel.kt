package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.nammakelsa.data.RequestStatus
import com.nammakelsa.data.WorkRequest
import com.nammakelsa.data.Worker
import com.nammakelsa.repository.StorageRepository
import com.nammakelsa.repository.StorageRepositoryImpl
import com.nammakelsa.repository.WorkerRepository
import com.nammakelsa.repository.WorkerRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Worker-specific UI state.
 * Manages availability, requests, profile, and registration.
 */
class WorkerViewModel(
    private val workerRepository: WorkerRepository = WorkerRepositoryImpl(),
    private val storageRepository: StorageRepository = StorageRepositoryImpl()
) : ViewModel() {

    // ── Auth State ──────────────────────────────────────────────────
    var loginEmail by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    fun onLoginEmailChange(value: String) { loginEmail = value }
    fun onLoginPasswordChange(value: String) { loginPassword = value }

    // ── Registration State ──────────────────────────────────────────
    var registerName by mutableStateOf("")
        private set
    var registerPhone by mutableStateOf("")
        private set
    var registerEmail by mutableStateOf("")
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
    fun onRegisterEmailChange(value: String) { registerEmail = value }
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

    // ── Worker Profile (Firestore via StateFlow) ────────────────────
    private val _workerProfile = MutableStateFlow<Worker?>(null)
    val workerProfile: StateFlow<Worker?> = _workerProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun loadProfile(uid: String) {
        if (uid.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            workerRepository.getWorkerProfile(uid).onSuccess { worker ->
                _workerProfile.value = worker
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    fun updateProfile(updatedWorker: Worker) {
        viewModelScope.launch {
            _isLoading.value = true
            workerRepository.saveWorkerProfile(updatedWorker).onSuccess {
                _workerProfile.value = updatedWorker
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    fun toggleAvailability() {
        val current = _workerProfile.value ?: return
        updateProfile(current.copy(isAvailable = !current.isAvailable))
    }

    fun uploadProfileImage(uid: String, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            storageRepository.uploadProfileImage(uid, uri).onSuccess { url ->
                val current = _workerProfile.value ?: return@onSuccess
                updateProfile(current.copy(profileImageUrl = url))
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    fun uploadGalleryImage(uid: String, uri: Uri) {
        viewModelScope.launch {
            _isLoading.value = true
            storageRepository.uploadGalleryImage(uid, uri).onSuccess { url ->
                val current = _workerProfile.value ?: return@onSuccess
                val newGallery = current.galleryImages + url
                updateProfile(current.copy(galleryImages = newGallery))
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    // ── Work Requests (mock data) ───────────────────────────────────
    val incomingRequests: List<WorkRequest> = listOf(
        WorkRequest(
            id = "1",
            customerName = "Priya Sharma",
            workType = "Interior Painting",
            description = "Need full 2BHK interior painting with premium emulsion paint",
            location = "Koramangala, Bangalore",
            date = "May 12, 2026",
            budget = "₹15,000",
            status = RequestStatus.PENDING
        ),
        WorkRequest(
            id = "2",
            customerName = "Amit Patel",
            workType = "Wall Texture",
            description = "Decorative texture work for living room accent wall",
            location = "Indiranagar, Bangalore",
            date = "May 15, 2026",
            budget = "₹8,000",
            status = RequestStatus.PENDING
        ),
        WorkRequest(
            id = "3",
            customerName = "Deepa Nair",
            workType = "Exterior Painting",
            description = "Complete exterior painting for 2-story building with weather-proof paint",
            location = "HSR Layout, Bangalore",
            date = "May 20, 2026",
            budget = "₹25,000",
            status = RequestStatus.ACCEPTED
        )
    )

    var requestStatuses by mutableStateOf<Map<String, RequestStatus>>(
        incomingRequests.associate { it.id to it.status }
    )
        private set

    fun acceptRequest(requestId: String) {
        requestStatuses = requestStatuses + (requestId to RequestStatus.ACCEPTED)
    }

    fun rejectRequest(requestId: String) {
        requestStatuses = requestStatuses + (requestId to RequestStatus.REJECTED)
    }

    fun getRequestStatus(requestId: String): RequestStatus {
        return requestStatuses[requestId] ?: RequestStatus.PENDING
    }

    /** Today's pending requests count */
    val pendingRequestsCount: Int
        get() = requestStatuses.count { it.value == RequestStatus.PENDING }

    /** Accepted jobs count */
    val acceptedJobsCount: Int
        get() = requestStatuses.count { it.value == RequestStatus.ACCEPTED }
}
