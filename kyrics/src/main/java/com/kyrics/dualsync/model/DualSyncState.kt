package com.kyrics.dualsync.model

import androidx.compose.runtime.Immutable
import com.kyrics.state.KyricsUiState

/**
 * Combined highlight state for two synchronized tracks.
 * Both tracks are driven by the same audio position.
 */
@Immutable
data class DualSyncState(
    val primaryHighlight: KyricsUiState = KyricsUiState(),
    val secondaryHighlight: KyricsUiState = KyricsUiState(),
)
