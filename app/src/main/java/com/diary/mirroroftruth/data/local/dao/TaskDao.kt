package com.diary.mirroroftruth.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diary.mirroroftruth.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity)

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Update
    suspend fun updateTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE dueDate = :date ORDER BY positionIndex ASC, createdAt ASC")
    fun getTasksForDate(date: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>
}
