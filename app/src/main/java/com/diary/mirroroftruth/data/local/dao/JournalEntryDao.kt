package com.diary.mirroroftruth.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.diary.mirroroftruth.data.local.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJournalEntry(entry: JournalEntryEntity)

    @Update
    suspend fun updateJournalEntry(entry: JournalEntryEntity)

    @Delete
    suspend fun deleteJournalEntry(entry: JournalEntryEntity)

    @Query("SELECT * FROM journal_entries WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getJournalEntriesBetween(start: Long, end: Long): Flow<List<JournalEntryEntity>>
    
    @Query("SELECT * FROM journal_entries WHERE date = :date LIMIT 1")
    fun getJournalEntryForDate(date: Long): Flow<JournalEntryEntity?>
}
