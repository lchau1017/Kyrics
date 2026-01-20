package com.kyrics.demo.presentation.mapper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.config.KyricsConfig
import com.kyrics.config.KyricsPresets
import com.kyrics.config.ViewerType
import com.kyrics.config.kyricsConfig
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.model.LyricsData
import com.kyrics.demo.domain.model.LyricsSource
import com.kyrics.demo.domain.model.PresetType
import com.kyrics.demo.domain.model.ViewerTypeId
import com.kyrics.demo.presentation.model.ColorPickerTarget
import com.kyrics.demo.presentation.model.DemoUiState
import com.kyrics.demo.presentation.model.LyricsSourceUiModel
import com.kyrics.demo.presentation.model.ViewerTypeUiModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper that transforms domain models to presentation UI models.
 * Handles conversion between pure Kotlin domain types and Compose types.
 */
@Singleton
class DemoUiMapper
    @Inject
    constructor() {
        /**
         * Maps domain settings to UI state properties.
         */
        fun mapSettingsToUiState(
            settings: DemoSettings,
            currentState: DemoUiState,
        ): DemoUiState =
            currentState.copy(
                // Text settings (convert from domain primitives to Compose types)
                fontSize = settings.fontSize,
                fontWeight = mapFontWeight(settings.fontWeightValue),
                fontFamily = mapFontFamily(settings.fontFamilyName),
                textAlign = mapTextAlign(settings.textAlignName),
                // Colors (convert from ARGB Long to Compose Color)
                sungColor = Color(settings.sungColorArgb),
                unsungColor = Color(settings.unsungColorArgb),
                activeColor = Color(settings.activeColorArgb),
                backgroundColor = Color(settings.backgroundColorArgb),
                // Visual effects
                gradientEnabled = settings.gradientEnabled,
                gradientAngle = settings.gradientAngle,
                blurEnabled = settings.blurEnabled,
                blurIntensity = settings.blurIntensity,
                // Character animations
                charAnimEnabled = settings.charAnimEnabled,
                charMaxScale = settings.charMaxScale,
                charFloatOffset = settings.charFloatOffset,
                charRotationDegrees = settings.charRotationDegrees,
                // Line animations
                lineAnimEnabled = settings.lineAnimEnabled,
                lineScaleOnPlay = settings.lineScaleOnPlay,
                // Pulse effect
                pulseEnabled = settings.pulseEnabled,
                pulseMinScale = settings.pulseMinScale,
                pulseMaxScale = settings.pulseMaxScale,
                // Layout
                lineSpacing = settings.lineSpacing,
                viewerTypeIndex = settings.viewerTypeIndex,
                // Lyrics source
                lyricsSourceIndex = settings.lyricsSource.ordinal,
                // Derived config
                libraryConfig = buildLibraryConfig(settings),
            )

        /**
         * Maps lyrics data to UI state properties.
         */
        fun mapLyricsToUiState(
            lyricsData: LyricsData,
            currentState: DemoUiState,
        ): DemoUiState =
            currentState.copy(
                demoLines = lyricsData.lines,
                totalDurationMs = lyricsData.totalDurationMs,
            )

        /**
         * Maps viewer type options to UI models.
         */
        fun mapViewerTypeOptions(): List<ViewerTypeUiModel> =
            ViewerTypeId.entries.mapIndexed { index, viewerTypeId ->
                ViewerTypeUiModel(
                    index = index,
                    displayName = viewerTypeId.displayName,
                )
            }

        /**
         * Maps lyrics source options to UI models.
         */
        fun mapLyricsSourceOptions(): List<LyricsSourceUiModel> =
            LyricsSource.entries.mapIndexed { index, source ->
                LyricsSourceUiModel(
                    index = index,
                    displayName = source.displayName,
                )
            }

        /**
         * Maps UI state back to domain settings for updates.
         */
        fun mapUiStateToSettings(uiState: DemoUiState): DemoSettings =
            DemoSettings(
                lyricsSource = LyricsSource.entries[uiState.lyricsSourceIndex],
                fontSize = uiState.fontSize,
                fontWeightValue = mapFontWeightToValue(uiState.fontWeight),
                fontFamilyName = mapFontFamilyToName(uiState.fontFamily),
                textAlignName = mapTextAlignToName(uiState.textAlign),
                sungColorArgb = uiState.sungColor.toArgb().toLong(),
                unsungColorArgb = uiState.unsungColor.toArgb().toLong(),
                activeColorArgb = uiState.activeColor.toArgb().toLong(),
                backgroundColorArgb = uiState.backgroundColor.toArgb().toLong(),
                gradientEnabled = uiState.gradientEnabled,
                gradientAngle = uiState.gradientAngle,
                blurEnabled = uiState.blurEnabled,
                blurIntensity = uiState.blurIntensity,
                charAnimEnabled = uiState.charAnimEnabled,
                charMaxScale = uiState.charMaxScale,
                charFloatOffset = uiState.charFloatOffset,
                charRotationDegrees = uiState.charRotationDegrees,
                lineAnimEnabled = uiState.lineAnimEnabled,
                lineScaleOnPlay = uiState.lineScaleOnPlay,
                pulseEnabled = uiState.pulseEnabled,
                pulseMinScale = uiState.pulseMinScale,
                pulseMaxScale = uiState.pulseMaxScale,
                lineSpacing = uiState.lineSpacing,
                viewerTypeIndex = uiState.viewerTypeIndex,
            )

        /**
         * Maps presentation ColorPickerTarget to domain field update.
         */
        fun applyColorUpdate(
            uiState: DemoUiState,
            target: ColorPickerTarget,
            color: Color,
        ): DemoUiState =
            when (target) {
                ColorPickerTarget.SUNG_COLOR -> uiState.copy(sungColor = color)
                ColorPickerTarget.UNSUNG_COLOR -> uiState.copy(unsungColor = color)
                ColorPickerTarget.ACTIVE_COLOR -> uiState.copy(activeColor = color)
                ColorPickerTarget.BACKGROUND_COLOR -> uiState.copy(backgroundColor = color)
            }

        /**
         * Maps preset type to library KyricsConfig.
         */
        fun mapPresetToConfig(presetType: PresetType): KyricsConfig =
            when (presetType) {
                PresetType.CLASSIC -> KyricsPresets.Classic
                PresetType.NEON -> KyricsPresets.Neon
                PresetType.MINIMAL -> KyricsPresets.Minimal
                PresetType.RAINBOW -> KyricsPresets.Rainbow
                PresetType.FIRE -> KyricsPresets.Fire
                PresetType.OCEAN -> KyricsPresets.Ocean
                PresetType.RETRO -> KyricsPresets.Retro
                PresetType.ELEGANT -> KyricsPresets.Elegant
                PresetType.PARTY -> KyricsPresets.Party
                PresetType.MATRIX -> KyricsPresets.Matrix
            }

        /**
         * Maps index to LyricsSource domain enum.
         */
        fun mapIndexToLyricsSource(index: Int): LyricsSource = LyricsSource.entries.getOrElse(index) { LyricsSource.TTML }

        // ==================== Type Conversion Helpers ====================

        private fun mapFontWeight(value: Int): FontWeight =
            when (value) {
                100 -> FontWeight.Thin
                200 -> FontWeight.ExtraLight
                300 -> FontWeight.Light
                400 -> FontWeight.Normal
                500 -> FontWeight.Medium
                600 -> FontWeight.SemiBold
                700 -> FontWeight.Bold
                800 -> FontWeight.ExtraBold
                900 -> FontWeight.Black
                else -> FontWeight.Normal
            }

        private fun mapFontWeightToValue(fontWeight: FontWeight): Int = fontWeight.weight

        private fun mapFontFamily(name: String): FontFamily =
            when (name.lowercase()) {
                "serif" -> FontFamily.Serif
                "sans-serif", "sansserif" -> FontFamily.SansSerif
                "monospace", "mono" -> FontFamily.Monospace
                "cursive" -> FontFamily.Cursive
                else -> FontFamily.Default
            }

        private fun mapFontFamilyToName(fontFamily: FontFamily): String =
            when (fontFamily) {
                FontFamily.Serif -> "serif"
                FontFamily.SansSerif -> "sans-serif"
                FontFamily.Monospace -> "monospace"
                FontFamily.Cursive -> "cursive"
                else -> "default"
            }

        private fun mapTextAlign(name: String): TextAlign =
            when (name.lowercase()) {
                "left", "start" -> TextAlign.Start
                "right", "end" -> TextAlign.End
                "center" -> TextAlign.Center
                "justify" -> TextAlign.Justify
                else -> TextAlign.Center
            }

        private fun mapTextAlignToName(textAlign: TextAlign): String =
            when (textAlign) {
                TextAlign.Start, TextAlign.Left -> "start"
                TextAlign.End, TextAlign.Right -> "end"
                TextAlign.Center -> "center"
                TextAlign.Justify -> "justify"
                else -> "center"
            }

        private fun mapViewerTypeIdToLibraryType(index: Int): ViewerType {
            val viewerTypeId = ViewerTypeId.entries.getOrElse(index) { ViewerTypeId.CENTER_FOCUSED }
            return when (viewerTypeId) {
                ViewerTypeId.CENTER_FOCUSED -> ViewerType.CENTER_FOCUSED
                ViewerTypeId.SMOOTH_SCROLL -> ViewerType.SMOOTH_SCROLL
                ViewerTypeId.STACKED -> ViewerType.STACKED
                ViewerTypeId.HORIZONTAL_PAGED -> ViewerType.HORIZONTAL_PAGED
                ViewerTypeId.WAVE_FLOW -> ViewerType.WAVE_FLOW
                ViewerTypeId.SPIRAL -> ViewerType.SPIRAL
                ViewerTypeId.CAROUSEL_3D -> ViewerType.CAROUSEL_3D
                ViewerTypeId.SPLIT_DUAL -> ViewerType.SPLIT_DUAL
                ViewerTypeId.ELASTIC_BOUNCE -> ViewerType.ELASTIC_BOUNCE
                ViewerTypeId.FADE_THROUGH -> ViewerType.FADE_THROUGH
                ViewerTypeId.RADIAL_BURST -> ViewerType.RADIAL_BURST
                ViewerTypeId.FLIP_CARD -> ViewerType.FLIP_CARD
            }
        }

        @Suppress("LongMethod")
        private fun buildLibraryConfig(settings: DemoSettings): KyricsConfig =
            kyricsConfig {
                colors {
                    playing = Color(settings.activeColorArgb)
                    played = Color(settings.sungColorArgb)
                    upcoming = Color(settings.unsungColorArgb)
                    background = Color(settings.backgroundColorArgb)
                    sung = Color(settings.sungColorArgb)
                    unsung = Color(settings.unsungColorArgb)
                    active = Color(settings.activeColorArgb)
                }

                typography {
                    fontSize = settings.fontSize.sp
                    fontWeight = mapFontWeight(settings.fontWeightValue)
                    fontFamily = mapFontFamily(settings.fontFamilyName)
                    textAlign = mapTextAlign(settings.textAlignName)
                }

                animations {
                    characterAnimations = settings.charAnimEnabled
                    characterScale = settings.charMaxScale
                    characterFloat = settings.charFloatOffset
                    characterRotation = settings.charRotationDegrees
                    characterDuration = 800f
                    lineAnimations = settings.lineAnimEnabled
                    lineScale = settings.lineScaleOnPlay
                    lineDuration = 700f
                    pulse = settings.pulseEnabled
                    pulseMin = settings.pulseMinScale
                    pulseMax = settings.pulseMaxScale
                }

                effects {
                    blur = settings.blurEnabled
                    blurIntensity = settings.blurIntensity
                    upcomingBlur = (3 * settings.blurIntensity).dp
                    distantBlur = (6 * settings.blurIntensity).dp
                }

                gradient {
                    enabled = settings.gradientEnabled
                    angle = settings.gradientAngle
                }

                viewer {
                    type = mapViewerTypeIdToLibraryType(settings.viewerTypeIndex)
                }

                layout {
                    lineSpacing = settings.lineSpacing.dp
                    containerPadding = PaddingValues(8.dp)
                }
            }
    }
