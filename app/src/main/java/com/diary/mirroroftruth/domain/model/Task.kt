package com.diary.mirroroftruth.domain.model

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val isCompleted: Boolean,
    val createdAt: Long,
    val dueDate: Long?
)
