package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.model.Preset
import com.kyrics.demo.presentation.model.ColorPickerTarget

/**
 * Sealed interface representing all user intents in the Demo screen.
 *
 * Intent naming conventions:
 * - Use verbs for actions: Toggle, Update, Select, Load
 * - Group related intents with sealed interfaces
 *
 * Note: While intents ideally should be pure Kotlin, we use Compose types here
 * because:
 * 1. These types (Color, FontWeight, etc.) are stable and won't change
 * 2. Creating wrapper types would add unnecessary complexity for a demo app
 * 3. The domain layer (DemoSettings) already uses these types
 */
sealed interface DemoIntent {
    // ==================== Playback ====================

    sealed interface Playback : DemoIntent {
        data object TogglePlayPause : Playback

        data object Reset : Playback

        data class Seek(
            val timeMs: Long,
        ) : Playback

        data class UpdateTime(
            val timeMs: Long,
        ) : Playback
    }

    // ==================== Selection ====================

    sealed interface Selection : DemoIntent {
        data class SelectLine(
            val index: Int,
        ) : Selection

        data class SelectViewerType(
            val index: Int,
        ) : Selection
    }

    // ==================== Color Picker ====================

    sealed interface ColorPicker : DemoIntent {
        data class Show(
            val target: ColorPickerTarget,
        ) : ColorPicker

        data object Dismiss : ColorPicker

        data class UpdateColor(
            val target: ColorPickerTarget,
            val color: Color,
        ) : ColorPicker
    }

    // ==================== Font Settings ====================

    sealed interface Font : DemoIntent {
        data class UpdateSize(
            val size: Float,
        ) : Font

        data class UpdateWeight(
            val weight: FontWeight,
        ) : Font

        data class UpdateFamily(
            val family: FontFamily,
        ) : Font

        data class UpdateAlign(
            val align: TextAlign,
        ) : Font
    }

    // ==================== Layout ====================

    sealed interface Layout : DemoIntent {
        data class UpdateLineSpacing(
            val spacing: Float,
        ) : Layout
    }

    // ==================== Visual Effects ====================

    sealed interface VisualEffect : DemoIntent {
        data class ToggleGradient(
            val enabled: Boolean,
        ) : VisualEffect

        data class UpdateGradientAngle(
            val angle: Float,
        ) : VisualEffect

        data class ToggleBlur(
            val enabled: Boolean,
        ) : VisualEffect

        data class UpdateBlurIntensity(
            val intensity: Float,
        ) : VisualEffect
    }

    // ==================== Animations ====================

    sealed interface Animation : DemoIntent {
        // Character animations
        data class ToggleCharAnimation(
            val enabled: Boolean,
        ) : Animation

        data class UpdateCharMaxScale(
            val scale: Float,
        ) : Animation

        data class UpdateCharFloatOffset(
            val offset: Float,
        ) : Animation

        data class UpdateCharRotation(
            val degrees: Float,
        ) : Animation

        // Line animations
        data class ToggleLineAnimation(
            val enabled: Boolean,
        ) : Animation

        data class UpdateLineScaleOnPlay(
            val scale: Float,
        ) : Animation

        // Pulse effect
        data class TogglePulse(
            val enabled: Boolean,
        ) : Animation

        data class UpdatePulseMinScale(
            val scale: Float,
        ) : Animation

        data class UpdatePulseMaxScale(
            val scale: Float,
        ) : Animation
    }

    // ==================== Presets ====================

    data class LoadPreset(
        val preset: Preset,
    ) : DemoIntent

    // ==================== Lyrics Source ====================

    data class SelectLyricsSource(
        val source: LyricsSource,
    ) : DemoIntent
}
