package com.diary.mirroroftruth.domain.repository

import com.diary.mirroroftruth.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalEntryRepository {
    suspend fun insertJournalEntry(entry: JournalEntry)
    suspend fun updateJournalEntry(entry: JournalEntry)
    suspend fun deleteJournalEntry(entry: JournalEntry)
    fun getJournalEntriesBetween(start: Long, end: Long): Flow<List<JournalEntry>>
    fun getJournalEntryForDate(date: Long): Flow<JournalEntry?>
}
