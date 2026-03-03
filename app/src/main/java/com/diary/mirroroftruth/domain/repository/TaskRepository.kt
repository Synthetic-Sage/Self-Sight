package com.diary.mirroroftruth.domain.repository

import com.diary.mirroroftruth.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun insertTask(task: Task)
    suspend fun updateTask(task: Task)
    suspend fun deleteTask(task: Task)
    fun getTasksForDate(date: Long): Flow<List<Task>>
    fun getAllTasks(): Flow<List<Task>>
}
