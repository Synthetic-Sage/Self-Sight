package com.diary.mirroroftruth.presentation.insights

sealed interface InsightsEvent {
    object Refresh : InsightsEvent
}
