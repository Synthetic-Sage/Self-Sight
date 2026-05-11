package com.diary.mirroroftruth.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DiaryColorScheme = lightColorScheme(
    primary = AccentRust,
    secondary = AccentTeal,
    background = ParchmentBase,
    surface = ParchmentDark,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = InkBlack,
    onSurface = InkBlack,
    error = ErrorRed,
    onError = Color.White,
    surfaceVariant = ParchmentBase,
    onSurfaceVariant = InkFaded
)

@Composable
fun MirrorOfTruthTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DiaryColorScheme,
        typography = Typography,
        content = content
    )
}