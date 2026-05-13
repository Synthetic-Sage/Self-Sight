package com.diary.mirroroftruth.presentation.home

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.diary.mirroroftruth.presentation.home.components.GoalCard
import com.diary.mirroroftruth.presentation.home.components.TaskItem
import com.diary.mirroroftruth.presentation.home.components.AddStepBottomSheet
import com.diary.mirroroftruth.presentation.home.components.CelebrationOverlay
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
import org.burnoutcrew.reorderable.*

/**
 * The main entry point of the app (the Dashboard).
 * It manages the daily overview, including:
 * - Fortune Cookie: A daily rotating quote from an internal dataset.
 * - Big Steps: Long-term goals/projects with nested Small Steps.
 * - Daily Tasks: A reorderable, priority-based list of immediate actions.
 *
 * Key Features:
 * - Celebrations: Triggers confetti/snackbars when milestones are reached.
 * - Task Reordering: Users can drag tasks to prioritize their day.
 * - Goal Visualization: Progress indicators based on Small Step completion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    diaryName: String,
    onEvent: (HomeEvent) -> Unit
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val todayString = dateFormatter.format(Date(state.currentDate))
    var newTaskTitle by remember { mutableStateOf("") }
    var newTaskPriority by remember { mutableStateOf<com.diary.mirroroftruth.domain.model.TaskPriority>(com.diary.mirroroftruth.domain.model.TaskPriority.NONE) }
    var isTaskInputFocused by remember { mutableStateOf(false) }
    var showAddStepSheet by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.showBigStepCelebration) {
        if (state.showBigStepCelebration) {
            kotlinx.coroutines.delay(3000)
            onEvent(HomeEvent.OnDismissCelebration)
        }
    }

    LaunchedEffect(state.showSmallStepCelebration) {
        if (state.showSmallStepCelebration) {
            snackbarHostState.showSnackbar("Small Step completed! Great job! 🌟")
            onEvent(HomeEvent.OnDismissCelebration)
        }
    }
    
    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to ->
            val fromKey = from.key as? Long
            val toKey = to.key as? Long
            if (fromKey != null && toKey != null) {
                val fromIndex = state.tasks.indexOfFirst { it.id == fromKey }
                val toIndex = state.tasks.indexOfFirst { it.id == toKey }
                if (fromIndex != -1 && toIndex != -1) {
                    onEvent(HomeEvent.OnReorderTasks(fromIndex, toIndex))
                }
            }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = diaryName,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 26.sp,
                                letterSpacing = 1.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Self Sight",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontStyle = FontStyle.Italic,
                                    fontWeight = FontWeight.Light
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "•  $todayString",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddStepSheet = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Step")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .reorderable(reorderState),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            if (state.dailyQuote.isNotEmpty()) {
                item {
                    com.diary.mirroroftruth.presentation.home.components.FortuneCookie(
                        quote = state.dailyQuote,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            if (state.bigSteps.isNotEmpty()) {
                item {
                    var expanded by remember { mutableStateOf(false) }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🚀 Big Steps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse Big Steps",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    AnimatedVisibility(visible = expanded) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            items(state.bigSteps) { goal ->
                                GoalCard(goal = goal, onProgressChange = { onEvent(HomeEvent.OnStepProgress(goal, it)) })
                            }
                        }
                    }
                }
            }

            if (state.smallSteps.isNotEmpty()) {
                item {
                    var expanded by remember { mutableStateOf(true) }
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🌱 Small Steps",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = if (expanded) androidx.compose.material.icons.Icons.Default.KeyboardArrowUp else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand/Collapse Small Steps",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    
                    AnimatedVisibility(visible = expanded) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            items(state.smallSteps) { goal ->
                                GoalCard(goal = goal, onProgressChange = { onEvent(HomeEvent.OnStepProgress(goal, it)) })
                            }
                        }
                    }
                }
            }

            item {
                val completedTasks = state.tasks.count { it.isCompleted }
                val totalTasks = state.tasks.size
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Tasks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (totalTasks > 0) {
                        Text(
                            text = "🎯 $completedTasks/$totalTasks Completed",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            if (state.tasks.isEmpty()) {
                item {
                    Text(
                        text = "No tasks for today. Take a deep breath.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(
                    items = state.tasks,
                    key = { it.id }
                ) { task ->
                    ReorderableItem(reorderState, key = task.id) { isDragging ->
                        val elevation by androidx.compose.animation.core.animateDpAsState(if (isDragging) 8.dp else 0.dp)
                        
                        val dismissState = androidx.compose.material3.rememberSwipeToDismissBoxState(
                            confirmValueChange = { dismissValue ->
                                if (dismissValue == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) {
                                    onEvent(HomeEvent.OnToggleTaskCompletion(task, true))
                                    false // Snap back, it's just a quick toggle
                                } else if (dismissValue == androidx.compose.material3.SwipeToDismissBoxValue.EndToStart) {
                                    onEvent(HomeEvent.OnDeleteTask(task))
                                    true
                                } else {
                                    false
                                }
                            }
                        )
                        
                        androidx.compose.material3.SwipeToDismissBox(
                            state = dismissState,
                            modifier = Modifier
                                .detectReorderAfterLongPress(reorderState)
                                .padding(vertical = if (isDragging) 4.dp else 0.dp),
                            backgroundContent = {
                                val direction = dismissState.dismissDirection
                                val color = when (direction) {
                                    androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primary // Swipe right to complete
                                    androidx.compose.material3.SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error // Swipe left to delete
                                    else -> Color.Transparent
                                }
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(color)
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = if (direction == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) Alignment.CenterStart else Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = if (direction == androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd) androidx.compose.material.icons.Icons.Default.Check else androidx.compose.material.icons.Icons.Default.Delete,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                TaskItem(
                                    task = task,
                                    onCheckedChange = { isChecked ->
                                        onEvent(HomeEvent.OnToggleTaskCompletion(task, isChecked))
                                    },
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .shadow(elevation, RoundedCornerShape(12.dp))
                                )
                            }
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newTaskTitle,
                            onValueChange = { newTaskTitle = it },
                            label = { Text("Add new task") },
                            modifier = Modifier.weight(1f).onFocusChanged { isTaskInputFocused = it.isFocused },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    val colorTag = if (newTaskPriority != com.diary.mirroroftruth.domain.model.TaskPriority.NONE) newTaskPriority.colorHex else null
                                    onEvent(HomeEvent.OnAddTask(newTaskTitle, colorTag))
                                    newTaskTitle = ""
                                    newTaskPriority = com.diary.mirroroftruth.domain.model.TaskPriority.NONE
                                }
                            },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Task",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    // Priority Selector (Visible when typing)
                    AnimatedVisibility(visible = isTaskInputFocused || newTaskTitle.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            com.diary.mirroroftruth.domain.model.TaskPriority.values().filter { it != com.diary.mirroroftruth.domain.model.TaskPriority.NONE }.forEach { priority ->
                                val color = Color(android.graphics.Color.parseColor(priority.colorHex))
                                val isSelected = newTaskPriority == priority
                                
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent)
                                        .clickable { 
                                            newTaskPriority = if (isSelected) com.diary.mirroroftruth.domain.model.TaskPriority.NONE else priority 
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(modifier = Modifier.size(12.dp).clip(androidx.compose.foundation.shape.CircleShape).background(color))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = priority.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        
        if (state.showBigStepCelebration) {
            CelebrationOverlay()
        }
        
        if (showAddStepSheet) {
            AddStepBottomSheet(
                onDismiss = { showAddStepSheet = false },
                onAddStep = { title, type, target, desc ->
                    onEvent(HomeEvent.OnAddStep(title, type, target, desc))
                }
            )
        }
        }
    }
}

