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
        "Success doesn’t just find you. You have to go out and get it.",
        "The harder you work for something, the greater you'll feel when you achieve it.",
        "Dream bigger. Do bigger.",
        "Don't stop when you're tired. Stop when you're done.",
        "Wake up with determination. Go to bed with satisfaction.",
        "Do something today that your future self will thank you for.",
        "Little things make big days.",
        "It's going to be hard, but hard does not mean impossible.",
        "Don't wait for opportunity. Create it.",
        "Sometimes we're tested not to show our weaknesses, but to discover our strengths.",
        "The key to success is to focus on goals, not obstacles.",
        "Dream it. Believe it. Build it.",
        "If it doesn't challenge you, it won't change you.",
        "Action is the foundational key to all success.",
        "Small steps in the right direction can turn out to be the biggest step of your life.",
        "You are entirely up to you.",
        "To live a creative life, we must lose our fear of being wrong.",
        "Good things come to people who wait, but better things come to those who go out and get them.",
        "You don't have to be great to start, but you have to start to be great.",
        "Every day is a second chance.",
        "Start where you are. Use what you have. Do what you can.",
        "Turn your wounds into wisdom.",
        "Doubt kills more dreams than failure ever will.",
        "The best way to predict the future is to create it.",
        "What you get by achieving your goals is not as important as what you become by achieving them.",
        "Success is not final, failure is not fatal: it is the courage to continue that counts.",
        "It always seems impossible until it's done.",
        "Keep your face always toward the sunshine—and shadows will fall behind you.",
        "You are never too old to set another goal or to dream a new dream.",
        "The future belongs to those who believe in the beauty of their dreams.",
        "If opportunity doesn't knock, build a door.",
        "No pressure, no diamonds.",
        "If you want to lift yourself up, lift up someone else.",
        "A year from now you may wish you had started today.",
        "Do what you can, with what you have, where you are.",
        "Change your thoughts and you change your world.",
        "It is never too late to be what you might have been.",
        "I didn't fail the test. I just found 100 ways to do it wrong.",
        "Nothing is impossible, the word itself says 'I'm possible'!",
        "Strive not to be a success, but rather to be of value.",
        "Definiteness of purpose is the starting point of all achievement.",
        "Life is what happens to you while you're busy making other plans.",
        "We become what we think about.",
        "Twenty years from now you will be more disappointed by the things that you didn't do than by the ones you did do.",
        "The only person you are destined to become is the person you decide to be.",
        "Fall seven times and stand up eight.",
        "When everything seems to be going against you, remember that the airplane takes off against the wind, not with it.",
        "Too many of us are not living our dreams because we are living our fears.",
        "I attribute my success to this: I never gave or took any excuse.",
        "The mind is everything. What you think you become.",
        "The most difficult thing is the decision to act, the rest is merely tenacity.",
        "Every strike brings me closer to the next home run.",
        "Your time is limited, so don't waste it living someone else's life.",
        "Winning isn't everything, but wanting to win is.",
        "I am not a product of my circumstances. I am a product of my decisions.",
        "Either you run the day, or the day runs you.",
        "Whether you think you can or you think you can't, you're right.",
        "The two most important days in your life are the day you are born and the day you find out why.",
        "Whatever you can do, or dream you can, begin it. Boldness has genius, power and magic in it.",
        "Life shrinks or expands in proportion to one's courage.",
        "If you hear a voice within you say 'you cannot paint,' then by all means paint and that voice will be silenced.",
        "There is only one way to avoid criticism: do nothing, say nothing, and be nothing.",
        "Ask and it will be given to you; search, and you will find; knock and the door will be opened for you.",
        "Certain things catch your eye, but pursue only those that capture the heart.",
        "Believe you can and you're halfway there.",
        "Everything you've ever wanted is on the other side of fear.",
        "We can easily forgive a child who is afraid of the dark; the real tragedy of life is when men are afraid of the light.",
        "Start where you are. Use what you have. Do what you can.",
        "When I let go of what I am, I become what I might be.",
        "Happiness is not something readymade. It comes from your own actions.",
        "If you're offered a seat on a rocket ship, don't ask what seat! Just get on.",
        "First, have a definite, clear practical ideal; a goal, an objective.",
        "Second, have the necessary means to achieve your ends; wisdom, money, materials, and methods.",
        "Third, adjust all your means to that end.",
        "If the wind will not serve, take to the oars.",
        "You can't fall if you don't climb. But there's no joy in living your whole life on the ground.",
        "We must believe that we are gifted for something, and that this thing, at whatever cost, must be attained.",
        "Too many of us are not living our dreams because we are living our fears.",
        "Challenges are what make life interesting and overcoming them is what makes life meaningful.",
        "If you want to lift yourself up, lift up someone else.",
        "I have been impressed with the urgency of doing. Knowing is not enough; we must apply. Being willing is not enough; we must do.",
        "Limitations live only in our minds. But if we use our imaginations, our possibilities become limitless.",
        "You take your life in your own hands, and what happens? A terrible thing, no one to blame.",
        "What's money? A man is a success if he gets up in the morning and goes to bed at night and in between does what he wants to do.",
        "I didn't fail the test. I just found 100 ways to do it wrong.",
        "In order to succeed, your desire for success should be greater than your fear of failure.",
        "A person who never made a mistake never tried anything new.",
        "The person who says it cannot be done should not interrupt the person who is doing it.",
        "There are no traffic jams along the extra mile.",
        "It is never too late to be what you might have been.",
        "You become what you believe.",
        "I would rather die of passion than of boredom.",
        "A truly rich man is one whose children run into his arms when his hands are empty.",
        "It is not what you do for your children, but what you have taught them to do for themselves, that will make them successful human beings.",
        "If you want your children to turn out well, spend twice as much time with them, and half as much money.",
        "Build your own dreams, or someone else will hire you to build theirs.",
        "The battles that count aren't the ones for gold medals. The struggles within yourself—the invisible battles inside all of us—that's where it's at.",
        "Education costs money. But then so does ignorance.",
        "I have learned over the years that when one's mind is made up, this diminishes fear.",
        "It does not matter how slowly you go as long as you do not stop.",
        "If you look at what you have in life, you'll always have more.",
        "Remember that not getting what you want is sometimes a wonderful stroke of luck.",
        "You can't use up creativity. The more you use, the more you have.",
        "Dream big and dare to fail.",
        "Our lives begin to end the day we become silent about things that matter.",
        "Do what you can, where you are, with what you have.",
        "If you do what you've always done, you'll get what you've always gotten.",
        "Dreaming, after all, is a form of planning.",
        "It's your place in the world; it's your life. Go on and do all you can with it, and make it the life you want to live."
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
                                colorTag = event.colorTag,
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
