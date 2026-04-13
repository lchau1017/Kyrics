package com.kyrics.demo.presentation.dualsync

import androidx.compose.runtime.Immutable
import com.kyrics.demo.data.datasource.DemoLanguage
import com.kyrics.dualsync.model.DualSyncState

/**
 * UI state for the DualSync demo screen.
 */
@Immutable
data class DualSyncUiState(
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val primaryLanguage: DemoLanguage = DemoLanguage.ENGLISH,
    val secondaryLanguage: DemoLanguage = DemoLanguage.CHINESE,
    val showSecondary: Boolean = true,
    val syncState: DualSyncState = DualSyncState(),
)
