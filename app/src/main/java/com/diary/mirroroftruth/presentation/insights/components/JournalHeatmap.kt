package com.diary.mirroroftruth.presentation.insights.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A Bezier "Vibe" curve that shows journal consistency throughout the month.
 * Dots = days with entries. Curve smoothly connects them.
 * Also shows the raw calendar heatmap below for reference.
 */
@Composable
fun JournalHeatmap(
    entriesByDay: Map<Int, Boolean>,
    modifier: Modifier = Modifier
) {
    val today = Calendar.getInstance()
    val monthFormatter = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val monthLabel = monthFormatter.format(today.time)
    val lastDay = today.getActualMaximum(Calendar.DAY_OF_MONTH)
    val todayDay = today.get(Calendar.DAY_OF_MONTH)

    // Determine starting weekday offset (Mon=0 .. Sun=6) for day 1
    val firstDayCalendar = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
    val startOffset = ((firstDayCalendar.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7)

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = monthLabel,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // ── Bezier Vibe Curve ─────────────────────────────────────────────────
        Text(
            text = "Your Writing Vibe",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val daysToShow = minOf(todayDay, lastDay)

            if (daysToShow < 2) return@Canvas

            // X positions: spread days across canvas width
            val xStep = canvasWidth / (daysToShow - 1).coerceAtLeast(1).toFloat()

            // Y: entry = top 30%, no entry = bottom 70%
            val yHigh = canvasHeight * 0.2f
            val yLow = canvasHeight * 0.8f

            // Build control points
            val points = (1..daysToShow).map { day ->
                val x = (day - 1) * xStep
                val y = if (entriesByDay[day] == true) yHigh else yLow
                Offset(x, y)
            }

            // Draw filled area under the curve
            val fillPath = buildBezierPath(points)
            fillPath.lineTo(points.last().x, canvasHeight)
            fillPath.lineTo(points.first().x, canvasHeight)
            fillPath.close()
            drawPath(path = fillPath, color = primaryColor.copy(alpha = 0.12f))

            // Draw the smooth Bezier curve
            val curvePath = buildBezierPath(points)
            drawPath(
                path = curvePath,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            )

            // Draw dots at each data point
            points.forEachIndexed { index, point ->
                val day = index + 1
                val hasEntry = entriesByDay[day] == true
                drawCircle(
                    color = if (hasEntry) primaryColor else onSurface.copy(alpha = 0.3f),
                    radius = if (hasEntry) 5.dp.toPx() else 3.dp.toPx(),
                    center = point
                )
                if (hasEntry) {
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = point
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Mini Calendar Heatmap ─────────────────────────────────────────────
        Text(
            text = "Entry Calendar",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        val weekdayLabels = listOf("M", "T", "W", "T", "F", "S", "S")
        Row(modifier = Modifier.fillMaxWidth()) {
            weekdayLabels.forEach { label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        val totalCells = startOffset + lastDay
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp)
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val day = cellIndex - startOffset + 1
                    val isValidDay = day in 1..lastDay
                    val hasEntry = entriesByDay[day] == true
                    val isToday = day == todayDay

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(1.5.dp)
                            .aspectRatio(1f)
                            .clip(if (isToday) CircleShape else RoundedCornerShape(3.dp))
                            .background(
                                when {
                                    !isValidDay -> Color.Transparent
                                    hasEntry -> primaryColor
                                    isToday -> primaryColor.copy(alpha = 0.25f)
                                    else -> surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isValidDay) {
                            Text(
                                text = day.toString(),
                                fontSize = 7.sp,
                                color = if (hasEntry) Color.White
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Builds a smooth cubic Bezier Path through the given list of points. */
private fun buildBezierPath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path
    path.moveTo(points.first().x, points.first().y)

    for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]
        val controlX = (p0.x + p1.x) / 2f
        // Cubic with symmetric control points gives a natural S-curve
        path.cubicTo(controlX, p0.y, controlX, p1.y, p1.x, p1.y)
    }
    return path
}
