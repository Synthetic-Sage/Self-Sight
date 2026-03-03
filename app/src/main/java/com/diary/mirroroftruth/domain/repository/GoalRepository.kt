package com.diary.mirroroftruth.domain.repository

import com.diary.mirroroftruth.domain.model.Goal
import kotlinx.coroutines.flow.Flow

interface GoalRepository {
    suspend fun insertGoal(goal: Goal)
    suspend fun updateGoal(goal: Goal)
    suspend fun deleteGoal(goal: Goal)
    fun getAllGoals(): Flow<List<Goal>>
}
