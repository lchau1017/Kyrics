package com.kyrics.dualsync

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.config.kyricsConfig
import com.kyrics.dualsync.model.DualSyncState
import com.kyrics.dualsync.model.TrackIdentifier
import com.kyrics.state.KyricsUiState
import kotlinx.coroutines.launch
import kotlin.math.max

/**
 * Composable that renders two synchronized lyric tracks with character-level highlighting.
 *
 * Both tracks use the full Kyrics canvas renderer for rich animations.
 * Primary is displayed on top, secondary below.
 *
 * @param state Combined highlight state for both tracks
 * @param modifier Modifier for the root layout
 * @param primaryConfig Kyrics configuration for the primary track
 * @param secondaryConfig Kyrics configuration for the secondary track (smaller font by default)
 * @param showSecondary Whether to display the secondary track
 * @param onWordClick Callback when a line is tapped, with the line text and its track
 */
@Composable
fun DualSyncLyricsView(
    state: DualSyncState,
    modifier: Modifier = Modifier,
    primaryConfig: KyricsConfig = KyricsConfig.Default,
    secondaryConfig: KyricsConfig = DefaultSecondaryConfig,
    showSecondary: Boolean = true,
    onWordClick: ((word: String, track: TrackIdentifier) -> Unit)? = null,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val lineCount =
        max(
            state.primaryHighlight.lines.size,
            if (showSecondary) state.secondaryHighlight.lines.size else 0,
        )

    val activeIndex = state.primaryHighlight.currentLineIndex
    var previousIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(activeIndex) {
        if (activeIndex != null && activeIndex != previousIndex) {
            previousIndex = activeIndex
            coroutineScope.launch {
                listState.animateScrollToItem(activeIndex)
            }
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenHeight = maxHeight

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding =
                PaddingValues(
                    top = screenHeight * 0.4f,
                    bottom = screenHeight * 0.6f,
                    start = 16.dp,
                    end = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            items(lineCount) { index ->
                DualLineBlock(
                    index = index,
                    primaryHighlight = state.primaryHighlight,
                    secondaryHighlight = state.secondaryHighlight,
                    primaryConfig = primaryConfig,
                    secondaryConfig = secondaryConfig,
                    showSecondary = showSecondary,
                    onWordClick = onWordClick,
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun DualLineBlock(
    index: Int,
    primaryHighlight: KyricsUiState,
    secondaryHighlight: KyricsUiState,
    primaryConfig: KyricsConfig,
    secondaryConfig: KyricsConfig,
    showSecondary: Boolean,
    onWordClick: ((word: String, track: TrackIdentifier) -> Unit)?,
) {
    val primaryLine = primaryHighlight.lines.getOrNull(index)
    val secondaryLine = if (showSecondary) secondaryHighlight.lines.getOrNull(index) else null

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (primaryLine != null) {
            KyricsSingleLine(
                line = primaryLine,
                lineUiState = primaryHighlight.getLineState(index),
                currentTimeMs = primaryHighlight.currentTimeMs,
                config = primaryConfig,
                onLineClick =
                    onWordClick?.let { callback ->
                        { _ -> callback(primaryLine.getContent(), TrackIdentifier.PRIMARY) }
                    },
            )
        }
        if (secondaryLine != null) {
            KyricsSingleLine(
                line = secondaryLine,
                lineUiState = secondaryHighlight.getLineState(index),
                currentTimeMs = secondaryHighlight.currentTimeMs,
                config = secondaryConfig,
                onLineClick =
                    onWordClick?.let { callback ->
                        { _ -> callback(secondaryLine.getContent(), TrackIdentifier.SECONDARY) }
                    },
            )
        }
    }
}

private val DefaultSecondaryConfig =
    kyricsConfig {
        typography {
            fontSize = 20.sp
        }
    }
