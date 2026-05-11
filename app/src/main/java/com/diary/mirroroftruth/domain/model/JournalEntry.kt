package com.diary.mirroroftruth.domain.model

data class JournalEntry(
    val id: Long = 0,
    val date: Long,
    val emotionTags: List<String>,
    val content: String,
    val wentWell: String,
    val toImprove: String,
    val learning: String,
    val imagePath: String? = null
)
