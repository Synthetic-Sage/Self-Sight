package com.diary.mirroroftruth.presentation.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private val WEEKDAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

@Composable
fun JournalHeatmap(
    entriesByDay: Map<Int, Boolean>,
    modifier: Modifier = Modifier
) {
    val today = Calendar.getInstance()
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthLabel = monthFormatter.format(today.time)
    val lastDay = today.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Determine starting weekday offset (Mon=0 .. Sun=6) for day 1
    val firstDayCalendar = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
    }
    val startOffset = ((firstDayCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7)

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Weekday header row
        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAY_LABELS.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Build a flat list of cells: offset blanks + days
        val totalCells = startOffset + lastDay
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1
                    val isValidDay = day in 1..lastDay

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when {
                                    !isValidDay -> MaterialTheme.colorScheme.background
                                    entriesByDay[day] == true -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isValidDay) {
                            Text(
                                text = day.toString(),
                                fontSize = 8.sp,
                                color = if (entriesByDay[day] == true)
                                    MaterialTheme.colorScheme.onPrimary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
