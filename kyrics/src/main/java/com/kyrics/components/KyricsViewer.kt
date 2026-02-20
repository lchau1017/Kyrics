package com.kyrics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kyrics.components.viewers.CenterFocusedViewer
import com.kyrics.components.viewers.FadeThroughViewer
import com.kyrics.components.viewers.SmoothScrollViewer
import com.kyrics.config.KyricsConfig
import com.kyrics.config.ViewerType
import com.kyrics.models.KyricsLine
import com.kyrics.state.KyricsUiState
import com.kyrics.state.rememberKyricsStateHolder

/**
 * Complete karaoke lyrics viewer with automatic scrolling and synchronization.
 *
 * @param lines List of synchronized lines to display
 * @param currentTimeMs Current playback time in milliseconds
 * @param config Complete configuration for visual and layout
 * @param modifier Modifier for the composable
 * @param onLineClick Optional callback when a line is clicked
 */
@Composable
fun KyricsViewer(
    lines: List<KyricsLine>,
    currentTimeMs: Int,
    config: KyricsConfig = KyricsConfig.Default,
    modifier: Modifier = Modifier,
    onLineClick: ((KyricsLine, Int) -> Unit)? = null,
) {
    // Create and manage state holder internally
    val stateHolder = rememberKyricsStateHolder(config)

    // Update state when inputs change
    LaunchedEffect(lines) {
        stateHolder.setLines(lines)
    }

    LaunchedEffect(currentTimeMs) {
        stateHolder.updateTime(currentTimeMs)
    }

    // Get current UI state
    val uiState by stateHolder.uiState

    // Render the viewer
    KyricsViewerContent(
        uiState = uiState,
        config = config,
        modifier = modifier,
        onLineClick = onLineClick,
    )
}

/**
 * Internal content renderer that uses KyricsUiState.
 * This separates state management from rendering.
 */
@Composable
private fun KyricsViewerContent(
    uiState: KyricsUiState,
    config: KyricsConfig,
    modifier: Modifier = Modifier,
    onLineClick: ((KyricsLine, Int) -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(config.visual.backgroundColor)
                .padding(config.layout.containerPadding),
    ) {
        when (config.layout.viewerConfig.type) {
            ViewerType.CENTER_FOCUSED -> {
                CenterFocusedViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick,
                )
            }
            ViewerType.SMOOTH_SCROLL -> {
                SmoothScrollViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick,
                )
            }
            ViewerType.FADE_THROUGH -> {
                FadeThroughViewer(
                    uiState = uiState,
                    config = config,
                    onLineClick = onLineClick,
                )
            }
        }
    }
}
