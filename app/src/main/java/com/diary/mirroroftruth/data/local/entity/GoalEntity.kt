package com.diary.mirroroftruth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String?,
    val progress: Float,
    val target: Float,
    val createdAt: Long,
    val deadline: Long?
)
