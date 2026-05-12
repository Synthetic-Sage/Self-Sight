package com.diary.mirroroftruth.presentation.settings

import android.net.Uri
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import com.diary.mirroroftruth.domain.repository.GoalRepository
import com.diary.mirroroftruth.domain.repository.TaskRepository
import com.diary.mirroroftruth.domain.model.JournalEntry
import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.Task
import com.diary.mirroroftruth.core.utils.AESUtils
import com.google.gson.Gson
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

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

data class SettingsState(
    val exportStatus: String = "",
    val importStatus: String = "",
    val showClearConfirmDialog: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val pendingAction: PendingAction? = null,
    val pendingUri: Uri? = null,
    val showFaq: Boolean = false,
    val largeFontEnabled: Boolean = false,
    val showWentWellPrompt: Boolean = true,
    val showToImprovePrompt: Boolean = true,
    val showLearningPrompt: Boolean = true,
    val selectedFont: String = "Modern",
    // Custom journal prompts
    val journalPrompts: List<String> = listOf("What went well today?", "Things to improve", "Today's learning"),
    val editingPromptIndex: Int? = null   // null = not editing
)

enum class PendingAction { EXPORT, IMPORT }

sealed interface SettingsEvent {
    data class OnExportRequested(val uri: Uri) : SettingsEvent
    data class OnImportRequested(val uri: Uri) : SettingsEvent
    data class OnPasswordSubmitted(val password: String) : SettingsEvent
    object OnPasswordDismissed : SettingsEvent
    object OnClearDataRequested : SettingsEvent
    object OnClearDataConfirmed : SettingsEvent
    object OnDismissDialog : SettingsEvent
    object OnToggleFaq : SettingsEvent
    object OnToggleLargeFont : SettingsEvent
    object OnToggleWentWell : SettingsEvent
    object OnToggleToImprove : SettingsEvent
    object OnToggleLearning : SettingsEvent
    data class OnFontSelected(val font: String) : SettingsEvent
    // Custom prompts
    object OnAddPrompt : SettingsEvent
    data class OnEditPrompt(val index: Int, val newText: String) : SettingsEvent
    data class OnDeletePrompt(val index: Int) : SettingsEvent
    data class OnStartEditingPrompt(val index: Int?) : SettingsEvent
}

/**
 * ViewModel for the Settings Screen.
 * Handles app-wide configuration, data portability, and customization.
 *
 * Core Responsibilities:
 * - App Configuration: Large font, font style, and prompt visibility (persisted via DataStore).
 * - Custom Prompts: Logic for managing a user-defined list of reflection questions.
 * - Data Portability: Encrypted JSON Export/Import via AES-256 for secure backups.
 * - Data Management: Global reset functionality to wipe local database state.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val journalRepo: JournalEntryRepository,
    private val goalRepo: GoalRepository,
    private val taskRepo: TaskRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    companion object {
        val KEY_LARGE_FONT   = booleanPreferencesKey("large_font")
        val KEY_WENT_WELL    = booleanPreferencesKey("show_went_well")
        val KEY_TO_IMPROVE   = booleanPreferencesKey("show_to_improve")
        val KEY_LEARNING     = booleanPreferencesKey("show_learning")
        val KEY_FONT         = stringPreferencesKey("selected_font")
        val KEY_PROMPTS      = stringPreferencesKey("journal_prompts")  // pipe-separated
        val DEFAULT_PROMPTS  = listOf("What went well today?", "Things to improve", "Today's learning")
    }

    init {
        viewModelScope.launch {
            context.settingsDataStore.data.collect { prefs ->
                val savedPrompts = prefs[KEY_PROMPTS]?.split("|")?.filter { it.isNotBlank() } ?: DEFAULT_PROMPTS
                _state.update {
                    it.copy(
                        largeFontEnabled    = prefs[KEY_LARGE_FONT]  ?: false,
                        showWentWellPrompt  = prefs[KEY_WENT_WELL]   ?: true,
                        showToImprovePrompt = prefs[KEY_TO_IMPROVE]  ?: true,
                        showLearningPrompt  = prefs[KEY_LEARNING]    ?: true,
                        selectedFont        = prefs[KEY_FONT]        ?: "Modern",
                        journalPrompts      = savedPrompts
                    )
                }
            }
        }
    }

    fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.OnExportRequested    -> _state.update { it.copy(showPasswordDialog = true, pendingAction = PendingAction.EXPORT, pendingUri = event.uri) }
            is SettingsEvent.OnImportRequested    -> _state.update { it.copy(showPasswordDialog = true, pendingAction = PendingAction.IMPORT, pendingUri = event.uri) }
            is SettingsEvent.OnPasswordDismissed  -> _state.update { it.copy(showPasswordDialog = false, pendingAction = null, pendingUri = null) }
            is SettingsEvent.OnPasswordSubmitted  -> {
                val action = _state.value.pendingAction
                val uri = _state.value.pendingUri
                _state.update { it.copy(showPasswordDialog = false, pendingAction = null, pendingUri = null) }
                if (uri != null) {
                    if (action == PendingAction.EXPORT) exportToUri(uri, event.password)
                    else if (action == PendingAction.IMPORT) importFromUri(uri, event.password)
                }
            }
            is SettingsEvent.OnClearDataRequested -> _state.update { it.copy(showClearConfirmDialog = true) }
            is SettingsEvent.OnClearDataConfirmed -> {
                _state.update { it.copy(showClearConfirmDialog = false) }
                clearAllData()
            }
            is SettingsEvent.OnDismissDialog      -> _state.update { it.copy(showClearConfirmDialog = false) }
            is SettingsEvent.OnToggleFaq          -> _state.update { it.copy(showFaq = !it.showFaq) }
            is SettingsEvent.OnToggleLargeFont    -> togglePref(KEY_LARGE_FONT,  _state.value.largeFontEnabled) { v -> _state.update { it.copy(largeFontEnabled = v) } }
            is SettingsEvent.OnToggleWentWell     -> togglePref(KEY_WENT_WELL,   _state.value.showWentWellPrompt) { v -> _state.update { it.copy(showWentWellPrompt = v) } }
            is SettingsEvent.OnToggleToImprove    -> togglePref(KEY_TO_IMPROVE,  _state.value.showToImprovePrompt) { v -> _state.update { it.copy(showToImprovePrompt = v) } }
            is SettingsEvent.OnToggleLearning     -> togglePref(KEY_LEARNING,    _state.value.showLearningPrompt) { v -> _state.update { it.copy(showLearningPrompt = v) } }
            is SettingsEvent.OnFontSelected       -> {
                _state.update { it.copy(selectedFont = event.font) }
                viewModelScope.launch {
                    context.settingsDataStore.edit { it[KEY_FONT] = event.font }
                }
            }
            is SettingsEvent.OnAddPrompt          -> savePrompts(_state.value.journalPrompts + "New question")
            is SettingsEvent.OnEditPrompt         -> {
                val updated = _state.value.journalPrompts.toMutableList().also { it[event.index] = event.newText }
                _state.update { it.copy(editingPromptIndex = null) }
                savePrompts(updated)
            }
            is SettingsEvent.OnDeletePrompt       -> savePrompts(_state.value.journalPrompts.filterIndexed { i, _ -> i != event.index })
            is SettingsEvent.OnStartEditingPrompt -> _state.update { it.copy(editingPromptIndex = event.index) }
        }
    }

    private fun savePrompts(prompts: List<String>) {
        _state.update { it.copy(journalPrompts = prompts) }
        viewModelScope.launch {
            context.settingsDataStore.edit { it[KEY_PROMPTS] = prompts.joinToString("|") }
        }
    }

    private fun togglePref(key: Preferences.Key<Boolean>, current: Boolean, update: (Boolean) -> Unit) {
        val newVal = !current
        update(newVal)
        viewModelScope.launch {
            context.settingsDataStore.edit { it[key] = newVal }
        }
    }

    private fun clearAllData() {
        viewModelScope.launch {
            val entries = journalRepo.getJournalEntriesBetween(0L, Long.MAX_VALUE).first()
            entries.forEach { journalRepo.deleteJournalEntry(it) }
            val goals = goalRepo.getAllGoals().first()
            goals.forEach { goalRepo.deleteGoal(it) }
            val tasks = taskRepo.getAllTasks().first()
            tasks.forEach { taskRepo.deleteTask(it) }
        }
    }

    private data class BackupData(
        val journalEntries: List<JournalEntry>,
        val goals: List<Goal>,
        val tasks: List<Task>
    )

    private fun exportToUri(uri: Uri, password: String) {
        viewModelScope.launch {
            try {
                val entries = journalRepo.getJournalEntriesBetween(0L, Long.MAX_VALUE).first()
                val goals = goalRepo.getAllGoals().first()
                val tasks = taskRepo.getAllTasks().first()
                
                val backupData = BackupData(entries, goals, tasks)
                val jsonString = Gson().toJson(backupData)
                val encryptedData = AESUtils.encrypt(jsonString, password)
                
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    BufferedWriter(OutputStreamWriter(outputStream)).use { writer ->
                        writer.write(encryptedData)
                    }
                }
                _state.update { it.copy(exportStatus = "Exported securely ✓") }
            } catch (e: Exception) {
                _state.update { it.copy(exportStatus = "Export failed: ${e.message}") }
            }
        }
    }
    
    private fun importFromUri(uri: Uri, password: String) {
        viewModelScope.launch {
            try {
                val encryptedData = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                if (encryptedData == null) {
                    _state.update { it.copy(importStatus = "Failed to read file") }
                    return@launch
                }
                
                val jsonString = AESUtils.decrypt(encryptedData, password)
                val backupData = Gson().fromJson(jsonString, BackupData::class.java)
                
                backupData.journalEntries.forEach { journalRepo.insertJournalEntry(it.copy(id = 0)) }
                backupData.goals.forEach { goalRepo.insertGoal(it.copy(id = 0)) }
                backupData.tasks.forEach { taskRepo.insertTask(it.copy(id = 0)) }
                
                _state.update { it.copy(importStatus = "Imported successfully ✓") }
            } catch (e: Exception) {
                _state.update { it.copy(importStatus = "Import failed: Invalid password or corrupted file") }
            }
        }
    }
}
