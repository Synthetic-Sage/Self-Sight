package com.diary.mirroroftruth.presentation.journal.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * Decorative background for the Journal page.
 * Draws playful doodles (triangles, circles, squiggles, trophy, coffee cup, star)
 * ONLY in the safe margin zone — corners and edges — never overlapping text input areas.
 *
 * Sticker positions are seeded by [dateSeed] so the same day always shows the same doodles.
 */
@Composable
fun JournalPageBackground(
    dateSeed: Long,
    modifier: Modifier = Modifier
) {
    // Use a fixed set of muted, theme-compatible colours
    val colors = listOf(
        Color(0xFFF4845F).copy(alpha = 0.18f), // warm orange
        Color(0xFF7EC8E3).copy(alpha = 0.18f), // sky blue
        Color(0xFF9EBC8A).copy(alpha = 0.18f), // sage green
        Color(0xFFD4A5C9).copy(alpha = 0.18f), // soft purple
        Color(0xFFE9C46A).copy(alpha = 0.18f), // golden yellow
        Color(0xFFE76F51).copy(alpha = 0.15f), // terracotta
    )

    // Pre-generate placement data from the date seed — stable per day
    val rng = remember(dateSeed) { Random(dateSeed) }

    // Doodle types available
    val doodleTypes = listOf("triangle", "circle", "squiggle", "star", "trophy_cup", "coffee")

    // Pick 5 doodles with stable positions for this date
    val doodles = remember(dateSeed) {
        (0 until 5).map {
            DoodleSpec(
                type = doodleTypes[rng.nextInt(doodleTypes.size)],
                color = colors[rng.nextInt(colors.size)],
                // position expressed as fractions of width/height
                xFraction = rng.nextFloat(),
                yFraction = rng.nextFloat(),
                sizeFraction = 0.06f + rng.nextFloat() * 0.05f, // 6-11% of width
                rotation = rng.nextFloat() * 360f
            )
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Safe content zone: text fields occupy roughly x: 8%-92%, y: 10%-88%
        // Stickers are placed in the MARGIN — we shift positions that land in the safe zone
        // toward the nearest edge.
        doodles.forEach { spec ->
            val rawX = spec.xFraction * w
            val rawY = spec.yFraction * h
            val doodleSize = spec.sizeFraction * w

            // Clamp to margin bands: top 9%, bottom 9%, left 7%, right 7%
            val x = clampToMargin(rawX, w, doodleSize, marginFraction = 0.09f)
            val y = clampToMargin(rawY, h, doodleSize, marginFraction = 0.09f)

            drawDoodle(
                type = spec.type,
                color = spec.color,
                center = Offset(x, y),
                size = doodleSize,
                rotation = spec.rotation
            )
        }
    }
}

/** Returns a coordinate snapped to the nearest margin band, keeping it out of the text area */
private fun clampToMargin(value: Float, total: Float, size: Float, marginFraction: Float): Float {
    val marginPx = total * marginFraction + size
    return when {
        value < total / 2f -> value.coerceAtMost(marginPx)          // left/top margin
        else               -> value.coerceAtLeast(total - marginPx) // right/bottom margin
    }
}

private data class DoodleSpec(
    val type: String,
    val color: Color,
    val xFraction: Float,
    val yFraction: Float,
    val sizeFraction: Float,
    val rotation: Float
)

private fun DrawScope.drawDoodle(
    type: String,
    color: Color,
    center: Offset,
    size: Float,
    rotation: Float
) {
    val stroke = Stroke(width = size * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)

    when (type) {
        "triangle" -> drawTriangle(color, center, size, rotation, stroke)
        "circle"   -> drawCircle(color = color, radius = size * 0.5f, center = center, style = stroke)
        "squiggle" -> drawSquiggle(color, center, size, stroke)
        "star"     -> drawStar(color, center, size, rotation, stroke)
        "trophy_cup" -> drawTrophy(color, center, size, stroke)
        "coffee"   -> drawCoffeeCup(color, center, size, stroke)
    }
}

private fun DrawScope.drawTriangle(color: Color, center: Offset, size: Float, rotation: Float, stroke: Stroke) {
    val path = Path()
    val r = size * 0.55f
    val angles = listOf(90f, 210f, 330f).map { (it + rotation) * Math.PI.toFloat() / 180f }
    path.moveTo(center.x + r * cos(angles[0]), center.y - r * sin(angles[0]))
    path.lineTo(center.x + r * cos(angles[1]), center.y - r * sin(angles[1]))
    path.lineTo(center.x + r * cos(angles[2]), center.y - r * sin(angles[2]))
    path.close()
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawSquiggle(color: Color, center: Offset, size: Float, stroke: Stroke) {
    val path = Path()
    val w = size * 1.2f
    path.moveTo(center.x - w / 2, center.y)
    path.cubicTo(
        center.x - w / 4, center.y - size * 0.4f,
        center.x + w / 4, center.y + size * 0.4f,
        center.x + w / 2, center.y
    )
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawStar(color: Color, center: Offset, size: Float, rotation: Float, stroke: Stroke) {
    val path = Path()
    val outerR = size * 0.5f
    val innerR = size * 0.22f
    val points = 5
    for (i in 0 until points * 2) {
        val angle = (i * Math.PI / points + rotation * Math.PI / 180f).toFloat()
        val r = if (i % 2 == 0) outerR else innerR
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, color, style = stroke)
}

private fun DrawScope.drawTrophy(color: Color, center: Offset, size: Float, stroke: Stroke) {
    // Cup bowl
    val path = Path()
    val halfW = size * 0.38f
    val top = center.y - size * 0.4f
    val bottom = center.y + size * 0.1f
    path.moveTo(center.x - halfW, top)
    path.lineTo(center.x - halfW, bottom - size * 0.15f)
    path.cubicTo(
        center.x - halfW, bottom,
        center.x + halfW, bottom,
        center.x + halfW, bottom - size * 0.15f
    )
    path.lineTo(center.x + halfW, top)
    drawPath(path, color, style = stroke)
    // Stem
    drawLine(color, Offset(center.x, bottom), Offset(center.x, center.y + size * 0.38f), stroke.width)
    // Base
    drawLine(color, Offset(center.x - halfW * 0.8f, center.y + size * 0.38f), Offset(center.x + halfW * 0.8f, center.y + size * 0.38f), stroke.width)
    // Handles
    drawArc(color, startAngle = -30f, sweepAngle = 60f, useCenter = false,
        topLeft = Offset(center.x + halfW - stroke.width, top + size * 0.05f),
        size = androidx.compose.ui.geometry.Size(halfW * 0.5f, size * 0.25f), style = stroke)
    drawArc(color, startAngle = 150f, sweepAngle = 60f, useCenter = false,
        topLeft = Offset(center.x - halfW * 1.5f + stroke.width, top + size * 0.05f),
        size = androidx.compose.ui.geometry.Size(halfW * 0.5f, size * 0.25f), style = stroke)
}

private fun DrawScope.drawCoffeeCup(color: Color, center: Offset, size: Float, stroke: Stroke) {
    // Cup body (trapezoid)
    val path = Path()
    val topW = size * 0.45f
    val botW = size * 0.35f
    val cupTop = center.y - size * 0.25f
    val cupBot = center.y + size * 0.32f
    path.moveTo(center.x - topW, cupTop)
    path.lineTo(center.x + topW, cupTop)
    path.lineTo(center.x + botW, cupBot)
    path.lineTo(center.x - botW, cupBot)
    path.close()
    drawPath(path, color, style = stroke)
    // Handle
    drawArc(
        color = color,
        startAngle = -60f,
        sweepAngle = 120f,
        useCenter = false,
        topLeft = Offset(center.x + botW * 0.5f, center.y - size * 0.1f),
        size = androidx.compose.ui.geometry.Size(size * 0.28f, size * 0.28f),
        style = stroke
    )
    // Steam wiggles
    val steamStroke = Stroke(width = stroke.width * 0.6f, cap = StrokeCap.Round)
    val steamPath = Path()
    steamPath.moveTo(center.x - size * 0.1f, cupTop - size * 0.05f)
    steamPath.cubicTo(
        center.x - size * 0.2f, cupTop - size * 0.2f,
        center.x, cupTop - size * 0.3f,
        center.x - size * 0.1f, cupTop - size * 0.45f
    )
    drawPath(steamPath, color, style = steamStroke)
    val steamPath2 = Path()
    steamPath2.moveTo(center.x + size * 0.1f, cupTop - size * 0.05f)
    steamPath2.cubicTo(
        center.x + size * 0.2f, cupTop - size * 0.2f,
        center.x, cupTop - size * 0.3f,
        center.x + size * 0.1f, cupTop - size * 0.45f
    )
    drawPath(steamPath2, color, style = steamStroke)
}
