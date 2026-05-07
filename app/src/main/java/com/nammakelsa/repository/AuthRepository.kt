package com.nammakelsa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.nammakelsa.data.User
import com.nammakelsa.data.UserRole
import com.nammakelsa.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String, role: UserRole): Result<User>
    fun getCurrentUser(): User?
    suspend fun logout(): Result<Unit>
}

class AuthRepositoryImpl(
    private val userRepository: UserRepository = UserRepositoryImpl()
) : AuthRepository {

    private val auth = FirebaseManager.auth
    private val TAG = "AuthRepository"

    override suspend fun login(email: String, password: String): Result<User> {
        Log.d(TAG, "Login attempt for email: $email")
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Login failed: UID is null")
            
            Log.d(TAG, "Login successful in Firebase Auth, UID: $uid. Fetching user profile...")
            
            // Fetch role from users collection
            when (val userResult = userRepository.getUser(uid)) {
                is Result.Success -> {
                    Log.d(TAG, "User profile fetched successfully. Role: ${userResult.data.role}")
                    Result.Success(userResult.data)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error fetching user profile", userResult.exception)
                    throw userResult.exception
                }
                else -> throw Exception("Unknown error fetching user profile")
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase Auth Error during login: ${e.errorCode}", e)
            Result.Error(mapFirebaseException(e))
        } catch (e: Exception) {
            Log.e(TAG, "General Error during login", e)
            Result.Error(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<User> {
        Log.d(TAG, "Registration attempt for email: $email, role: $role")
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Registration failed: UID is null")

            Log.d(TAG, "Registration successful in Firebase Auth, UID: $uid. Saving user profile...")

            val newUser = User(uid = uid, email = email, role = role)
            
            // Save role to users collection
            when (val saveResult = userRepository.saveUser(newUser)) {
                is Result.Success -> {
                    Log.d(TAG, "User profile saved successfully")
                    Result.Success(newUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error saving user profile", saveResult.exception)
                    throw saveResult.exception
                }
                else -> throw Exception("Unknown error saving user profile")
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase Auth Error during registration: ${e.errorCode}", e)
            Result.Error(mapFirebaseException(e))
        } catch (e: Exception) {
            Log.e(TAG, "General Error during registration", e)
            Result.Error(e)
        }
    }

    override fun getCurrentUser(): User? {
        val fbUser = auth.currentUser ?: return null
        return User(uid = fbUser.uid, email = fbUser.email ?: "")
    }

    override suspend fun logout(): Result<Unit> {
        Log.d(TAG, "Logout attempt")
        return try {
            auth.signOut()
            Log.d(TAG, "Logout successful")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout", e)
            Result.Error(e)
        }
    }

    private fun mapFirebaseException(e: FirebaseAuthException): Exception {
        val message = when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email format"
            "ERROR_USER_NOT_FOUND" -> "User not found"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Try again later"
            "ERROR_OPERATION_NOT_ALLOWED" -> "Email/Password authentication is not enabled"
            "CONFIGURATION_NOT_FOUND" -> "Firebase configuration issue. Please check API settings"
            else -> e.localizedMessage ?: "Authentication failed"
        }
        return Exception(message)
    }
}
