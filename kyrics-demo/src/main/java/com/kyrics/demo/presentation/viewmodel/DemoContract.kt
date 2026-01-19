package com.kyrics.demo.presentation.viewmodel

import com.kyrics.demo.presentation.model.DemoUiState

/**
 * MVI Contract for the Demo screen.
 * Defines the relationship between State, Intent, and Effect.
 */
interface DemoContract {
    /**
     * UI State - represents the current state of the screen.
     * Should be immutable and contain all data needed for rendering.
     */
    val state: DemoUiState

    /**
     * Intent - user actions that can modify state.
     * Intents are processed by the ViewModel.
     */
    val intent: DemoIntent

    /**
     * Effect - one-time side effects (navigation, toasts, etc).
     * Effects are consumed once and not persisted in state.
     */
    val effect: DemoEffect
}
