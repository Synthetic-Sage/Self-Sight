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

    // Fade out cookie when unrolling begins
    val cookieAlpha by animateFloatAsState(
        targetValue = if (cookieState == CookieState.Idle || cookieState == CookieState.Cracking) 1f else 0f,
        animationSpec = tween(300),
        label = "cookie_alpha"
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
                        .padding(16.dp)
                        .alpha(textAlpha),
                    textAlign = TextAlign.Center
                )
            }
        }

        // The Cookie
        if (cookieAlpha > 0f) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.alpha(cookieAlpha)) {
                Text(
                    text = "🥠",
                    fontSize = 64.sp,
                    modifier = Modifier.rotate(shake)
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
