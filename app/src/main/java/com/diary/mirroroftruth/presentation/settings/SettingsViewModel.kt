package com.diary.mirroroftruth.presentation.settings

import android.net.Uri
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class SettingsState(
    val exportStatus: String = "",
    val showClearConfirmDialog: Boolean = false
)

sealed interface SettingsEvent {
    data class OnExportToUri(val uri: Uri) : SettingsEvent
    object OnClearDataRequested : SettingsEvent
    object OnClearDataConfirmed : SettingsEvent
    object OnDismissDialog : SettingsEvent
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val journalRepo: JournalEntryRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnExportToUri -> exportToUri(event.uri)
            is SettingsEvent.OnClearDataRequested -> _state.update { it.copy(showClearConfirmDialog = true) }
            is SettingsEvent.OnClearDataConfirmed -> _state.update { it.copy(showClearConfirmDialog = false) }
            is SettingsEvent.OnDismissDialog -> _state.update { it.copy(showClearConfirmDialog = false) }
        }
    }

    private fun exportToUri(uri: Uri) {
        viewModelScope.launch {
            try {
                val entries = journalRepo.getJournalEntriesBetween(0L, Long.MAX_VALUE).first()
                val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                        writer.write("date,mood,wentWell,challenges,gratitude,tomorrowsTask")
                        writer.newLine()
                        entries.forEach { entry ->
                            val parts = entry.promptResponses.split("|")
                            val wentWell = parts.getOrElse(0) { "" }.escapeCsv()
                            val challenges = parts.getOrElse(1) { "" }.escapeCsv()
                            val gratitude = parts.getOrElse(2) { "" }.escapeCsv()
                            val date = dateFormatter.format(Date(entry.date))
                            writer.write("$date,${entry.mood.escapeCsv()},$wentWell,$challenges,$gratitude,${entry.content.escapeCsv()}")
                            writer.newLine()
                        }
                    }
                }
                _state.update { it.copy(exportStatus = "Exported ${entries.size} entries ✓") }
            } catch (e: Exception) {
                _state.update { it.copy(exportStatus = "Export failed: ${e.message}") }
            }
        }
    }

    private fun String.escapeCsv(): String {
        return if (contains(",") || contains("\"") || contains("\n")) {
            "\"${replace("\"", "\"\"")}\""
        } else this
    }
}
