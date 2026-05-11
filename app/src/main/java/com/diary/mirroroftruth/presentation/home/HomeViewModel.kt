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

    private val quotes = listOf(
        "The only way to do great work is to love what you do.",
        "Believe you can and you're halfway there.",
        "Your limitation—it's only your imagination.",
        "Push yourself, because no one else is going to do it for you.",
        "Sometimes later becomes never. Do it now.",
        "Great things never come from comfort zones.",
        "Dream it. Wish it. Do it.",
        "Success doesn’t just find you. You have to go out and get it."
    )

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

        // Pick a quote based on the day of year
        val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        val quote = quotes[dayOfYear % quotes.size]

        _state.update { it.copy(currentDate = startOfToday, dailyQuote = quote) }

        // Observe Tasks
        viewModelScope.launch {
            taskRepo.getTasksForDate(startOfToday).collect { tasks ->
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            }
        }

        // Observe Goals
        viewModelScope.launch {
            goalRepo.getAllGoals().collect { goals ->
                val bigSteps = goals.filter { it.type == com.diary.mirroroftruth.domain.model.StepType.BIG }
                val smallSteps = goals.filter { it.type == com.diary.mirroroftruth.domain.model.StepType.SMALL }
                _state.update { it.copy(bigSteps = bigSteps, smallSteps = smallSteps) }
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
            is HomeEvent.OnAddTask -> {
                if (event.title.isNotBlank()) {
                    viewModelScope.launch {
                        val currentCount = _state.value.tasks.size
                        taskRepo.insertTask(
                            Task(
                                title = event.title,
                                description = "",
                                isCompleted = false,
                                createdAt = System.currentTimeMillis(),
                                dueDate = _state.value.currentDate,
                                positionIndex = currentCount
                            )
                        )
                    }
                }
            }
            is HomeEvent.OnDeleteTask -> {
                viewModelScope.launch {
                    taskRepo.deleteTask(event.task)
                }
            }
            is HomeEvent.OnReorderTasks -> {
                val currentTasks = _state.value.tasks.toMutableList()
                if (event.fromIndex < 0 || event.toIndex < 0 || event.fromIndex >= currentTasks.size || event.toIndex >= currentTasks.size) return
                
                val taskToMove = currentTasks.removeAt(event.fromIndex)
                currentTasks.add(event.toIndex, taskToMove)
                
                // Update local state immediately for smooth UI
                _state.update { it.copy(tasks = currentTasks) }
                
                // Update position indices and save to DB
                viewModelScope.launch {
                    val updatedTasks = currentTasks.mapIndexed { index, task ->
                        task.copy(positionIndex = index)
                    }
                    taskRepo.updateTasks(updatedTasks)
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
                            type = com.diary.mirroroftruth.domain.model.StepType.BIG,
                            createdAt = System.currentTimeMillis(),
                            deadline = null
                        )
                    )
                    goalRepo.insertGoal(
                        Goal(
                            title = "Drink 2L Water",
                            description = "Stay hydrated today",
                            progress = 0f,
                            target = 1f,
                            type = com.diary.mirroroftruth.domain.model.StepType.SMALL,
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
            is HomeEvent.OnAddStep -> {
                viewModelScope.launch {
                    goalRepo.insertGoal(
                        Goal(
                            title = event.title,
                            description = event.description,
                            progress = 0f,
                            target = event.target,
                            type = event.type,
                            createdAt = System.currentTimeMillis(),
                            deadline = null
                        )
                    )
                }
            }
            is HomeEvent.OnStepProgress -> {
                viewModelScope.launch {
                    val wasCompleted = event.goal.progress >= event.goal.target
                    val isCompleted = event.progress >= event.goal.target
                    
                    val updatedGoal = event.goal.copy(progress = event.progress)
                    goalRepo.updateGoal(updatedGoal)

                    // Trigger celebration if it just became completed
                    if (!wasCompleted && isCompleted) {
                        if (event.goal.type == com.diary.mirroroftruth.domain.model.StepType.BIG) {
                            _state.update { it.copy(showBigStepCelebration = true) }
                        } else {
                            _state.update { it.copy(showSmallStepCelebration = true) }
                        }
                    }
                }
            }
            is HomeEvent.OnDeleteStep -> {
                viewModelScope.launch {
                    goalRepo.deleteGoal(event.goal)
                }
            }
            is HomeEvent.OnDismissCelebration -> {
                _state.update { it.copy(showBigStepCelebration = false, showSmallStepCelebration = false) }
            }
        }
    }
}
