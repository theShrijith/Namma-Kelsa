package com.nammakelsa.repository

import android.net.Uri
import com.nammakelsa.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await
import java.util.UUID

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String>
    suspend fun uploadGalleryImage(userId: String, imageUri: Uri): Result<String>
}

class StorageRepositoryImpl : StorageRepository {
    private val storageRef = FirebaseManager.storage.reference

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val fileRef = storageRef.child("profile_images/$userId.jpg")
            fileRef.putFile(imageUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun uploadGalleryImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val uniqueFilename = UUID.randomUUID().toString() + ".jpg"
            val fileRef = storageRef.child("gallery_images/$userId/$uniqueFilename")
            fileRef.putFile(imageUri).await()
            val downloadUrl = fileRef.downloadUrl.await().toString()
            Result.Success(downloadUrl)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
