package com.kyrics.demo.presentation.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kyrics.demo.domain.model.PresetType
import com.kyrics.demo.presentation.model.ColorPickerTarget

/**
 * Sealed interface representing all user intents in the Demo screen.
 *
 * Intent naming conventions:
 * - Use verbs for actions: Toggle, Update, Select, Load
 * - Group related intents with sealed interfaces
 *
 * Note: Intents use Compose types (Color, FontWeight, etc.) since they
 * originate from UI interactions. The mapper converts these to domain types.
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
    }

    // ==================== Presets ====================

    data class LoadPreset(
        val presetType: PresetType,
    ) : DemoIntent
}
