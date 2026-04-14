package com.kyrics.demo.presentation.shared

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Reusable fake audio playback controller.
 * Ticks at a fixed interval, wraps at totalDurationMs, and auto-pauses at end.
 */
class PlaybackController(
    private val totalDurationMs: Long,
    private val scope: CoroutineScope,
) {
    private val _currentTimeMs = MutableStateFlow(0L)
    val currentTimeMs: StateFlow<Long> = _currentTimeMs.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private var playbackJob: Job? = null

    fun togglePlayPause() {
        if (_isPlaying.value) pause() else play()
    }

    fun play() {
        _isPlaying.value = true
        playbackJob?.cancel()
        playbackJob =
            scope.launch {
                while (_isPlaying.value) {
                    delay(TICK_MS)
                    val current = _currentTimeMs.value
                    val newTime = if (current + TICK_MS > totalDurationMs) 0L else current + TICK_MS
                    _currentTimeMs.value = newTime
                    if (newTime == 0L) {
                        _isPlaying.value = false
                        return@launch
                    }
                }
            }
    }

    fun pause() {
        _isPlaying.value = false
        playbackJob?.cancel()
        playbackJob = null
    }

    fun reset() {
        pause()
        _currentTimeMs.value = 0L
    }

    fun seekTo(timeMs: Long) {
        _currentTimeMs.value = timeMs
    }

    companion object {
        private const val TICK_MS = 100L
    }
}
