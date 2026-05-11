package com.diary.mirroroftruth

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class CrashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val stackTrace = intent.getStringExtra("EXTRA_STACK_TRACE") ?: "No stack trace"

        setContent {
            MaterialTheme {
                Scaffold { padding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        item {
                            Text(text = "App Crashed!", style = MaterialTheme.typography.titleLarge)
                            Text(text = stackTrace, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}
