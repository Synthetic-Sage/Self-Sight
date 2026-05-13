package com.diary.mirroroftruth.presentation.lock

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.diary.mirroroftruth.presentation.settings.SettingsViewModel
import kotlinx.coroutines.launch

/**
 * Full-screen PIN entry lock screen.
 * Shown on app launch when PIN lock is enabled.
 *
 * Forgot PIN: user taps the fingerprint icon to authenticate via device biometrics.
 * This bypasses the PIN without wiping any data.
 */
@Composable
fun LockScreen(
    viewModel: SettingsViewModel,
    onUnlocked: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    // Shake/error colour animation on wrong PIN
    val dotColor by animateColorAsState(
        targetValue = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
        animationSpec = tween(200), label = "dotColor"
    )

    // Check if biometrics are available on this device
    val biometricAvailable = remember {
        BiometricManager.from(context).canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    // Auto-verify as soon as 4 digits are entered
    LaunchedEffect(pin) {
        if (pin.length == 4) {
            val correct = viewModel.verifyPin(pin)
            if (correct) {
                onUnlocked()
            } else {
                isError = true
                kotlinx.coroutines.delay(500)
                pin = ""
                isError = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // App name header
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Self Sight",
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Enter your PIN to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 4-dot PIN indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(4) { index ->
                    val filled = index < pin.length
                    val size by animateDpAsState(
                        targetValue = if (filled) 18.dp else 14.dp,
                        animationSpec = tween(150), label = "dotSize$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(size)
                            .clip(CircleShape)
                            .background(if (filled) dotColor else MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
            }

            if (isError) {
                Text(
                    text = "Incorrect PIN",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Number pad
            NumberPad(
                onDigit = { digit ->
                    if (pin.length < 4) pin += digit
                },
                onDelete = {
                    if (pin.isNotEmpty()) pin = pin.dropLast(1)
                }
            )

            // Biometric fallback — "Forgot PIN?"
            if (biometricAvailable) {
                TextButton(
                    onClick = {
                        showBiometricPrompt(context, onSuccess = onUnlocked)
                    }
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Forgot PIN? Use fingerprint")
                }
            }
        }
    }
}

/** 3×4 digit keypad */
@Composable
private fun NumberPad(
    onDigit: (String) -> Unit,
    onDelete: () -> Unit
) {
    val keys = listOf("1","2","3","4","5","6","7","8","9","","0","⌫")
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keys.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { key ->
                    if (key.isEmpty()) {
                        Spacer(Modifier.size(80.dp))
                    } else {
                        Button(
                            onClick = { if (key == "⌫") onDelete() else onDigit(key) },
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Launch the device biometric / screen-lock prompt for PIN recovery */
private fun showBiometricPrompt(context: Context, onSuccess: () -> Unit) {
    val activity = context as? FragmentActivity ?: return
    val executor = ContextCompat.getMainExecutor(context)
    val callback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            onSuccess()
        }
    }
    val prompt = BiometricPrompt(activity, executor, callback)
    val info = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Identity Verification")
        .setSubtitle("Use biometrics to unlock Self Sight")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()
    prompt.authenticate(info)
}
