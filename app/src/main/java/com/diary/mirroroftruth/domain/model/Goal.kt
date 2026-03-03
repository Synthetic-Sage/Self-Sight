package com.diary.mirroroftruth.domain.model

data class Goal(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val progress: Float,
    val target: Float,
    val createdAt: Long,
    val deadline: Long?
)
