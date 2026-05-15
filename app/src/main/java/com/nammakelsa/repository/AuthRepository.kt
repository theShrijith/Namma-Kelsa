package com.nammakelsa.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.nammakelsa.models.User
import com.nammakelsa.models.UserRole
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
        Log.i(TAG, "Login attempt started for email: $email")
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            val uid = firebaseUser?.uid ?: throw Exception("Login failed: Firebase User or UID is null")
            
            Log.d(TAG, "Firebase Auth login successful, UID: $uid. Fetching user profile from Firestore...")
            
            // Fetch role from users collection
            when (val userResult = userRepository.getUser(uid)) {
                is Result.Success -> {
                    Log.i(TAG, "User profile fetched successfully. Role: ${userResult.data.role}")
                    Result.Success(userResult.data)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error fetching user profile from Firestore for UID: $uid", userResult.exception)
                    Result.Error(userResult.exception)
                }
                else -> {
                    Log.e(TAG, "Unknown error result while fetching user profile for UID: $uid")
                    Result.Error(Exception("Unknown error fetching user profile"))
                }
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase Auth Exception during login: [${e.errorCode}] ${e.message}", e)
            Result.Error(mapFirebaseException(e))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Exception during login for email: $email", e)
            Result.Error(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<User> {
        Log.i(TAG, "Registration attempt started for email: $email, role: $role")
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            val uid = firebaseUser?.uid ?: throw Exception("Registration failed: Firebase User or UID is null")

            Log.d(TAG, "Firebase Auth registration successful, UID: $uid. Saving user profile to Firestore...")

            val newUser = User(uid = uid, email = email, role = role)
            
            // Save role to users collection
            when (val saveResult = userRepository.saveUser(newUser)) {
                is Result.Success -> {
                    Log.i(TAG, "User profile saved successfully to Firestore for UID: $uid")
                    Result.Success(newUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error saving user profile to Firestore for UID: $uid", saveResult.exception)
                    Result.Error(saveResult.exception)
                }
                else -> {
                    Log.e(TAG, "Unknown error result while saving user profile for UID: $uid")
                    Result.Error(Exception("Unknown error saving user profile"))
                }
            }
        } catch (e: FirebaseAuthException) {
            Log.e(TAG, "Firebase Auth Exception during registration: [${e.errorCode}] ${e.message}", e)
            Result.Error(mapFirebaseException(e))
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected Exception during registration for email: $email", e)
            Result.Error(e)
        }
    }

    override fun getCurrentUser(): User? {
        val fbUser = auth.currentUser
        return if (fbUser != null) {
            Log.d(TAG, "Current session found for UID: ${fbUser.uid}")
            User(uid = fbUser.uid, email = fbUser.email ?: "")
        } else {
            Log.d(TAG, "No current active session found")
            null
        }
    }

    override suspend fun logout(): Result<Unit> {
        Log.i(TAG, "Logout initiated")
        return try {
            auth.signOut()
            Log.d(TAG, "Firebase Auth sign out successful")
            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error during Firebase Auth sign out", e)
            Result.Error(e)
        }
    }

    /**
     * Maps Firebase Auth error codes to user-friendly messages.
     * Also handles cases where the error code might not have the ERROR_ prefix.
     */
    private fun mapFirebaseException(e: FirebaseAuthException): Exception {
        val errorCode = e.errorCode.uppercase().removePrefix("ERROR_")
        Log.d(TAG, "Mapping normalized error code: $errorCode")

        val message = when (errorCode) {
            "INVALID_EMAIL" -> "Invalid email format"
            "USER_NOT_FOUND" -> "User not found. Please register first."
            "WRONG_PASSWORD" -> "Incorrect password"
            "EMAIL_ALREADY_IN_USE" -> "This email is already registered"
            "WEAK_PASSWORD" -> "Password should be at least 6 characters"
            "USER_DISABLED" -> "This account has been disabled"
            "TOO_MANY_REQUESTS" -> "Too many failed attempts. Try again later"
            "OPERATION_NOT_ALLOWED" -> "Email/Password sign-in is not enabled in Firebase Console."
            "CONFIGURATION_NOT_FOUND" -> "Firebase Authentication is not configured correctly. Ensure Email/Password provider is enabled in the Firebase Console."
            "NETWORK_REQUEST_FAILED" -> "Network error. Please check your internet connection."
            "INTERNAL_ERROR" -> "An internal error occurred. Please try again later."
            else -> e.localizedMessage ?: "Authentication failed ($errorCode)"
        }
        return Exception(message)
    }
}
