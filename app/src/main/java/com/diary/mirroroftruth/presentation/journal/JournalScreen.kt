package com.diary.mirroroftruth.presentation.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import coil.compose.AsyncImage
import java.io.File
import com.diary.mirroroftruth.presentation.journal.components.JournalPromptField
import com.diary.mirroroftruth.presentation.journal.components.MoodSelector
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("DEPRECATION")
@Composable
fun JournalScreen(
    state: JournalState,
    onEvent: (JournalEvent) -> Unit,
    largeFontEnabled: Boolean = false,
    journalPrompts: List<String> = listOf("What went well today?", "Things to improve", "Today's learning"),
    showWentWell: Boolean = true,
    showToImprove: Boolean = true,
    showLearning: Boolean = true,
    selectedFont: String = "Modern"
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val todayFormatter = SimpleDateFormat("yyyyMMdd", Locale.getDefault())

    val displayDate = dateFormatter.format(Date(state.currentDate))
    val isToday = todayFormatter.format(Date(state.currentDate)) ==
            todayFormatter.format(Date(System.currentTimeMillis()))

    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = state.currentDate,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis <= System.currentTimeMillis()
            }
        }
    )

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onEvent(JournalEvent.OnDateSelected(it))
                    }
                    showDatePicker = false
                }) { Text("Jump") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { onEvent(JournalEvent.OnImageAdded(it)) }
        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(
                        modifier = Modifier
                            .clickable { showDatePicker = true }
                            .padding(vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isToday) "Today's Reflection" else "Past Entry",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            if (state.isPastDate) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Read only",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Select Date",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                // Date navigation arrows
                navigationIcon = {
                    IconButton(onClick = { onEvent(JournalEvent.NavigateToPreviousDay) }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Previous day",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onEvent(JournalEvent.NavigateToNextDay) },
                        enabled = !isToday   // disable forward arrow on today
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next day",
                            tint = if (isToday) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                                   else MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // ── Photo Memory ──────────────────────────────────────────────────
            item {
                if (state.imagePath != null) {
                    Box(modifier = Modifier.fillMaxWidth().height(250.dp).clip(RoundedCornerShape(12.dp))) {
                        AsyncImage(
                            model = state.imagePath,
                            contentDescription = "Journal Memory",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        // Top Right buttons: Delete and Save
                        Row(
                            modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    try {
                                        val file = File(state.imagePath)
                                        val resolver = context.contentResolver
                                        val contentValues = android.content.ContentValues().apply {
                                            put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, "SelfSight_Memory_${System.currentTimeMillis()}.jpg")
                                            put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/SelfSight")
                                            }
                                        }
                                        val destUri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                                        destUri?.let { dest ->
                                            resolver.openOutputStream(dest)?.use { outStream ->
                                                file.inputStream().use { inStream ->
                                                    inStream.copyTo(outStream)
                                                }
                                            }
                                            android.widget.Toast.makeText(context, "Saved to Pictures/SelfSight", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                },
                                modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Save to Gallery", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            if (!state.isPastDate) {
                                IconButton(
                                    onClick = { onEvent(JournalEvent.OnImageRemoved) },
                                    modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.5f), androidx.compose.foundation.shape.CircleShape)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete Memory", tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                } else if (!state.isPastDate) {
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                androidx.activity.result.PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add Photo Memory", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }

            // ── Vibe Tags ─────────────────────────────────────────────────────
            item {
                MoodSelector(
                    selectedTags = state.selectedTags,
                    onTagToggled = { if (!state.isPastDate) onEvent(JournalEvent.OnTagToggled(it)) }
                )
            }

            // Past-entry banner
            if (state.isPastDate) {
                item {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "📖  Ink has dried — this entry is read-only.",
                            style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Empty past-entry state
            if (state.isPastDate &&
                state.wentWell.isBlank() &&
                state.toImprove.isBlank() &&
                state.learning.isBlank() &&
                state.selectedTags.isEmpty()
            ) {
                item {
                    Text(
                        text = "Nothing was written on this day. 🍂",
                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            // ── Reflection prompts (first 3 use existing DB fields, extras use additionalAnswers) ──
            if (showWentWell) {
                item {
                    JournalPromptField(
                        prompt = journalPrompts.getOrElse(0) { "What went well today?" },
                        value = state.wentWell,
                        onValueChange = { onEvent(JournalEvent.OnWentWellChanged(it)) },
                        readOnly = state.isPastDate,
                        largeFontEnabled = largeFontEnabled,
                        selectedFont = selectedFont
                    )
                }
            }

            if (showToImprove) {
                item {
                    JournalPromptField(
                        prompt = journalPrompts.getOrElse(1) { "Things to improve" },
                        value = state.toImprove,
                        onValueChange = { onEvent(JournalEvent.OnToImproveChanged(it)) },
                        readOnly = state.isPastDate,
                        largeFontEnabled = largeFontEnabled,
                        selectedFont = selectedFont
                    )
                }
            }

            if (showLearning) {
                item {
                    JournalPromptField(
                        prompt = journalPrompts.getOrElse(2) { "Today's learning" },
                        value = state.learning,
                        onValueChange = { onEvent(JournalEvent.OnLearningChanged(it)) },
                        readOnly = state.isPastDate,
                        largeFontEnabled = largeFontEnabled,
                        selectedFont = selectedFont
                    )
                }
            }

            // Extra custom prompts (index 3+) stored in state.additionalAnswers
            val extraPrompts = journalPrompts.drop(3)
            extraPrompts.forEachIndexed { extraIndex, prompt ->
                item {
                    JournalPromptField(
                        prompt = prompt,
                        value = state.additionalAnswers.getOrElse(extraIndex) { "" },
                        onValueChange = { onEvent(JournalEvent.OnAdditionalAnswerChanged(extraIndex, it)) },
                        readOnly = state.isPastDate,
                        largeFontEnabled = largeFontEnabled,
                        selectedFont = selectedFont
                    )
                }
            }

            // ── Tomorrow's Tasks (only shown for today's entry) ───────────────
            if (!state.isPastDate) {
                item {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        thickness = 1.dp
                    )
                }

                item {
                    Text(
                        text = "Tomorrow's Tasks",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                items(state.newTasks) { taskTitle ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = taskTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { onEvent(JournalEvent.OnRemoveNewTask(taskTitle)) }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Remove task",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }

                item {
                    var newTaskTitle by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = "Add a task for tomorrow...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            IconButton(onClick = {
                                if (newTaskTitle.isNotBlank()) {
                                    onEvent(JournalEvent.OnAddNewTask(newTaskTitle))
                                    newTaskTitle = ""
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add task",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.secondary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                }

                // ── Save button ───────────────────────────────────────────────
                item {
                    ElevatedButton(
                        onClick = { onEvent(JournalEvent.OnSaveEntry) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (state.isSaved) "Entry Saved ✓" else "Save Entry",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            } else {
                // Bottom breathing room for past entries
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}
