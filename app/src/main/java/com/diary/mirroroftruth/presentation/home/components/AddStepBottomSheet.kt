package com.diary.mirroroftruth.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.diary.mirroroftruth.domain.model.StepType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStepBottomSheet(
    onDismiss: () -> Unit,
    onAddStep: (title: String, type: StepType, target: Float, description: String?) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(StepType.BIG) }
    var targetText by remember { mutableStateOf("1") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Step",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Type", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = type == StepType.BIG,
                            onClick = { type = StepType.BIG },
                            label = { Text("Big Step") }
                        )
                        FilterChip(
                            selected = type == StepType.SMALL,
                            onClick = { type = StepType.SMALL },
                            label = { Text("Small Step") }
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text("Target", style = MaterialTheme.typography.labelMedium)
                    OutlinedTextField(
                        value = targetText,
                        onValueChange = { targetText = it.filter { char -> char.isDigit() } },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Button(
                onClick = {
                    val target = targetText.toFloatOrNull() ?: 1f
                    onAddStep(title, type, target, description.takeIf { it.isNotBlank() })
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = title.isNotBlank() && targetText.isNotBlank(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Step")
            }
        }
    }
}
