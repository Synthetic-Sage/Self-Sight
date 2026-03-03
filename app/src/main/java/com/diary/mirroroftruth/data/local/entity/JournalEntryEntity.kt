package com.diary.mirroroftruth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long, // Start of day timestamp
    val mood: String,
    val content: String,
    val promptResponses: String // JSON serialized
)
