package com.nammakelsa.repository

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

    override suspend fun login(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Login failed: UID is null")
            
            // Fetch role from users collection
            when (val userResult = userRepository.getUser(uid)) {
                is Result.Success -> Result.Success(userResult.data)
                is Result.Error -> throw userResult.exception
                else -> throw Exception("Unknown error fetching user profile")
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun register(email: String, password: String, role: UserRole): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw Exception("Registration failed: UID is null")

            val newUser = User(uid = uid, email = email, role = role)
            
            // Save role to users collection
            when (val saveResult = userRepository.saveUser(newUser)) {
                is Result.Success -> Result.Success(newUser)
                is Result.Error -> throw saveResult.exception
                else -> throw Exception("Unknown error saving user profile")
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getCurrentUser(): User? {
        val fbUser = auth.currentUser ?: return null
        // Note: For full User info including role, we should ideally fetch from Firestore, 
        // but this provides the basic info available synchronously
        return User(uid = fbUser.uid, email = fbUser.email ?: "")
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            auth.signOut()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
