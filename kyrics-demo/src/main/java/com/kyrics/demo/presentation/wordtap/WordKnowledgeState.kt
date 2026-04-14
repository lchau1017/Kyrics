package com.kyrics.demo.presentation.wordtap

import androidx.compose.runtime.Immutable
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * Definition data for a single word.
 */
@Immutable
data class WordDefinition(
    val word: String,
    val phonetic: String? = null,
    val partOfSpeech: String? = null,
    val definition: String,
    val example: String? = null,
)

/**
 * State for the word knowledge bottom sheet.
 * Non-null means the sheet is visible.
 */
@Immutable
data class WordKnowledgeState(
    val syllable: KyricsSyllable,
    val line: KyricsLine,
    val definition: WordDefinition?,
)
