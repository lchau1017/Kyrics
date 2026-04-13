package com.kyrics.demo.presentation.dualsync

import com.kyrics.demo.data.datasource.DemoLanguage

/**
 * User intents for the DualSync demo screen.
 */
sealed interface DualSyncIntent {
    data object TogglePlayPause : DualSyncIntent

    data object Reset : DualSyncIntent

    data object ToggleSecondary : DualSyncIntent

    data class SetPrimaryLanguage(
        val language: DemoLanguage,
    ) : DualSyncIntent

    data class SetSecondaryLanguage(
        val language: DemoLanguage,
    ) : DualSyncIntent

    data object SwapLanguages : DualSyncIntent
}
