package com.kyrics.demo.presentation.shared

import androidx.compose.ui.graphics.Color

val DemoBackgroundColor = Color(0xFF121212)
val AccentGreen = Color(0xFF1DB954)

fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
