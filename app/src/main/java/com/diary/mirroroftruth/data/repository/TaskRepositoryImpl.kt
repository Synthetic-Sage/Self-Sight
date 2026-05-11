package com.diary.mirroroftruth.data.repository

import com.diary.mirroroftruth.data.local.dao.TaskDao
import com.diary.mirroroftruth.data.mapper.toDomain
import com.diary.mirroroftruth.data.mapper.toEntity
import com.diary.mirroroftruth.domain.model.Task
import com.diary.mirroroftruth.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {
    override suspend fun insertTask(task: Task) {
        taskDao.insertTask(task.toEntity())
    }

    override suspend fun updateTask(task: Task) {
        taskDao.updateTask(task.toEntity())
    }

    override suspend fun updateTasks(tasks: List<Task>) {
        taskDao.updateTasks(tasks.map { it.toEntity() })
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.deleteTask(task.toEntity())
    }

    override fun getTasksForDate(date: Long): Flow<List<Task>> {
        return taskDao.getTasksForDate(date).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getAllTasks(): Flow<List<Task>> {
        return taskDao.getAllTasks().map { list ->
            list.map { it.toDomain() }
        }
    }
}
