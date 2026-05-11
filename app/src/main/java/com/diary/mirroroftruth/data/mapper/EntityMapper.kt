package com.diary.mirroroftruth.data.mapper

import com.diary.mirroroftruth.data.local.entity.GoalEntity
import com.diary.mirroroftruth.data.local.entity.JournalEntryEntity
import com.diary.mirroroftruth.data.local.entity.TaskEntity
import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.JournalEntry
import com.diary.mirroroftruth.domain.model.Task

// Task Mappers
fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate,
        colorTag = colorTag,
        positionIndex = positionIndex
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        isCompleted = isCompleted,
        createdAt = createdAt,
        dueDate = dueDate,
        colorTag = colorTag,
        positionIndex = positionIndex
    )
}

// Goal Mappers
fun GoalEntity.toDomain(): Goal {
    return Goal(
        id = id,
        title = title,
        description = description,
        progress = progress,
        target = target,
        type = type,
        createdAt = createdAt,
        deadline = deadline
    )
}

fun Goal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        description = description,
        progress = progress,
        target = target,
        type = type,
        createdAt = createdAt,
        deadline = deadline
    )
}

// Journal Entry Mappers
fun JournalEntryEntity.toDomain(): JournalEntry {
    return JournalEntry(
        id = id,
        date = date,
        emotionTags = emotionTags,
        content = content,
        wentWell = wentWell,
        toImprove = toImprove,
        learning = learning,
        imagePath = imagePath
    )
}

fun JournalEntry.toEntity(): JournalEntryEntity {
    return JournalEntryEntity(
        id = id,
        date = date,
        emotionTags = emotionTags,
        content = content,
        wentWell = wentWell,
        toImprove = toImprove,
        learning = learning,
        imagePath = imagePath
    )
}
