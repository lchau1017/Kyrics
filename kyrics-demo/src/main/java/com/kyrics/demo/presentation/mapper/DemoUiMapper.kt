package com.kyrics.demo.presentation.mapper

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.config.KyricsConfig
import com.kyrics.config.kyricsConfig
import com.kyrics.demo.domain.model.DemoSettings
import com.kyrics.demo.domain.model.LyricsData
import com.kyrics.demo.domain.model.ViewerTypeOption
import com.kyrics.demo.presentation.model.ColorPickerTarget
import com.kyrics.demo.presentation.model.DemoUiState
import com.kyrics.demo.presentation.model.ViewerTypeUiModel
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Mapper that transforms domain models to presentation UI models.
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
                // Text settings
                fontSize = settings.fontSize,
                fontWeight = settings.fontWeight,
                fontFamily = settings.fontFamily,
                textAlign = settings.textAlign,
                // Colors
                sungColor = settings.sungColor,
                unsungColor = settings.unsungColor,
                activeColor = settings.activeColor,
                backgroundColor = settings.backgroundColor,
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
                lyricsSource = settings.lyricsSource,
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
            ViewerTypeOption.all.mapIndexed { index, option ->
                ViewerTypeUiModel(
                    index = index,
                    displayName = option.displayName,
                )
            }

        /**
         * Maps UI state back to domain settings for updates.
         */
        fun mapUiStateToSettings(uiState: DemoUiState): DemoSettings =
            DemoSettings(
                lyricsSource = uiState.lyricsSource,
                fontSize = uiState.fontSize,
                fontWeight = uiState.fontWeight,
                fontFamily = uiState.fontFamily,
                textAlign = uiState.textAlign,
                sungColor = uiState.sungColor,
                unsungColor = uiState.unsungColor,
                activeColor = uiState.activeColor,
                backgroundColor = uiState.backgroundColor,
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
            color: androidx.compose.ui.graphics.Color,
        ): DemoUiState =
            when (target) {
                ColorPickerTarget.SUNG_COLOR -> uiState.copy(sungColor = color)
                ColorPickerTarget.UNSUNG_COLOR -> uiState.copy(unsungColor = color)
                ColorPickerTarget.ACTIVE_COLOR -> uiState.copy(activeColor = color)
                ColorPickerTarget.BACKGROUND_COLOR -> uiState.copy(backgroundColor = color)
            }

        private fun buildLibraryConfig(settings: DemoSettings): KyricsConfig =
            kyricsConfig {
                colors {
                    playing = settings.activeColor
                    played = settings.sungColor
                    upcoming = settings.unsungColor
                    background = settings.backgroundColor
                    sung = settings.sungColor
                    unsung = settings.unsungColor
                    active = settings.activeColor
                }

                typography {
                    fontSize = settings.fontSize.sp
                    fontWeight = settings.fontWeight
                    fontFamily = settings.fontFamily
                    textAlign = settings.textAlign
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
                    type = ViewerTypeOption.all.getOrNull(settings.viewerTypeIndex)?.type
                        ?: com.kyrics.config.ViewerType.CENTER_FOCUSED
                }

                layout {
                    lineSpacing = settings.lineSpacing.dp
                    containerPadding = PaddingValues(8.dp)
                }
            }
    }
