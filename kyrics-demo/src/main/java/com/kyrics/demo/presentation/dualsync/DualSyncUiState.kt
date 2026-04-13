package com.kyrics.demo.presentation.dualsync

import androidx.compose.runtime.Immutable
import com.kyrics.dualsync.model.DualSyncState
import com.kyrics.dualsync.model.DualTrackLyrics

/**
 * Language display mode for the DualSync demo.
 */
enum class LanguageMode {
    DUAL,
    EN_ONLY,
    ZH_ONLY,
}

/**
 * UI state for the DualSync demo screen.
 */
@Immutable
data class DualSyncUiState(
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val languageMode: LanguageMode = LanguageMode.DUAL,
    val lyrics: DualTrackLyrics = DualTrackLyrics(emptyList(), emptyList()),
    val syncState: DualSyncState = DualSyncState(),
)
