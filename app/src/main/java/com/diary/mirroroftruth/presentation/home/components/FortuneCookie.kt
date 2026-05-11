package com.diary.mirroroftruth.presentation.home.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.clipRect

enum class CookieState {
    Idle,
    Cracking,
    Unrolling,
    Revealed
}

@Composable
fun FortuneCookie(quote: String, modifier: Modifier = Modifier) {
    var cookieState by remember { mutableStateOf(CookieState.Idle) }
    var shakeRotation by remember { mutableStateOf(0f) }

    // Shake animation
    val shake by animateFloatAsState(
        targetValue = shakeRotation,
        animationSpec = spring(dampingRatio = Spring.DampingRatioHighBouncy, stiffness = Spring.StiffnessHigh),
        label = "shake"
    )

    LaunchedEffect(cookieState) {
        when (cookieState) {
            CookieState.Cracking -> {
                shakeRotation = 15f
                delay(100)
                shakeRotation = -15f
                delay(100)
                shakeRotation = 15f
                delay(100)
                shakeRotation = 0f
                delay(200)
                cookieState = CookieState.Unrolling
            }
            CookieState.Unrolling -> {
                delay(500)
                cookieState = CookieState.Revealed
            }
            else -> {}
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 140.dp)
            .clickable(
                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                indication = null
            ) {
                if (cookieState == CookieState.Idle) {
                    cookieState = CookieState.Cracking
                }
            },
        contentAlignment = Alignment.Center
    ) {
        // The Paper
        AnimatedVisibility(
            visible = cookieState == CookieState.Unrolling || cookieState == CookieState.Revealed,
            enter = expandVertically(animationSpec = tween(500, easing = LinearOutSlowInEasing)) +
                    fadeIn(animationSpec = tween(500)),
            exit = fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(2.dp), // Slightly sharp corners for a paper look
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCF9EE)), // Crisp pale off-white/yellow
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp) // Subtle drop shadow
            ) {
                val textAlpha by animateFloatAsState(
                    targetValue = if (cookieState == CookieState.Revealed) 1f else 0f,
                    animationSpec = tween(durationMillis = 800),
                    label = "text_alpha"
                )
                
                Text(
                    text = quote,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontStyle = FontStyle.Italic,
                        color = Color.DarkGray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            drawContent()
                            // Faint wrinkles for crumpled paper effect
                            drawLine(
                                color = Color.Black.copy(alpha = 0.05f),
                                start = Offset(0f, size.height * 0.3f),
                                end = Offset(size.width, size.height * 0.4f),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Black.copy(alpha = 0.05f),
                                start = Offset(size.width * 0.2f, 0f),
                                end = Offset(size.width * 0.3f, size.height),
                                strokeWidth = 2f
                            )
                            drawLine(
                                color = Color.Black.copy(alpha = 0.03f),
                                start = Offset(0f, size.height * 0.7f),
                                end = Offset(size.width, size.height * 0.6f),
                                strokeWidth = 3f
                            )
                        }
                        .padding(16.dp)
                        .alpha(textAlpha),
                    textAlign = TextAlign.Center
                )
                if (textAlpha > 0.8f) {
                    Text(
                        text = "Come back tomorrow for a new quote",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth().alpha(textAlpha),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // The Cookie Halves
        val splitOffset by animateDpAsState(
            targetValue = if (cookieState >= CookieState.Unrolling) 80.dp else 0.dp,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
        val splitAngle by animateFloatAsState(
            targetValue = if (cookieState >= CookieState.Unrolling) 45f else 0f,
            animationSpec = tween(500)
        )
        val cookieFade by animateFloatAsState(
            targetValue = if (cookieState == CookieState.Revealed) 0f else 1f,
            animationSpec = tween(400, delayMillis = 200)
        )

        if (cookieFade > 0f) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.alpha(cookieFade)) {
                // Left half
                Text(
                    text = "🥠",
                    fontSize = 64.sp,
                    modifier = Modifier
                        .offset(x = -splitOffset)
                        .rotate(shake - splitAngle)
                        .drawWithContent {
                            clipRect(right = size.width / 2.3f) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
                // Right half
                Text(
                    text = "🥠",
                    fontSize = 64.sp,
                    modifier = Modifier
                        .offset(x = splitOffset)
                        .rotate(shake + splitAngle)
                        .drawWithContent {
                            clipRect(left = size.width / 2.3f) {
                                this@drawWithContent.drawContent()
                            }
                        }
                )
                
                if (cookieState == CookieState.Idle) {
                    Text(
                        text = "Tap to crack",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.offset(y = 48.dp)
                    )
                }
            }
        }
    }
}
