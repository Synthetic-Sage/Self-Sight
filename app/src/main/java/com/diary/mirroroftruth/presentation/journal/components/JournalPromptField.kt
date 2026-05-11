package com.diary.mirroroftruth.presentation.journal.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.diary.mirroroftruth.core.theme.NotebookLine

/**
 * A lined-notebook style text field.
 *
 * @param readOnly  When true (viewing a past entry), the field is non-editable
 *                  and slightly faded — the "ink drying" effect.
 */
@Composable
fun JournalPromptField(
    prompt: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    largeFontEnabled: Boolean = false
) {
    val baseStyle = if (largeFontEnabled) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge
    val textStyle = baseStyle.copy(color = MaterialTheme.colorScheme.onBackground)
    val density = LocalDensity.current
    val lineHeightPx = with(density) { textStyle.lineHeight.toPx() }
    val paddingTop = 4.dp

    // Ink-dry alpha: past entries are visually faded
    val contentAlpha = if (readOnly) 0.55f else 1f

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = prompt,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = contentAlpha),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val width = size.width
                    val height = size.height
                    val paddingTopPx = with(density) { paddingTop.toPx() }
                    var y = paddingTopPx + lineHeightPx
                    while (y < height) {
                        drawLine(
                            color = NotebookLine,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 2f
                        )
                        y += lineHeightPx
                    }
                }
                .padding(vertical = paddingTop)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = textStyle.copy(
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = contentAlpha)
                ),
                // Allow unlimited lines — the parent LazyColumn handles scrolling
                modifier = Modifier
                    .fillMaxWidth()
                    // Minimum 3 lines so an empty field still looks like a diary page section
                    .defaultMinSize(minHeight = (textStyle.lineHeight.value * 3).dp),
                readOnly = readOnly,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    if (value.isEmpty() && !readOnly) {
                        Text(
                            text = "Dear diary...",
                            style = textStyle,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
