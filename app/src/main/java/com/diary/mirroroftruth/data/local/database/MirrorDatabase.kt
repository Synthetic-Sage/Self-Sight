package com.diary.mirroroftruth.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
    version = 5,
    exportSchema = false
)
@androidx.room.TypeConverters(Converters::class)
abstract class MirrorDatabase : RoomDatabase() {
    abstract val taskDao: TaskDao
    abstract val goalDao: GoalDao
    abstract val journalEntryDao: JournalEntryDao

    companion object {
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE goals ADD COLUMN type TEXT NOT NULL DEFAULT 'BIG'")
            }
        }
    }
}
