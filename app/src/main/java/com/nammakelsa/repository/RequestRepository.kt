package com.nammakelsa.repository

import com.nammakelsa.models.WorkRequest
import com.nammakelsa.models.RequestStatus

interface RequestRepository {
    suspend fun createRequest(request: WorkRequest): Result<Unit>
    suspend fun getRequestsForWorker(workerId: String): Result<List<WorkRequest>>
    suspend fun getRequestsForCustomer(customerId: String): Result<List<WorkRequest>>
    suspend fun updateRequestStatus(requestId: String, status: RequestStatus): Result<Unit>
}

class RequestRepositoryImpl : RequestRepository {
    override suspend fun createRequest(request: WorkRequest): Result<Unit> {
        // Mock implementation
        return Result.Success(Unit)
    }

    override suspend fun getRequestsForWorker(workerId: String): Result<List<WorkRequest>> {
        // Mock implementation
        return Result.Success(emptyList())
    }

    override suspend fun getRequestsForCustomer(customerId: String): Result<List<WorkRequest>> {
        // Mock implementation
        return Result.Success(emptyList())
    }

    override suspend fun updateRequestStatus(
        requestId: String,
        status: RequestStatus
    ): Result<Unit> {
        // Mock implementation
        return Result.Success(Unit)
    }
}
