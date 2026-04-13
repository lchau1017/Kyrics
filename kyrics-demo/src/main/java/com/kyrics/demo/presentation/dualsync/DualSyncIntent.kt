package com.kyrics.demo.presentation.dualsync

/**
 * User intents for the DualSync demo screen.
 */
sealed interface DualSyncIntent {
    data object TogglePlayPause : DualSyncIntent

    data object Reset : DualSyncIntent

    data class SetLanguageMode(
        val mode: LanguageMode,
    ) : DualSyncIntent
}
