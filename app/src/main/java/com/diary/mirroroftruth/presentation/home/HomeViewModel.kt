package com.diary.mirroroftruth.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diary.mirroroftruth.domain.model.Goal
import com.diary.mirroroftruth.domain.model.Task
import com.diary.mirroroftruth.domain.repository.GoalRepository
import com.diary.mirroroftruth.domain.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val taskRepo: TaskRepository,
    private val goalRepo: GoalRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }
        
        // Get start of today
        val startOfToday = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        _state.update { it.copy(currentDate = startOfToday) }

        // Observe Tasks
        viewModelScope.launch {
            taskRepo.getTasksForDate(startOfToday).collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }

        // Observe Goals
        viewModelScope.launch {
            goalRepo.getAllGoals().collect { goals ->
                _state.update { it.copy(goals = goals) }
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.OnToggleTaskCompletion -> {
                viewModelScope.launch {
                    val updatedTask = event.task.copy(isCompleted = event.isCompleted)
                    taskRepo.updateTask(updatedTask)
                }
            }
            is HomeEvent.OnAddSampleData -> {
                viewModelScope.launch {
                    goalRepo.insertGoal(
                        Goal(
                            title = "Read 50 Books",
                            description = "Read 50 books by the end of the year.",
                            progress = 12f,
                            target = 50f,
                            createdAt = System.currentTimeMillis(),
                            deadline = null
                        )
                    )
                    
                    val today = _state.value.currentDate
                    taskRepo.insertTask(
                        Task(
                            title = "Morning Meditation",
                            description = "10 minutes of mindfulness",
                            isCompleted = false,
                            createdAt = System.currentTimeMillis(),
                            dueDate = today
                        )
                    )
                    taskRepo.insertTask(
                        Task(
                            title = "Workout",
                            description = "Push day at the gym",
                            isCompleted = true,
                            createdAt = System.currentTimeMillis(),
                            dueDate = today
                        )
                    )
                }
            }
        }
    }
}
