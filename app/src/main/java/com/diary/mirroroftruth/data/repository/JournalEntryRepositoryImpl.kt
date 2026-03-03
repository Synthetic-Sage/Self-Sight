package com.diary.mirroroftruth.data.repository

import com.diary.mirroroftruth.data.local.dao.JournalEntryDao
import com.diary.mirroroftruth.data.mapper.toDomain
import com.diary.mirroroftruth.data.mapper.toEntity
import com.diary.mirroroftruth.domain.model.JournalEntry
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JournalEntryRepositoryImpl @Inject constructor(
    private val journalEntryDao: JournalEntryDao
) : JournalEntryRepository {
    override suspend fun insertJournalEntry(entry: JournalEntry) {
        journalEntryDao.insertJournalEntry(entry.toEntity())
    }

    override suspend fun updateJournalEntry(entry: JournalEntry) {
        journalEntryDao.updateJournalEntry(entry.toEntity())
    }

    override suspend fun deleteJournalEntry(entry: JournalEntry) {
        journalEntryDao.deleteJournalEntry(entry.toEntity())
    }

    override fun getJournalEntriesBetween(start: Long, end: Long): Flow<List<JournalEntry>> {
        return journalEntryDao.getJournalEntriesBetween(start, end).map { list ->
            list.map { it.toDomain() }
        }
    }

    override fun getJournalEntryForDate(date: Long): Flow<JournalEntry?> {
        return journalEntryDao.getJournalEntryForDate(date).map { entity ->
            entity?.toDomain()
        }
    }
}
