package com.diary.mirroroftruth.domain.model

data class JournalEntry(
    val id: Long = 0,
    val date: Long,
    val mood: String,
    val content: String,
    val promptResponses: String
)
