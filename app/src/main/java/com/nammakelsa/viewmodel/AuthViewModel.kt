package com.nammakelsa.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammakelsa.models.Customer
import com.nammakelsa.models.User
import com.nammakelsa.models.UserRole
import com.nammakelsa.models.Worker
import com.nammakelsa.repository.AuthRepository
import com.nammakelsa.repository.AuthRepositoryImpl
import com.nammakelsa.repository.CustomerRepository
import com.nammakelsa.repository.CustomerRepositoryImpl
import com.nammakelsa.repository.Result
import com.nammakelsa.repository.UserRepository
import com.nammakelsa.repository.UserRepositoryImpl
import com.nammakelsa.repository.WorkerRepository
import com.nammakelsa.repository.WorkerRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.nammakelsa.state.AuthState



class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val customerRepository: CustomerRepository = CustomerRepositoryImpl(),
    private val workerRepository: WorkerRepository = WorkerRepositoryImpl()
) : ViewModel() {

    private val TAG = "AuthViewModel"

    // ── Auth State (drives navigation) ──────────────────────────────────────
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // ── Loading / Error ──────────────────────────────────────────────────────
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // ── Last registered Worker ID (surfaced for success dialog) ─────────────
    private val _lastRegisteredWorkerId = MutableStateFlow<String?>(null)
    val lastRegisteredWorkerId: StateFlow<String?> = _lastRegisteredWorkerId.asStateFlow()

    // ── Convenience accessor ─────────────────────────────────────────────────
    val currentUser: User?
        get() = when (val s = _authState.value) {
            is AuthState.AuthenticatedCustomer -> s.user
            is AuthState.AuthenticatedWorker   -> s.user
            else                               -> null
        }

    init {
        checkSession()
    }

    // ────────────────────────────────────────────────────────────────────────
    // Session check
    // ────────────────────────────────────────────────────────────────────────

    fun checkSession() {
        viewModelScope.launch {
            Log.d(TAG, "checkSession: checking for existing Firebase Auth session")
            _authState.value = AuthState.Initializing
            val fbUser = authRepository.getCurrentUser()
            if (fbUser != null) {
                Log.i(TAG, "checkSession: session found for UID=${fbUser.uid}; fetching profile…")
                when (val result = userRepository.getUser(fbUser.uid)) {
                    is Result.Success -> {
                        val user = result.data
                        Log.i(TAG, "checkSession: profile restored, role=${user.role}")
                        _authState.value = if (user.role == UserRole.WORKER)
                            AuthState.AuthenticatedWorker(user)
                        else
                            AuthState.AuthenticatedCustomer(user)
                    }
                    is Result.Error -> {
                        Log.e(TAG, "checkSession: profile fetch failed", result.exception)
                        _authState.value = AuthState.Unauthenticated
                    }
                    else -> _authState.value = AuthState.Unauthenticated
                }
            } else {
                Log.d(TAG, "checkSession: no active session")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Login
    // ────────────────────────────────────────────────────────────────────────

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        // Guard: prevent duplicate requests while one is in-flight
        if (_isLoading.value) return

        viewModelScope.launch {
            Log.i(TAG, "login: attempting for email=$email")
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = authRepository.login(email.trim(), password)) {
                is Result.Success -> {
                    val user = result.data
                    Log.i(TAG, "login: success, role=${user.role}")
                    _authState.value = if (user.role == UserRole.WORKER)
                        AuthState.AuthenticatedWorker(user)
                    else
                        AuthState.AuthenticatedCustomer(user)
                    onResult(user)
                }
                is Result.Error -> {
                    Log.e(TAG, "login: failed — ${result.exception.message}")
                    _errorMessage.value = result.exception.message ?: "Login failed"
                    onResult(null)
                }
                is Result.Loading -> {
                    Log.d(TAG, "login: in progress…")
                }
            }
            _isLoading.value = false
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Customer Registration
    // ────────────────────────────────────────────────────────────────────────

    fun registerCustomer(
        email: String,
        password: String,
        name: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            Log.i(TAG, "registerCustomer: attempting for email=$email")
            _isLoading.value = true
            _errorMessage.value = null

            when (val authResult = authRepository.register(email.trim(), password, UserRole.CUSTOMER)) {
                is Result.Success -> {
                    val uid = authResult.data.uid
                    Log.d(TAG, "registerCustomer: auth success UID=$uid; saving profiles…")

                    // Save customer profile in Firestore
                    val customer = Customer(
                        id    = uid,
                        name  = name.trim(),
                        email = email.trim(),
                        phone = phone.trim()
                    )
                    val profileResult = customerRepository.saveCustomerProfile(customer)
                    if (profileResult is Result.Error) {
                        Log.e(TAG, "registerCustomer: customer profile save failed", profileResult.exception)
                        _errorMessage.value = profileResult.exception.message ?: "Failed to save customer profile"
                        _isLoading.value = false
                        onResult(false)
                        return@launch
                    }

                    Log.i(TAG, "registerCustomer: profile saved successfully for ${customer.email}")
                    _authState.value = AuthState.AuthenticatedCustomer(authResult.data)
                    onResult(true)
                }
                is Result.Error -> {
                    Log.e(TAG, "registerCustomer: registration failed", authResult.exception)
                    _errorMessage.value = authResult.exception.message ?: "Registration failed"
                    onResult(false)
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Worker Registration
    // ────────────────────────────────────────────────────────────────────────

    fun registerWorker(
        email: String,
        password: String,
        name: String,
        phone: String,
        skill: String,
        location: String,
        dailyRate: Int,
        onResult: (success: Boolean, workerId: String?) -> Unit
    ) {
        if (_isLoading.value) return

        viewModelScope.launch {
            Log.i(TAG, "registerWorker: attempting for email=$email")
            _isLoading.value = true
            _errorMessage.value = null

            when (val authResult = authRepository.register(email.trim(), password, UserRole.WORKER)) {
                is Result.Success -> {
                    val uid = authResult.data.uid
                    // Generate worker ID — single source of truth
                    val randomSuffix = (2000..9999).random()
                    val workerUniqueId = "NK-WRK-$randomSuffix"

                    Log.d(TAG, "registerWorker: auth success UID=$uid; workerID=$workerUniqueId; saving profile…")

                    val worker = Worker(
                        id         = uid,
                        name       = name.trim(),
                        phone      = phone.trim(),
                        skill      = skill,
                        dailyRate  = dailyRate,
                        distance   = location,
                        workerId   = workerUniqueId,
                        isAvailable = true
                    )

                    val profileResult = workerRepository.saveWorkerProfile(worker)
                    if (profileResult is Result.Error) {
                        Log.e(TAG, "registerWorker: profile save failed", profileResult.exception)
                        _errorMessage.value = profileResult.exception.message ?: "Failed to save worker profile"
                        _isLoading.value = false
                        onResult(false, null)
                        return@launch
                    }

                    Log.i(TAG, "registerWorker: profile saved, workerID=$workerUniqueId")
                    _lastRegisteredWorkerId.value = workerUniqueId
                    _authState.value = AuthState.AuthenticatedWorker(authResult.data)
                    onResult(true, workerUniqueId)
                }
                is Result.Error -> {
                    Log.e(TAG, "registerWorker: registration failed", authResult.exception)
                    _errorMessage.value = authResult.exception.message ?: "Registration failed"
                    onResult(false, null)
                }
                else -> {}
            }
            _isLoading.value = false
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Logout — full state reset
    // ────────────────────────────────────────────────────────────────────────

    fun logout(onComplete: () -> Unit) {
        viewModelScope.launch {
            Log.i(TAG, "logout: signing out user: ${currentUser?.email}")
            authRepository.logout()
            // Reset all state
            _authState.value = AuthState.Unauthenticated
            _errorMessage.value = null
            _lastRegisteredWorkerId.value = null
            onComplete()
        }
    }

    // ────────────────────────────────────────────────────────────────────────
    // Utilities
    // ────────────────────────────────────────────────────────────────────────

    fun clearError() {
        _errorMessage.value = null
    }
}
