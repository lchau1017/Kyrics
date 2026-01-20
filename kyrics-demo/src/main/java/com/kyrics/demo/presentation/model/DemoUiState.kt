package com.kyrics.demo.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kyrics.config.KyricsConfig
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.models.KyricsLine

/**
 * Presentation layer UI state - completely flattened for UI consumption.
 * This decouples the presentation layer from domain models.
 */
@Immutable
data class DemoUiState(
    // Playback state
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val selectedLineIndex: Int = 0,
    // Text settings
    val fontSize: Float = 32f,
    val fontWeight: FontWeight = FontWeight.Bold,
    val fontFamily: FontFamily = FontFamily.Default,
    val textAlign: TextAlign = TextAlign.Center,
    // Colors
    val sungColor: Color = Color.Green,
    val unsungColor: Color = Color.White,
    val activeColor: Color = Color.Yellow,
    val backgroundColor: Color = Color.Black,
    // Visual effects
    val gradientEnabled: Boolean = false,
    val gradientAngle: Float = 45f,
    val blurEnabled: Boolean = false,
    val blurIntensity: Float = 1f,
    // Character animations
    val charAnimEnabled: Boolean = false,
    val charMaxScale: Float = 1.2f,
    val charFloatOffset: Float = 8f,
    val charRotationDegrees: Float = 5f,
    // Line animations
    val lineAnimEnabled: Boolean = false,
    val lineScaleOnPlay: Float = 1.05f,
    // Pulse effect
    val pulseEnabled: Boolean = false,
    val pulseMinScale: Float = 0.95f,
    val pulseMaxScale: Float = 1.05f,
    // Layout
    val lineSpacing: Float = 80f,
    val viewerTypeIndex: Int = 0,
    // Lyrics source
    val lyricsSource: LyricsSource = LyricsSource.TTML,
    // UI-specific state
    val showColorPicker: ColorPickerTarget? = null,
    val viewerTypeOptions: List<ViewerTypeUiModel> = emptyList(),
    // Data for display
    val demoLines: List<KyricsLine> = emptyList(),
    // Library config (derived from settings)
    val libraryConfig: KyricsConfig = KyricsConfig.Default,
) {
    companion object {
        val Initial = DemoUiState()
    }
}

/**
 * UI model for viewer type options.
 */
@Immutable
data class ViewerTypeUiModel(
    val index: Int,
    val displayName: String,
)

/**
 * Represents the target color being edited in the color picker.
 */
enum class ColorPickerTarget {
    SUNG_COLOR,
    UNSUNG_COLOR,
    ACTIVE_COLOR,
    BACKGROUND_COLOR,
}
