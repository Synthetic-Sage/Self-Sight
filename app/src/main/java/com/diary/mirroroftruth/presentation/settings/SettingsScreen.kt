package com.diary.mirroroftruth.presentation.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.IosShare
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.diary.mirroroftruth.presentation.notifications.ReminderManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onEvent: (SettingsEvent) -> Unit
) {
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let { onEvent(SettingsEvent.OnExportRequested(it)) }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onEvent(SettingsEvent.OnImportRequested(it)) }
    }

    if (state.showPasswordDialog) {
        var password by remember { mutableStateOf("") }
        val title = if (state.pendingAction == PendingAction.EXPORT) "Encrypt Backup" else "Decrypt Backup"
        val message = if (state.pendingAction == PendingAction.EXPORT) 
            "Create a password to encrypt your backup file. You will need this password to import it later." 
        else "Enter the password you used to encrypt this backup file."

        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.OnPasswordDismissed) },
            title = { Text(title) },
            text = {
                Column {
                    Text(message)
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        label = { Text("Password") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { onEvent(SettingsEvent.OnPasswordSubmitted(password)) }) {
                    Text(if (state.pendingAction == PendingAction.EXPORT) "Export" else "Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.OnPasswordDismissed) }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (state.showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(SettingsEvent.OnDismissDialog) },
            title = { Text("Clear All Data?") },
            text = { Text("This action cannot be undone. All journal entries and tasks will be permanently deleted.") },
            confirmButton = {
                TextButton(onClick = { onEvent(SettingsEvent.OnClearDataConfirmed) }) {
                    Text("Clear Everything", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(SettingsEvent.OnDismissDialog) }) { Text("Cancel") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── Appearance ────────────────────────────────────────────────────
            item { SectionHeader("Appearance") }
            item {
                InfoRow(
                    icon = Icons.Default.Palette,
                    label = "Theme",
                    value = "Diary Parchment (Light)"
                )
            }

            // ── Notifications ─────────────────────────────────────────────────
            item { SectionHeader("Notifications") }
            item {
                var showTimePicker by remember { mutableStateOf(false) }
                val context = LocalContext.current
                val prefs = context.getSharedPreferences(ReminderManager.PREFS_NAME, Context.MODE_PRIVATE)
                var reminderEnabled by remember { mutableStateOf(prefs.getBoolean(ReminderManager.KEY_REMINDER_ENABLED, false)) }
                var reminderHour by remember { mutableStateOf(prefs.getInt(ReminderManager.KEY_REMINDER_HOUR, 20)) }
                var reminderMinute by remember { mutableStateOf(prefs.getInt(ReminderManager.KEY_REMINDER_MINUTE, 0)) }
                
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if (isGranted) {
                        reminderEnabled = true
                        ReminderManager.saveReminderTime(context, reminderHour, reminderMinute, true)
                    }
                }

                SwitchRow(
                    icon = Icons.Default.Notifications,
                    label = "Daily Reminder",
                    sublabel = if (reminderEnabled) {
                        val amPm = if (reminderHour >= 12) "PM" else "AM"
                        val displayHour = if (reminderHour % 12 == 0) 12 else reminderHour % 12
                        "Scheduled at ${String.format("%02d:%02d %s", displayHour, reminderMinute, amPm)}"
                    } else "Remind me to write my diary",
                    checked = reminderEnabled,
                    onCheckedChange = {
                        if (!reminderEnabled) {
                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                reminderEnabled = true
                                ReminderManager.saveReminderTime(context, reminderHour, reminderMinute, true)
                            }
                        } else {
                            reminderEnabled = false
                            ReminderManager.saveReminderTime(context, reminderHour, reminderMinute, false)
                        }
                    }
                )

                if (reminderEnabled) {
                    ActionRow(
                        icon = Icons.Default.Schedule,
                        label = "Reminder Time",
                        sublabel = "Change what time you are reminded",
                        onClick = { showTimePicker = true }
                    )
                }

                if (showTimePicker) {
                    ReminderTimePickerDialog(
                        initialHour = reminderHour,
                        initialMinute = reminderMinute,
                        onDismiss = { showTimePicker = false },
                        onTimeSelected = { hour, minute ->
                            reminderHour = hour
                            reminderMinute = minute
                            ReminderManager.saveReminderTime(context, hour, minute, true)
                            showTimePicker = false
                        }
                    )
                }
            }

            // ── Journal Customisation ─────────────────────────────────────────
            item { SectionHeader("Journal Customisation") }

            item {
                SwitchRow(
                    icon = Icons.Default.FormatSize,
                    label = "Large Text",
                    sublabel = "Increases font size in reflection fields",
                    checked = state.largeFontEnabled,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleLargeFont) }
                )
            }
            item { SectionDivider() }

            item {
                SwitchRow(
                    icon = Icons.Default.Star,
                    label = "Show 'What went well' prompt",
                    sublabel = "Display the gratitude reflection section",
                    checked = state.showWentWellPrompt,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleWentWell) }
                )
            }
            item { SectionDivider() }

            item {
                SwitchRow(
                    icon = Icons.Default.Edit,
                    label = "Show 'Things to improve' prompt",
                    sublabel = "Display the growth reflection section",
                    checked = state.showToImprovePrompt,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleToImprove) }
                )
            }
            item { SectionDivider() }

            item {
                SwitchRow(
                    icon = Icons.Default.Article,
                    label = "Show Today's Learning prompt",
                    sublabel = "Display the knowledge reflection section",
                    checked = state.showLearningPrompt,
                    onCheckedChange = { onEvent(SettingsEvent.OnToggleLearning) }
                )
            }

            item {
                // Context note
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡  Hiding a prompt doesn't delete any saved data — it just keeps the journal view cleaner.",
                        style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ── Journal Prompts ───────────────────────────────────────────────
            item { SectionHeader("Journal Questions") }
            item {
                Text(
                    text = "Tap a question to edit it. You can delete or reorder them, and add new ones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            itemsIndexed(state.journalPrompts) { index, prompt ->
                var editText by remember(prompt) { mutableStateOf(prompt) }
                val isEditing = state.editingPromptIndex == index

                if (isEditing) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = editText,
                            onValueChange = { editText = it },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            trailingIcon = {
                                IconButton(onClick = { onEvent(SettingsEvent.OnEditPrompt(index, editText)) }) {
                                    Icon(Icons.Default.Check, contentDescription = "Save", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .clickable { onEvent(SettingsEvent.OnStartEditingPrompt(index)) }
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.DragHandle, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(20.dp))
                        Text(text = prompt, style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
                        IconButton(onClick = { onEvent(SettingsEvent.OnDeletePrompt(index)) },
                            modifier = Modifier.size(32.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                    HorizontalDivider(modifier = Modifier.padding(start = 48.dp), color = MaterialTheme.colorScheme.surfaceVariant)
                }
            }

            item {
                TextButton(
                    onClick = { onEvent(SettingsEvent.OnAddPrompt) },
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Add a question")
                }
            }

            // ── Data ──────────────────────────────────────────────────────────
            item { SectionHeader("Data") }
            item {
                ActionRow(
                    icon = Icons.Default.IosShare,
                    label = "Export Journal Data",
                    sublabel = state.exportStatus.ifEmpty { "Save all entries securely to your device" },
                    onClick = { exportLauncher.launch("mirror_backup.json") }
                )
            }
            item { SectionDivider() }
            item {
                ActionRow(
                    icon = Icons.Default.Article,
                    label = "Import Backup",
                    sublabel = state.importStatus.ifEmpty { "Restore entries from an encrypted JSON backup" },
                    onClick = { importLauncher.launch(arrayOf("application/json", "*/*")) }
                )
            }
            item { SectionDivider() }
            item {
                ActionRow(
                    icon = Icons.Default.DeleteForever,
                    label = "Clear All Data",
                    sublabel = "Permanently delete all entries",
                    isDestructive = true,
                    onClick = { onEvent(SettingsEvent.OnClearDataRequested) }
                )
            }

            // ── FAQ ───────────────────────────────────────────────────────────
            item { SectionHeader("FAQ & Help") }
            item {
                ActionRow(
                    icon = Icons.Default.HelpOutline,
                    label = "Frequently Asked Questions",
                    sublabel = "Learn how to use Self Sight",
                    onClick = { onEvent(SettingsEvent.OnToggleFaq) }
                )
            }
            if (state.showFaq) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(16.dp)
                    ) {
                        // ── General ──
                        Text("General", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 8.dp))
                        
                        FaqItem("What is the purpose of this app?", "This app is a hybrid daily planner and reflection journal. It is designed to help you break down ambitious goals into manageable actions while providing a quiet space to reflect on your daily mindset, habits, and personal growth.")
                        FaqItem("Do I need an internet connection to use the app?", "No. The app is built entirely offline-first. Everything you write, track, and save is stored locally on your device, ensuring maximum privacy and speed.")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        
                        // ── Managing Tasks ──
                        Text("Managing Tasks & Goals", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 8.dp))
                        
                        FaqItem("What is the difference between Big Steps, Small Steps, and Daily Tasks?", "• Big Steps: Your long-term goals or major projects.\n• Small Steps: Actionable milestones for Big Steps.\n• Daily Tasks: Immediate, everyday actions you check off.")
                        FaqItem("What happens when I complete all my tasks for the day?", "Checking off your tasks contributes to your daily productivity, which you can reflect on in the Evening Journal. The app clears your daily task list the next day so you can start fresh.")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        
                        // ── Journaling ──
                        Text("Journaling & Insights", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 8.dp))
                        
                        FaqItem("Can I change the daily reflection questions?", "Yes. You can customize your experience by navigating to Settings. From there, you can toggle default prompts, edit existing questions, or add your own custom prompts.")
                        FaqItem("How does the \"Vibe\" tracker work?", "When you log your reflection, you can select a \"Vibe\" that represents your mood. The app tracks these and visualizes patterns in the Insights tab.")
                        FaqItem("How is my daily streak calculated?", "Your streak increases for every consecutive day you save a journal entry. If you miss a day, the streak resets, but your history remains safe.")
                        
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)
                        
                        // ── Privacy ──
                        Text("Privacy, Data & Backups", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary, modifier = Modifier.padding(bottom = 8.dp))
                        
                        FaqItem("Who can see my journal entries?", "Only you. Because the app does not use cloud servers, your data never leaves your phone. There is no external tracking.")
                        FaqItem("How do I back up my data if I get a new phone?", "Go to Settings > Export Journal Data to create a backup file. Transfer this file to your new phone and use the Import Backup option.")
                        FaqItem("Why does the backup require a password?", "To ensure your private entries remain secure if the backup file is accidentally shared, the app encrypts it using a password of your choice.")
                        FaqItem("I forgot my backup password. Can I reset it?", "No. For your privacy, there is no central server. If you lose the password, the file cannot be restored. Use a password you will easily remember.")
                    }
                }
            }

            // ── About ─────────────────────────────────────────────────────────
            item { SectionHeader("About") }
            item {
                InfoRow(icon = null, label = "Self Sight", value = "v1.0-beta")
            }
            item { SectionDivider() }
            item {
                InfoRow(icon = null, label = "Built with", value = "Jetpack Compose ❤️")
            }
            item { SectionDivider() }
            item {
                val context = LocalContext.current
                ActionRow(
                    icon = Icons.Default.Email,
                    label = "Send Feedback",
                    sublabel = "Report a bug or suggest a feature",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://github.com/Synthetic-Sage/Mirror-of-Truth/issues")
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

// ── Composable helpers ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReminderTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onTimeSelected(timePickerState.hour, timePickerState.minute) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 56.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    )
}

@Composable
private fun SwitchRow(
    icon: ImageVector,
    label: String,
    sublabel: String,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCheckedChange)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            Text(text = sublabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(
            checked = checked,
            onCheckedChange = { onCheckedChange() },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    label: String,
    sublabel: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    val tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = tint)
            Text(text = sublabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector?, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (icon != null) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        } else {
            Spacer(modifier = Modifier.size(24.dp))
        }
        Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    HorizontalDivider(modifier = Modifier.padding(start = 56.dp), color = MaterialTheme.colorScheme.surfaceVariant)
}

@Composable
private fun FaqItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = "Q: $question",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "A: $answer",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
