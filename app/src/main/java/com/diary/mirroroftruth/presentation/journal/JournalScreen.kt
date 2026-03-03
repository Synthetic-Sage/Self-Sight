package com.diary.mirroroftruth.presentation.journal

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.diary.mirroroftruth.presentation.journal.components.JournalPromptField
import com.diary.mirroroftruth.presentation.journal.components.MoodSelector
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    state: JournalState,
    onEvent: (JournalEvent) -> Unit
) {
    val dateFormatter = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val todayString = dateFormatter.format(Date(state.currentDate))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Today's Reflection",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = todayString,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
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
            // Mood Selector
            item {
                MoodSelector(
                    selectedMood = state.selectedMood,
                    onMoodSelected = { onEvent(JournalEvent.OnMoodSelected(it)) }
                )
            }

            // Reflection prompts
            item {
                JournalPromptField(
                    prompt = "What went well today?",
                    value = state.wentWell,
                    onValueChange = { onEvent(JournalEvent.OnWentWellChanged(it)) }
                )
            }

            item {
                JournalPromptField(
                    prompt = "What challenged you?",
                    value = state.challenges,
                    onValueChange = { onEvent(JournalEvent.OnChallengesChanged(it)) }
                )
            }

            item {
                JournalPromptField(
                    prompt = "What are you grateful for?",
                    value = state.gratitude,
                    onValueChange = { onEvent(JournalEvent.OnGratitudeChanged(it)) }
                )
            }

            // Tomorrow's intentions
            item {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    thickness = 1.dp
                )
            }

            item {
                Text(
                    text = "Tomorrow's Intentions",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            item {
                OutlinedTextField(
                    value = state.tomorrowsTask,
                    onValueChange = { onEvent(JournalEvent.OnTomorrowsTaskChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "What do you want to accomplish tomorrow?",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    },
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.secondary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }

            // Save button
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
                        text = if (state.isSaved) "Entry Saved!" else "Save Entry",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
