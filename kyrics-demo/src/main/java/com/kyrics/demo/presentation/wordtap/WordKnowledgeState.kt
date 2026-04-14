package com.kyrics.demo.presentation.wordtap

import androidx.compose.runtime.Immutable
import com.kyrics.demo.data.model.WordDefinition
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

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
