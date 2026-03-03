package com.diary.mirroroftruth.di

import com.diary.mirroroftruth.data.repository.GoalRepositoryImpl
import com.diary.mirroroftruth.data.repository.JournalEntryRepositoryImpl
import com.diary.mirroroftruth.data.repository.TaskRepositoryImpl
import com.diary.mirroroftruth.domain.repository.GoalRepository
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import com.diary.mirroroftruth.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    abstract fun bindGoalRepository(
        goalRepositoryImpl: GoalRepositoryImpl
    ): GoalRepository

    @Binds
    abstract fun bindJournalEntryRepository(
        journalEntryRepositoryImpl: JournalEntryRepositoryImpl
    ): JournalEntryRepository
}
