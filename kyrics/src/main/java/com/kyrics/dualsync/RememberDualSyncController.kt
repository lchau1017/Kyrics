package com.kyrics.dualsync

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.kyrics.config.VisualConfig
import com.kyrics.dualsync.model.DualTrackLyrics
import kotlinx.coroutines.flow.Flow

/**
 * Creates and remembers a [DualSyncController] that is scoped to the calling composable's lifecycle.
 *
 * @param lyrics The dual-track lyrics to synchronize
 * @param positionMs Flow of audio playback position in milliseconds
 * @param visualConfig Visual configuration for state calculations
 * @return A remembered controller whose [DualSyncController.state] can be collected
 */
@Composable
fun rememberDualSyncController(
    lyrics: DualTrackLyrics,
    positionMs: Flow<Long>,
    visualConfig: VisualConfig = VisualConfig(),
): DualSyncController {
    val scope = rememberCoroutineScope()
    return remember(lyrics, positionMs) {
        DualSyncController(
            lyrics = lyrics,
            positionMs = positionMs,
            scope = scope,
            visualConfig = visualConfig,
        )
    }
}
