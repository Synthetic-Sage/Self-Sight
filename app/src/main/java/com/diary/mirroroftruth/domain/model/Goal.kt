package com.diary.mirroroftruth.domain.model

enum class StepType {
    BIG, SMALL
}

data class Goal(
    val id: Long = 0,
    val title: String,
    val description: String?,
    val progress: Float,
    val target: Float,
    val type: StepType = StepType.BIG,
    val createdAt: Long,
    val deadline: Long?
)
