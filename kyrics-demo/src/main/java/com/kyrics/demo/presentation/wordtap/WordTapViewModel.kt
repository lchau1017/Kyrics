package com.kyrics.demo.presentation.wordtap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kyrics.demo.data.datasource.DemoLyricsEnglish
import com.kyrics.demo.data.datasource.DemoWordDictionary
import com.kyrics.demo.presentation.shared.PlaybackController
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import dagger.hilt.android.lifecycle.HiltViewModel
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
        private val playback = PlaybackController(TOTAL_DURATION_MS, viewModelScope)

        private val _state =
            MutableStateFlow(
                WordTapUiState(
                    lines = lines,
                    totalDurationMs = TOTAL_DURATION_MS,
                    clickableWords = buildClickableWords(),
                ),
            )
        val state: StateFlow<WordTapUiState> = _state.asStateFlow()

        init {
            observePlayback()
        }

        private fun observePlayback() {
            viewModelScope.launch {
                playback.isPlaying.collect { isPlaying ->
                    _state.update {
                        it.copy(
                            isPlaying = isPlaying,
                            selectedWord = if (isPlaying) null else it.selectedWord,
                        )
                    }
                }
            }
            viewModelScope.launch {
                playback.currentTimeMs.collect { timeMs ->
                    _state.update { it.copy(currentTimeMs = timeMs) }
                }
            }
        }

        fun onIntent(intent: WordTapIntent) {
            when (intent) {
                is WordTapIntent.TogglePlayPause -> playback.togglePlayPause()
                is WordTapIntent.Reset -> playback.reset()
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

        private fun onWordTapped(
            syllable: KyricsSyllable,
            line: KyricsLine,
        ) {
            playback.pause()
            val definition = dictionary.lookup(syllable.content) ?: return
            playback.seekTo(line.start.toLong())
            _state.update {
                it.copy(
                    selectedWord = syllable.content.trim().lowercase(),
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
            playback.pause()
            playback.seekTo(word.lineStartMs.toLong())
            val line = lines.getOrNull(word.lineIndex)
            val syllable =
                line?.syllables?.firstOrNull {
                    it.content.trim().equals(word.word, ignoreCase = true)
                }
            val definition = dictionary.lookup(word.word)

            _state.update {
                it.copy(
                    selectedWord = word.word.lowercase(),
                    wordKnowledge =
                        if (definition != null && syllable != null && line != null) {
                            WordKnowledgeState(syllable = syllable, line = line, definition = definition)
                        } else {
                            null
                        },
                )
            }
        }

        companion object {
            private const val TOTAL_DURATION_MS = 31_000L
        }
    }
