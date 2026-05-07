package com.nammakelsa.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammakelsa.data.Customer
import com.nammakelsa.data.User
import com.nammakelsa.data.UserRole
import com.nammakelsa.data.Worker
import com.nammakelsa.repository.AuthRepository
import com.nammakelsa.repository.AuthRepositoryImpl
import com.nammakelsa.repository.CustomerRepository
import com.nammakelsa.repository.CustomerRepositoryImpl
import com.nammakelsa.repository.Result
import com.nammakelsa.repository.UserRepository
import com.nammakelsa.repository.UserRepositoryImpl
import com.nammakelsa.repository.WorkerRepository
import com.nammakelsa.repository.WorkerRepositoryImpl
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl(),
    private val userRepository: UserRepository = UserRepositoryImpl(),
    private val customerRepository: CustomerRepository = CustomerRepositoryImpl(),
    private val workerRepository: WorkerRepository = WorkerRepositoryImpl()
) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        checkSession(null)
    }

    fun checkSession(onSessionChecked: ((User?) -> Unit)?) {
        viewModelScope.launch {
            val fbUser = authRepository.getCurrentUser()
            if (fbUser != null) {
                // Fetch full user with role
                when (val result = userRepository.getUser(fbUser.uid)) {
                    is Result.Success -> {
                        currentUser = result.data
                        onSessionChecked?.invoke(result.data)
                    }
                    else -> {
                        currentUser = null
                        onSessionChecked?.invoke(null)
                    }
                }
            } else {
                currentUser = null
                onSessionChecked?.invoke(null)
            }
        }
    }

    fun login(email: String, password: String, onResult: (User?) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            
            when (val result = authRepository.login(email, password)) {
                is Result.Success -> {
                    currentUser = result.data
                    onResult(result.data)
                }
                is Result.Error -> {
                    errorMessage = result.exception.message ?: "Login failed"
                    onResult(null)
                }
                is Result.Loading -> {}
            }
            isLoading = false
        }
    }

    fun registerCustomer(email: String, password: String, name: String, phone: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            when (val authResult = authRepository.register(email, password, UserRole.CUSTOMER)) {
                is Result.Success -> {
                    val customer = Customer(
                        id = authResult.data.uid,
                        name = name,
                        email = email,
                        phone = phone
                    )
                    when (val profileResult = customerRepository.saveCustomerProfile(customer)) {
                        is Result.Success -> {
                            currentUser = authResult.data
                            onResult(true)
                        }
                        is Result.Error -> {
                            errorMessage = profileResult.exception.message ?: "Failed to save profile"
                            onResult(false)
                        }
                        else -> {}
                    }
                }
                is Result.Error -> {
                    errorMessage = authResult.exception.message ?: "Registration failed"
                    onResult(false)
                }
                else -> {}
            }
            isLoading = false
        }
    }

    fun registerWorker(
        email: String, password: String, name: String, phone: String, 
        skill: String, location: String, dailyRate: Int, onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            when (val authResult = authRepository.register(email, password, UserRole.WORKER)) {
                is Result.Success -> {
                    val uid = authResult.data.uid
                    val randomSuffix = (2000..9999).random()
                    val workerUniqueId = "NK-WRK-$randomSuffix"

                    val worker = Worker(
                        id = uid,
                        name = name,
                        phone = phone,
                        skill = skill,
                        dailyRate = dailyRate,
                        distance = location, // Storing location in distance field for now
                        workerId = workerUniqueId,
                        isAvailable = true
                    )
                    
                    when (val profileResult = workerRepository.saveWorkerProfile(worker)) {
                        is Result.Success -> {
                            currentUser = authResult.data
                            onResult(true)
                        }
                        is Result.Error -> {
                            errorMessage = profileResult.exception.message ?: "Failed to save profile"
                            onResult(false)
                        }
                        else -> {}
                    }
                }
                is Result.Error -> {
                    errorMessage = authResult.exception.message ?: "Registration failed"
                    onResult(false)
                }
                else -> {}
            }
            isLoading = false
        }
    }

    fun logout(onResult: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            currentUser = null
            onResult()
        }
    }
}
