package com.kyrics.dualsync

import com.kyrics.config.VisualConfig
import com.kyrics.dualsync.model.DualSyncState
import com.kyrics.dualsync.model.DualTrackLyrics
import com.kyrics.state.StateCalculator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Controller that synchronizes two independent lyric tracks against the same audio clock.
 *
 * Internally runs the existing [StateCalculator] logic once per track on each position update,
 * producing a combined [DualSyncState] that consumers can collect.
 *
 * @param lyrics The dual-track lyrics data
 * @param positionMs A flow of audio playback position in milliseconds
 * @param scope Coroutine scope that controls the controller's lifetime
 * @param visualConfig Visual configuration for state calculations
 */
class DualSyncController(
    private val lyrics: DualTrackLyrics,
    positionMs: Flow<Long>,
    scope: CoroutineScope,
    private val visualConfig: VisualConfig = VisualConfig(),
) {
    private val _state = MutableStateFlow(DualSyncState())
    val state: StateFlow<DualSyncState> = _state.asStateFlow()

    init {
        scope.launch {
            positionMs.collect { position ->
                updatePosition(position.toInt())
            }
        }
    }

    private fun updatePosition(currentTimeMs: Int) {
        val primaryHighlight =
            StateCalculator.calculateState(
                lines = lyrics.primary,
                currentTimeMs = currentTimeMs,
                visualConfig = visualConfig,
            )
        val secondaryHighlight =
            StateCalculator.calculateState(
                lines = lyrics.secondary,
                currentTimeMs = currentTimeMs,
                visualConfig = visualConfig,
            )
        _state.value =
            DualSyncState(
                primaryHighlight = primaryHighlight,
                secondaryHighlight = secondaryHighlight,
            )
    }
}
