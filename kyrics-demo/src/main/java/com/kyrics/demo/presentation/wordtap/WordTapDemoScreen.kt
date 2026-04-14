package com.kyrics.demo.presentation.wordtap

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kyrics.demo.presentation.shared.AccentGreen
import com.kyrics.demo.presentation.shared.DemoBackgroundColor
import com.kyrics.demo.presentation.shared.formatTime

/**
 * Demo screen showcasing word-tap knowledge feature.
 * Single-language English lyrics with specific vocabulary words highlighted
 * in green. Tap a highlighted word to see its definition.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordTapDemoScreen(
    state: WordTapUiState,
    onIntent: (WordTapIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val clickableWordSet = state.clickableWords.map { it.word.lowercase() }.toSet()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("Word Tap Demo") },
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
            // Playback controls (top)
            PlaybackControls(state = state, onIntent = onIntent)

            // Vocabulary chips — tap to seek + show definition
            WordListBar(
                words = state.clickableWords,
                selectedWord = state.selectedWord,
                onWordClick = { onIntent(WordTapIntent.SeekToWord(it)) },
            )

            // Lyrics area
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(DemoBackgroundColor),
            ) {
                WordTapLyricsView(
                    lines = state.lines,
                    currentTimeMs = state.currentTimeMs,
                    clickableWords = clickableWordSet,
                    selectedWord = state.selectedWord,
                    onWordClick = { syllable, line ->
                        onIntent(WordTapIntent.WordTapped(syllable, line))
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                Text(
                    text = "Tap a green word for its definition",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier =
                        Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp),
                )
            }
        }
    }

    state.wordKnowledge?.let { knowledge ->
        WordKnowledgeSheet(
            state = knowledge,
            onDismiss = { onIntent(WordTapIntent.DismissWordSheet) },
        )
    }
}

@Composable
private fun WordListBar(
    words: List<WordListItem>,
    selectedWord: String?,
    onWordClick: (WordListItem) -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
        ) {
            Text(
                text = "Vocabulary \u2014 tap to seek",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding =
                    androidx.compose.foundation.layout
                        .PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            ) {
                items(words) { word ->
                    val isSelected = selectedWord == word.word.lowercase()
                    Surface(
                        color =
                            if (isSelected) {
                                AccentGreen
                            } else {
                                AccentGreen.copy(alpha = 0.15f)
                            },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.clickable { onWordClick(word) },
                    ) {
                        Text(
                            text = word.word,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White else AccentGreen,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PlaybackControls(
    state: WordTapUiState,
    onIntent: (WordTapIntent) -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedButton(onClick = { onIntent(WordTapIntent.Reset) }) {
                Text("Reset")
            }
            Button(onClick = { onIntent(WordTapIntent.TogglePlayPause) }) {
                Text(if (state.isPlaying) "Pause" else "Play")
            }
            Text(
                text = formatTime(state.currentTimeMs),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            )
        }
    }
}
