package com.kyrics.demo.presentation.wordtap

import androidx.compose.runtime.Immutable
import com.kyrics.models.KyricsLine

/**
 * A word from the lyrics that has a dictionary definition and can be tapped.
 */
@Immutable
data class WordListItem(
    val word: String,
    val lineIndex: Int,
    val lineContent: String,
    val lineStartMs: Int,
    val timestampMs: Int,
)

/**
 * UI state for the Word Tap demo screen.
 */
@Immutable
data class WordTapUiState(
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val lines: List<KyricsLine> = emptyList(),
    val clickableWords: List<WordListItem> = emptyList(),
    val selectedWord: String? = null,
    val selectedLineIndex: Int? = null,
    val wordKnowledge: WordKnowledgeState? = null,
)
