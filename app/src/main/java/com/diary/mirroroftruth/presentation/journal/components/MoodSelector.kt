package com.diary.mirroroftruth.presentation.journal.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodSelector(
    selectedTags: List<String>,
    onTagToggled: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tags = listOf("Joyful", "Focused", "Tired", "Anxious", "Calm", "Productive", "Stressed", "Grateful", "Overwhelmed", "Frustrated", "Content")
    
    Column(modifier = modifier) {
        Text(
            text = "What's the vibe today?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tags.forEach { tag ->
                FilterChip(
                    selected = selectedTags.contains(tag),
                    onClick = { onTagToggled(tag) },
                    label = {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        }
    }
}
