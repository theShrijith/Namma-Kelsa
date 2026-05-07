package com.nammakelsa.repository

import com.nammakelsa.data.Customer
import com.nammakelsa.firebase.FirebaseManager
import kotlinx.coroutines.tasks.await

interface CustomerRepository {
    suspend fun saveCustomerProfile(customer: Customer): Result<Unit>
    suspend fun getCustomerProfile(customerId: String): Result<Customer>
}

class CustomerRepositoryImpl : CustomerRepository {
    private val customersCollection = FirebaseManager.customersCollection

    override suspend fun saveCustomerProfile(customer: Customer): Result<Unit> {
        return try {
            customersCollection.document(customer.id).set(customer).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getCustomerProfile(customerId: String): Result<Customer> {
        return try {
            val document = customersCollection.document(customerId).get().await()
            val customer = document.toObject(Customer::class.java)
            if (customer != null) {
                Result.Success(customer)
            } else {
                Result.Error(Exception("Customer profile not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
