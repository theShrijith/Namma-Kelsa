package com.nammakelsa.models

/**
 * Represents a work request sent by a customer to a worker.
 */
data class WorkRequest(
    val id: String = "",
    val customerId: String = "",
    val workerId: String = "",
    val customerName: String = "",
    val workType: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val budget: String = "",
    val notes: String = "",
    val status: RequestStatus = RequestStatus.PENDING
)
