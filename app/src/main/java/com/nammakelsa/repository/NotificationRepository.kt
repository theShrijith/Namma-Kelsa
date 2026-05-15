package com.nammakelsa.repository

import com.nammakelsa.models.Notification

interface NotificationRepository {
    suspend fun createNotification(notification: Notification): Result<Unit>
    suspend fun getNotificationsForUser(userId: String): Result<List<Notification>>
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit>
}

class NotificationRepositoryImpl : NotificationRepository {
    override suspend fun createNotification(notification: Notification): Result<Unit> {
        // Mock implementation
        return Result.Success(Unit)
    }

    override suspend fun getNotificationsForUser(userId: String): Result<List<Notification>> {
        // Mock implementation
        return Result.Success(emptyList())
    }

    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit> {
        // Mock implementation
        return Result.Success(Unit)
    }
}
