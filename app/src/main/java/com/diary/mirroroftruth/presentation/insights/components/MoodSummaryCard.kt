package com.diary.mirroroftruth.presentation.insights.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Warm-to-cool palette for each tag slot
private val TAG_COLORS = listOf(
    Color(0xFFE07A5F), // Terra cotta – Joyful
    Color(0xFF3D405B), // Midnight navy – Focused
    Color(0xFF81B29A), // Sage – Calm
    Color(0xFFF2CC8F), // Warm sand – Grateful
    Color(0xFF8ECAE6), // Sky – Productive
    Color(0xFFFFB4A2), // Blush – Tired
    Color(0xFFB5838D), // Mauve – Stressed
    Color(0xFF6B4226), // Espresso – Overwhelmed
    Color(0xFFCDB4DB), // Lavender – Anxious
    Color(0xFF95D5B2), // Mint – Content
    Color(0xFFFF9A3C), // Amber – Frustrated
)

/**
 * Shows word-tag frequency as a sorted bar chart with coloured pill labels.
 * If no data yet, shows a gentle empty state.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodSummaryCard(
    moodCounts: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "This Month's Vibes",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (moodCounts.isEmpty()) {
                Text(
                    text = "No vibes logged yet — start journaling! ✨",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val sorted = moodCounts.entries.sortedByDescending { it.value }
                val maxCount = sorted.first().value.coerceAtLeast(1)

                sorted.forEachIndexed { index, (tag, count) ->
                    val tagColor = TAG_COLORS.getOrElse(index) { MaterialTheme.colorScheme.primary }
                    val fraction = count.toFloat() / maxCount

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Pill label
                        Box(
                            modifier = Modifier
                                .width(96.dp)
                                .background(tagColor.copy(alpha = 0.18f), RoundedCornerShape(50))
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = tagColor,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }

                        // Bar
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape(50)
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(fraction)
                                    .background(tagColor, RoundedCornerShape(50))
                            )
                        }

                        // Count badge
                        Text(
                            text = "×$count",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(28.dp)
                        )
                    }
                }
            }
        }
    }
}
