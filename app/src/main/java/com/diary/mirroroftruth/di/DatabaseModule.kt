package com.diary.mirroroftruth.di

import android.app.Application
import androidx.room.Room
import com.diary.mirroroftruth.data.local.dao.GoalDao
import com.diary.mirroroftruth.data.local.dao.JournalEntryDao
import com.diary.mirroroftruth.data.local.dao.TaskDao
import com.diary.mirroroftruth.data.local.database.MirrorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMirrorDatabase(app: Application): MirrorDatabase {
        return Room.databaseBuilder(
            app,
            MirrorDatabase::class.java,
            "mirror_db"
        )
        .addMigrations(MirrorDatabase.MIGRATION_4_5, MirrorDatabase.MIGRATION_5_6)
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideTaskDao(db: MirrorDatabase): TaskDao {
        return db.taskDao
    }

    @Provides
    @Singleton
    fun provideGoalDao(db: MirrorDatabase): GoalDao {
        return db.goalDao
    }

    @Provides
    @Singleton
    fun provideJournalEntryDao(db: MirrorDatabase): JournalEntryDao {
        return db.journalEntryDao
    }
}
