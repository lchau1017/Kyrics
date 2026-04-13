package com.kyrics.demo.presentation.dualsync

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kyrics.demo.data.datasource.DemoLanguage
import com.kyrics.dualsync.DualSyncLyricsView

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
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFF121212)),
            ) {
                DualSyncLyricsView(
                    state = state.syncState,
                    modifier = Modifier.fillMaxSize(),
                    showSecondary = state.showSecondary,
                )
            }

            ControlBar(state = state, onIntent = onIntent)
        }
    }
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
                    .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PlaybackRow(state = state, onIntent = onIntent)

            LanguageRow(
                label = "Primary",
                selected = state.primaryLanguage,
                onSelect = { onIntent(DualSyncIntent.SetPrimaryLanguage(it)) },
                disabledLanguages = setOf(state.secondaryLanguage),
            )

            SecondaryRow(state = state, onIntent = onIntent)
        }
    }
}

@Composable
private fun PlaybackRow(
    state: DualSyncUiState,
    onIntent: (DualSyncIntent) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedButton(onClick = { onIntent(DualSyncIntent.Reset) }) {
            Text("Reset")
        }
        Button(onClick = { onIntent(DualSyncIntent.TogglePlayPause) }) {
            Text(if (state.isPlaying) "Pause" else "Play")
        }
        OutlinedButton(onClick = { onIntent(DualSyncIntent.SwapLanguages) }) {
            Text("\u2B83 Swap")
        }
        Text(
            text = formatTime(state.currentTimeMs),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
}

@Composable
private fun SecondaryRow(
    state: DualSyncUiState,
    onIntent: (DualSyncIntent) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Switch(
            checked = state.showSecondary,
            onCheckedChange = { onIntent(DualSyncIntent.ToggleSecondary) },
            modifier = Modifier.padding(top = 12.dp),
        )
        LanguageRow(
            label = "Secondary",
            selected = state.secondaryLanguage,
            onSelect = { onIntent(DualSyncIntent.SetSecondaryLanguage(it)) },
            modifier = Modifier.weight(1f),
            enabled = state.showSecondary,
            disabledLanguages = setOf(state.primaryLanguage),
        )
    }
}

@Composable
@Suppress("LongParameterList")
private fun LanguageRow(
    label: String,
    selected: DemoLanguage,
    onSelect: (DemoLanguage) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    disabledLanguages: Set<DemoLanguage> = emptySet(),
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color =
                MaterialTheme.colorScheme.onSurface.copy(
                    alpha = if (enabled) 0.7f else 0.3f,
                ),
        )
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            DemoLanguage.entries.forEach { lang ->
                val isDisabled = lang in disabledLanguages
                FilterChip(
                    selected = selected == lang,
                    onClick = { onSelect(lang) },
                    enabled = enabled && !isDisabled,
                    label = { Text(lang.displayName) },
                    colors =
                        FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
