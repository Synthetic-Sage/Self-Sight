package com.diary.mirroroftruth.data.repository

import com.diary.mirroroftruth.data.local.dao.GoalDao
import com.diary.mirroroftruth.data.mapper.toDomain
import com.diary.mirroroftruth.data.mapper.toEntity
import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.repository.GoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GoalRepositoryImpl @Inject constructor(
    private val goalDao: GoalDao
) : GoalRepository {
    override suspend fun insertGoal(goal: Goal) {
        goalDao.insertGoal(goal.toEntity())
    }

    override suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal.toEntity())
    }

    override suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal.toEntity())
    }

    override fun getAllGoals(): Flow<List<Goal>> {
        return goalDao.getAllGoals().map { list ->
            list.map { it.toDomain() }
        }
    }
}
