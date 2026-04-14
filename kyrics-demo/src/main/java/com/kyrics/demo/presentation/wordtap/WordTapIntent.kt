package com.kyrics.demo.presentation.wordtap

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

/**
 * User intents for the Word Tap demo screen.
 */
sealed interface WordTapIntent {
    data object TogglePlayPause : WordTapIntent

    data object Reset : WordTapIntent

    data class LineTapped(
        val line: KyricsLine,
    ) : WordTapIntent

    data class WordTapped(
        val syllable: KyricsSyllable,
        val line: KyricsLine,
    ) : WordTapIntent

    data object DismissWordSheet : WordTapIntent

    data class SeekToWord(
        val word: WordListItem,
    ) : WordTapIntent
}
