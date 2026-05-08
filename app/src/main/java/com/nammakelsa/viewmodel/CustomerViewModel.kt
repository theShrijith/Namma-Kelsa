package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammakelsa.data.Customer
import com.nammakelsa.data.Worker
import com.nammakelsa.repository.CustomerRepository
import com.nammakelsa.repository.CustomerRepositoryImpl
import com.nammakelsa.repository.WorkerRepository
import com.nammakelsa.repository.WorkerRepositoryImpl
import com.nammakelsa.repository.onSuccess
import com.nammakelsa.repository.onFailure
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for Customer-specific UI state.
 * Manages search, filters, saved workers, customer profile, and login/register form fields.
 */
class CustomerViewModel(
    private val workerRepository: WorkerRepository = WorkerRepositoryImpl(),
    private val customerRepository: CustomerRepository = CustomerRepositoryImpl()
) : ViewModel() {

    // ── Login Form Fields ────────────────────────────────────────────────────
    var loginEmail by mutableStateOf("")
        private set
    var loginPassword by mutableStateOf("")
        private set

    fun onLoginEmailChange(value: String) { loginEmail = value }
    fun onLoginPasswordChange(value: String) { loginPassword = value }

    // ── Registration Form Fields ─────────────────────────────────────────────
    var registerName by mutableStateOf("")
        private set
    var registerPhone by mutableStateOf("")
        private set
    var registerEmail by mutableStateOf("")
        private set
    var registerPassword by mutableStateOf("")
        private set
    var registerConfirmPassword by mutableStateOf("")
        private set

    fun onRegisterNameChange(value: String) { registerName = value }
    fun onRegisterPhoneChange(value: String) { registerPhone = value.filter { it.isDigit() }.take(10) }
    fun onRegisterEmailChange(value: String) { registerEmail = value }
    fun onRegisterPasswordChange(value: String) { registerPassword = value }
    fun onRegisterConfirmPasswordChange(value: String) { registerConfirmPassword = value }

    // ── Customer Profile (loaded from Firestore) ─────────────────────────────
    private val _customerProfile = MutableStateFlow<Customer?>(null)
    val customerProfile: StateFlow<Customer?> = _customerProfile.asStateFlow()

    /** Display name — real Firestore data; falls back gracefully. */
    val customerName: String
        get() = _customerProfile.value?.name?.ifBlank { null } ?: ""

    /** Phone — real Firestore data. */
    val customerPhone: String
        get() = _customerProfile.value?.phone ?: ""

    /**
     * Called after login / session restore to load the customer's real profile from Firestore.
     */
    fun loadCustomerProfile(uid: String) {
        if (uid.isBlank()) return
        viewModelScope.launch {
            customerRepository.getCustomerProfile(uid).onSuccess { customer ->
                _customerProfile.value = customer
            }.onFailure { /* fail silently — UI will show blank name fallback */ }
        }
    }

    /**
     * Clear customer profile data on logout.
     */
    fun clearProfile() {
        _customerProfile.value = null
        loginEmail = ""
        loginPassword = ""
        registerName = ""
        registerPhone = ""
        registerEmail = ""
        registerPassword = ""
        registerConfirmPassword = ""
        searchQuery = ""
        activeFilter = null
        savedWorkerIds = emptySet()
        resetRequestForm()
    }

    // ── Search State ─────────────────────────────────────────────────────────
    var searchQuery by mutableStateOf("")
        private set
    var activeFilter by mutableStateOf<String?>(null)
        private set

    fun onSearchQueryChange(value: String) { searchQuery = value }

    fun onFilterSelected(skill: String?) {
        activeFilter = if (activeFilter == skill) null else skill
    }

    // ── Saved Workers ────────────────────────────────────────────────────────
    var savedWorkerIds by mutableStateOf<Set<String>>(emptySet())
        private set

    fun toggleSaveWorker(workerId: String) {
        savedWorkerIds = if (workerId in savedWorkerIds) {
            savedWorkerIds - workerId
        } else {
            savedWorkerIds + workerId
        }
    }

    fun isWorkerSaved(workerId: String): Boolean = workerId in savedWorkerIds

    // ── Work Request Form ────────────────────────────────────────────────────
    var requestDescription by mutableStateOf("")
        private set
    var requestLocation by mutableStateOf("")
        private set
    var requestDate by mutableStateOf("")
        private set
    var requestBudget by mutableStateOf("")
        private set
    var requestNotes by mutableStateOf("")
        private set
    var requestSubmitted by mutableStateOf(false)
        private set

    fun onRequestDescriptionChange(value: String) { requestDescription = value }
    fun onRequestLocationChange(value: String) { requestLocation = value }
    fun onRequestDateChange(value: String) { requestDate = value }
    fun onRequestBudgetChange(value: String) { requestBudget = value.filter { it.isDigit() }.take(6) }
    fun onRequestNotesChange(value: String) { requestNotes = value }

    fun submitRequest() { requestSubmitted = true }

    fun resetRequestForm() {
        requestDescription = ""
        requestLocation = ""
        requestDate = ""
        requestBudget = ""
        requestNotes = ""
        requestSubmitted = false
    }

    // ── Worker Data (Firestore) ──────────────────────────────────────────────
    private val _workers = MutableStateFlow<List<Worker>>(emptyList())
    val workers: StateFlow<List<Worker>> = _workers.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchAvailableWorkers()
    }

    private fun fetchAvailableWorkers() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val result = workerRepository.getAvailableWorkers()
            result.onSuccess { list ->
                _workers.value = list
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
            _isLoading.value = false
        }
    }

    /** Filtered worker list based on search query and active skill filter. */
    val filteredWorkers: List<Worker>
        get() {
            var result = _workers.value
            if (activeFilter != null) {
                result = result.filter { it.skill == activeFilter }
            }
            if (searchQuery.isNotBlank()) {
                val q = searchQuery.lowercase()
                result = result.filter {
                    it.name.lowercase().contains(q) ||
                    it.skill.lowercase().contains(q)
                }
            }
            return result
        }

    /** Nearby workers (available, sorted by distance). */
    val nearbyWorkers: List<Worker>
        get() = _workers.value
            .filter { it.isAvailable }
            .sortedBy { it.distance.replace(" km", "").toFloatOrNull() ?: 99f }
            .take(4)

    /** Popular workers (high-rated). */
    val popularWorkers: List<Worker>
        get() = _workers.value
            .sortedByDescending { it.rating }
            .take(4)

    /** Saved workers list. */
    val savedWorkers: List<Worker>
        get() = _workers.value.filter { it.id in savedWorkerIds }

    /** Get a worker by id. */
    fun getWorkerById(id: String): Worker? = _workers.value.find { it.id == id }
}
