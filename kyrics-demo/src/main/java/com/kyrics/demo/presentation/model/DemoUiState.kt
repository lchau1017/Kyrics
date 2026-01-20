package com.kyrics.demo.presentation.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kyrics.config.KyricsConfig
import com.kyrics.models.KyricsLine

/**
 * Presentation layer UI state - completely flattened for UI consumption.
 * Uses Compose types directly (appropriate for presentation layer).
 *
 * Note: KyricsLine and KyricsConfig are library types used directly since
 * the presentation layer needs to pass them to KyricsViewer composable.
 */
@Immutable
data class DemoUiState(
    // Playback state
    val isPlaying: Boolean = false,
    val currentTimeMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val selectedLineIndex: Int = 0,
    // Text settings (Compose types - appropriate for presentation)
    val fontSize: Float = 32f,
    val fontWeight: FontWeight = FontWeight.Bold,
    val fontFamily: FontFamily = FontFamily.Default,
    val textAlign: TextAlign = TextAlign.Center,
    // Colors (Compose Color - appropriate for presentation)
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
    // Lyrics source (presentation-specific, maps from domain)
    val lyricsSourceIndex: Int = 0,
    // UI-specific state
    val showColorPicker: ColorPickerTarget? = null,
    val viewerTypeOptions: List<ViewerTypeUiModel> = emptyList(),
    val lyricsSourceOptions: List<LyricsSourceUiModel> = emptyList(),
    // Data for display (library types needed for KyricsViewer)
    val demoLines: List<KyricsLine> = emptyList(),
    // Library config (derived in mapper, needed for KyricsViewer)
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
 * UI model for lyrics source options.
 */
@Immutable
data class LyricsSourceUiModel(
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
