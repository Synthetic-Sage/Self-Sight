package com.diary.mirroroftruth.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diary.mirroroftruth.data.local.dao.GoalDao
import com.diary.mirroroftruth.data.local.dao.JournalEntryDao
import com.diary.mirroroftruth.data.local.dao.TaskDao
import com.diary.mirroroftruth.data.local.entity.GoalEntity
import com.diary.mirroroftruth.data.local.entity.JournalEntryEntity
import com.diary.mirroroftruth.data.local.entity.TaskEntity

@Database(
    entities = [
        TaskEntity::class,
        GoalEntity::class,
        JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MirrorDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val goalDao: GoalDao
    abstract val journalEntryDao: JournalEntryDao
}
