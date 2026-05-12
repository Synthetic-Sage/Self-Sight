package com.diary.mirroroftruth.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.mirroroftruth.domain.model.JournalEntry
import com.diary.mirroroftruth.domain.model.Task
import com.diary.mirroroftruth.domain.repository.JournalEntryRepository
import com.diary.mirroroftruth.domain.repository.TaskRepository
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class JournalViewModel @Inject constructor(
    private val journalRepo: JournalEntryRepository,
    private val taskRepo: TaskRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(JournalState())
    val state: StateFlow<JournalState> = _state.asStateFlow()

    /** Start-of-today in millis (recomputed each time so midnight roll-over is safe) */
    private val startOfToday: Long
        get() = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    /** The date currently being viewed (start-of-day millis) */
    private var viewingDate: Long = startOfToday

    private var entryLoadJob: Job? = null

    init {
        viewingDate = startOfToday
        _state.update { it.copy(currentDate = viewingDate, isPastDate = false) }
        loadEntryForDate(viewingDate)
    }

    // ── Date navigation ────────────────────────────────────────────────────────

    private fun goToPreviousDay() {
        viewingDate -= 86_400_000L
        switchToDate(viewingDate)
    }

    private fun goToNextDay() {
        val nextDate = viewingDate + 86_400_000L
        // Don't allow navigation beyond today
        if (nextDate > startOfToday) return
        viewingDate = nextDate
        switchToDate(viewingDate)
    }

    private fun switchToDate(date: Long) {
        val isPast = date < startOfToday
        _state.update {
            it.copy(
                currentDate = date,
                isPastDate = isPast,
                // Clear input state — will be refilled from DB if an entry exists
                selectedTags = emptyList(),
                wentWell = "",
                toImprove = "",
                learning = "",
                newTasks = emptyList(),
                imagePath = null,
                isSaved = false
            )
        }
        loadEntryForDate(date)
    }

    private fun loadEntryForDate(date: Long) {
        entryLoadJob?.cancel()
        entryLoadJob = viewModelScope.launch {
            journalRepo.getJournalEntryForDate(date).collect { entry ->
                if (entry != null) {
                    _state.update {
                        it.copy(
                            selectedTags = entry.emotionTags,
                            wentWell = entry.wentWell,
                            toImprove = entry.toImprove,
                            learning = entry.learning,
                            additionalAnswers = if (entry.content.isEmpty()) emptyList() else entry.content.split("|~|"),
                            imagePath = entry.imagePath
                        )
                    }
                }
            }
        }
    }

    // ── Event handling ─────────────────────────────────────────────────────────

    fun onEvent(event: JournalEvent) {
        val current = _state.value
        when (event) {
            is JournalEvent.NavigateToPreviousDay -> goToPreviousDay()
            is JournalEvent.NavigateToNextDay -> goToNextDay()
            is JournalEvent.OnDateSelected -> {
                viewingDate = event.date
                switchToDate(viewingDate)
            }

            is JournalEvent.OnTagToggled -> {
                if (current.isPastDate) return   // read-only for past
                val tags = current.selectedTags.toMutableList()
                if (tags.contains(event.tag)) tags.remove(event.tag) else tags.add(event.tag)
                _state.update { it.copy(selectedTags = tags, isSaved = false) }
            }
            is JournalEvent.OnWentWellChanged -> {
                if (current.isPastDate) return
                _state.update { it.copy(wentWell = event.text, isSaved = false) }
            }
            is JournalEvent.OnToImproveChanged -> {
                if (current.isPastDate) return
                _state.update { it.copy(toImprove = event.text, isSaved = false) }
            }
            is JournalEvent.OnLearningChanged -> {
                if (current.isPastDate) return
                _state.update { it.copy(learning = event.text, isSaved = false) }
            }
            is JournalEvent.OnAdditionalAnswerChanged -> {
                if (current.isPastDate) return
                val newAnswers = current.additionalAnswers.toMutableList()
                while (newAnswers.size <= event.index) {
                    newAnswers.add("")
                }
                newAnswers[event.index] = event.text
                _state.update { it.copy(additionalAnswers = newAnswers, isSaved = false) }
            }
            is JournalEvent.OnAddNewTask -> {
                if (current.isPastDate) return
                if (event.title.isNotBlank()) {
                    _state.update { it.copy(newTasks = it.newTasks + event.title, isSaved = false) }
                }
            }
            is JournalEvent.OnRemoveNewTask -> {
                if (current.isPastDate) return
                _state.update { it.copy(newTasks = it.newTasks - event.title, isSaved = false) }
            }
            is JournalEvent.OnSaveEntry -> {
                if (!current.isPastDate) saveEntry()
            }
            is JournalEvent.OnImageAdded -> {
                if (current.isPastDate) return
                viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                    try {
                        val memoriesDir = File(context.filesDir, "memories")
                        if (!memoriesDir.exists()) memoriesDir.mkdirs()
                        
                        val file = File(memoriesDir, "memory_${System.currentTimeMillis()}.jpg")
                        context.contentResolver.openInputStream(event.uri)?.use { input ->
                            FileOutputStream(file).use { output ->
                                input.copyTo(output)
                            }
                        }
                        
                        _state.update { it.copy(imagePath = file.absolutePath, isSaved = false) }
                    } catch (_: Exception) {
                        // Silently ignore — image save failed (storage permission or I/O error)
                    }
                }
            }
            is JournalEvent.OnImageRemoved -> {
                if (current.isPastDate) return
                current.imagePath?.let { path ->
                    try { File(path).delete() } catch (e: Exception) {}
                }
                _state.update { it.copy(imagePath = null, isSaved = false) }
            }
        }
    }

    private fun saveEntry() {
        val current = _state.value
        viewModelScope.launch {
            val entry = JournalEntry(
                date = viewingDate,
                emotionTags = current.selectedTags,
                content = current.additionalAnswers.joinToString("|~|"),
                wentWell = current.wentWell,
                toImprove = current.toImprove,
                learning = current.learning,
                imagePath = current.imagePath
            )
            journalRepo.insertJournalEntry(entry)

            // Save new tasks for the next day relative to the viewed date
            val nextDay = viewingDate + 86_400_000L
            current.newTasks.forEach { taskTitle ->
                taskRepo.insertTask(
                    Task(
                        title = taskTitle,
                        description = "",
                        isCompleted = false,
                        createdAt = System.currentTimeMillis(),
                        dueDate = nextDay
                    )
                )
            }

            _state.update { it.copy(isSaved = true, newTasks = emptyList()) }
        }
    }
}
