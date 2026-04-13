package com.kyrics.demo.presentation.dualsync

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.dualsync.DualSyncLyricsView
import com.kyrics.dualsync.model.DualSyncState
import com.kyrics.state.KyricsUiState

/**
 * Demo screen showcasing dual-language synchronized lyrics highlighting.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DualSyncDemoScreen(
    state: DualSyncUiState,
    onIntent: (DualSyncIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("DualSync Demo") },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                    ),
            )
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Lyrics display area
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFF121212)),
            ) {
                val displayState = buildDisplayState(state)
                DualSyncLyricsView(
                    state = displayState,
                    modifier = Modifier.fillMaxSize(),
                    secondaryStyle =
                        TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.7f),
                        ),
                    showSecondary = state.languageMode != LanguageMode.EN_ONLY,
                )
            }

            // Controls
            ControlBar(
                state = state,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun buildDisplayState(state: DualSyncUiState): DualSyncState =
    when (state.languageMode) {
        LanguageMode.DUAL -> state.syncState
        LanguageMode.EN_ONLY ->
            state.syncState.copy(
                secondaryHighlight = KyricsUiState(),
            )
        LanguageMode.ZH_ONLY ->
            DualSyncState(
                primaryHighlight = state.syncState.secondaryHighlight,
                secondaryHighlight = KyricsUiState(),
            )
    }

@Composable
private fun ControlBar(
    state: DualSyncUiState,
    onIntent: (DualSyncIntent) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Playback controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedButton(onClick = { onIntent(DualSyncIntent.Reset) }) {
                    Text("Reset")
                }

                Button(onClick = { onIntent(DualSyncIntent.TogglePlayPause) }) {
                    Text(if (state.isPlaying) "Pause" else "Play")
                }

                Text(
                    text = formatTime(state.currentTimeMs),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }

            // Language toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LanguageMode.entries.forEach { mode ->
                    FilterChip(
                        selected = state.languageMode == mode,
                        onClick = { onIntent(DualSyncIntent.SetLanguageMode(mode)) },
                        label = { Text(mode.label()) },
                        colors =
                            FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                        shape = RoundedCornerShape(20.dp),
                    )
                }
            }
        }
    }
}

private fun LanguageMode.label(): String =
    when (this) {
        LanguageMode.DUAL -> "Dual"
        LanguageMode.EN_ONLY -> "EN"
        LanguageMode.ZH_ONLY -> "ZH"
    }

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
