package com.nammakelsa.repository

import com.nammakelsa.data.User
import com.nammakelsa.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(uid: String): Result<User>
}

class UserRepositoryImpl : UserRepository {
    private val usersCollection = FirebaseManager.usersCollection

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUser(uid: String): Result<User> {
        return try {
            val document = usersCollection.document(uid).get().await()
            val user = document.toObject(User::class.java)
            if (user != null) {
                Result.Success(user)
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
