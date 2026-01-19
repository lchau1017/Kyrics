package com.kyrics.demo.presentation.viewmodel

/**
 * Sealed interface representing one-time side effects in the Demo screen.
 * Effects are used for actions that should happen once (not persisted in state).
 */
sealed interface DemoEffect {
    /**
     * Emitted when a preset has been successfully loaded.
     */
    data object PresetLoaded : DemoEffect
}
