package com.kyrics.demo.presentation.wordtap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrics.demo.data.datasource.DemoLyricsEnglish
import com.kyrics.demo.data.datasource.DemoWordDictionary
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WordTapViewModel
    @Inject
    constructor(
        private val dictionary: DemoWordDictionary,
    ) : ViewModel() {
        private val lines = DemoLyricsEnglish.track()

        private val _state =
            MutableStateFlow(
                WordTapUiState(
                    lines = lines,
                    totalDurationMs = TOTAL_DURATION_MS,
                    clickableWords = buildClickableWords(),
                ),
            )
        val state: StateFlow<WordTapUiState> = _state.asStateFlow()

        private var playbackJob: Job? = null

        fun onIntent(intent: WordTapIntent) {
            when (intent) {
                is WordTapIntent.TogglePlayPause -> togglePlayPause()
                is WordTapIntent.Reset -> reset()
                is WordTapIntent.LineTapped -> onLineTapped()
                is WordTapIntent.WordTapped -> onWordTapped(intent.syllable, intent.line)
                is WordTapIntent.DismissWordSheet -> dismissWordSheet()
                is WordTapIntent.SeekToWord -> seekToWord(intent.word)
            }
        }

        private fun buildClickableWords(): List<WordListItem> =
            lines.flatMapIndexed { lineIndex, line ->
                line.syllables
                    .filter { dictionary.hasWord(it.content) }
                    .map { syllable ->
                        WordListItem(
                            word = syllable.content.trim(),
                            lineIndex = lineIndex,
                            lineContent = line.getContent(),
                            lineStartMs = line.start,
                            timestampMs = syllable.start,
                        )
                    }
            }

        private fun onLineTapped() {
            if (_state.value.isPlaying) {
                _state.update { it.copy(isPlaying = false) }
                stopPlayback()
            }
        }

        private fun onWordTapped(
            syllable: KyricsSyllable,
            line: KyricsLine,
        ) {
            if (_state.value.isPlaying) {
                _state.update { it.copy(isPlaying = false) }
                stopPlayback()
            }
            val definition = dictionary.lookup(syllable.content) ?: return
            val lineIndex = lines.indexOf(line)
            _state.update {
                it.copy(
                    currentTimeMs = line.start.toLong(),
                    selectedWord = syllable.content.trim().lowercase(),
                    selectedLineIndex = if (lineIndex >= 0) lineIndex else null,
                    wordKnowledge =
                        WordKnowledgeState(
                            syllable = syllable,
                            line = line,
                            definition = definition,
                        ),
                )
            }
        }

        private fun dismissWordSheet() {
            _state.update { it.copy(wordKnowledge = null) }
        }

        private fun seekToWord(word: WordListItem) {
            val line = lines.getOrNull(word.lineIndex)
            val syllable = line?.syllables?.firstOrNull { it.content.trim().equals(word.word, ignoreCase = true) }
            val definition = dictionary.lookup(word.word)

            _state.update {
                it.copy(
                    currentTimeMs = word.lineStartMs.toLong(),
                    selectedWord = word.word.lowercase(),
                    selectedLineIndex = word.lineIndex,
                    wordKnowledge =
                        if (definition != null && syllable != null && line != null) {
                            WordKnowledgeState(
                                syllable = syllable,
                                line = line,
                                definition = definition,
                            )
                        } else {
                            null
                        },
                )
            }
        }

        private fun togglePlayPause() {
            val newIsPlaying = !_state.value.isPlaying
            _state.update { it.copy(isPlaying = newIsPlaying) }
            if (newIsPlaying) startPlayback() else stopPlayback()
        }

        private fun startPlayback() {
            playbackJob?.cancel()
            playbackJob =
                viewModelScope.launch {
                    while (_state.value.isPlaying) {
                        delay(TICK_MS)
                        val current = _state.value.currentTimeMs
                        val newTime =
                            if (current + TICK_MS > TOTAL_DURATION_MS) 0L else current + TICK_MS
                        _state.update { it.copy(currentTimeMs = newTime) }
                        if (newTime == 0L) {
                            _state.update { it.copy(isPlaying = false) }
                            stopPlayback()
                            return@launch
                        }
                    }
                }
        }

        private fun stopPlayback() {
            playbackJob?.cancel()
            playbackJob = null
        }

        private fun reset() {
            stopPlayback()
            _state.update { it.copy(isPlaying = false, currentTimeMs = 0L) }
        }

        companion object {
            private const val TICK_MS = 100L
            private const val TOTAL_DURATION_MS = 31_000L
        }
    }
