package com.nammakelsa.repository

import com.nammakelsa.models.Worker
import com.nammakelsa.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

interface WorkerRepository {
    suspend fun saveWorkerProfile(worker: Worker): Result<Unit>
    suspend fun getWorkerProfile(workerId: String): Result<Worker>
    suspend fun getAllWorkers(): Result<List<Worker>>
    suspend fun getAvailableWorkers(): Result<List<Worker>>
}

class WorkerRepositoryImpl : WorkerRepository {
    private val workersCollection = FirebaseManager.workersCollection

    override suspend fun saveWorkerProfile(worker: Worker): Result<Unit> {
        return try {
            workersCollection.document(worker.id).set(worker).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getWorkerProfile(workerId: String): Result<Worker> {
        return try {
            val document = workersCollection.document(workerId).get().await()
            val worker = document.toObject(Worker::class.java)
            if (worker != null) {
                Result.Success(worker)
            } else {
                Result.Error(Exception("Worker profile not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllWorkers(): Result<List<Worker>> {
        return try {
            val snapshot = workersCollection.get().await()
            val workers = snapshot.documents.mapNotNull { it.toObject(Worker::class.java) }
            Result.Success(workers)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAvailableWorkers(): Result<List<Worker>> {
        return try {
            val snapshot = workersCollection.whereEqualTo("isAvailable", true).get().await()
            val workers = snapshot.documents.mapNotNull { it.toObject(Worker::class.java) }
            Result.Success(workers)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
