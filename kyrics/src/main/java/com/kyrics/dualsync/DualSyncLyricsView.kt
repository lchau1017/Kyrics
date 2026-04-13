package com.kyrics.dualsync

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.components.KyricsSingleLine
import com.kyrics.config.KyricsConfig
import com.kyrics.dualsync.model.DualSyncState
import com.kyrics.dualsync.model.TrackIdentifier
import com.kyrics.models.KyricsLine
import com.kyrics.state.KyricsUiState
import com.kyrics.state.LineUiState
import kotlinx.coroutines.launch
import kotlin.math.max

private val DefaultSecondaryStyle =
    TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Normal,
        textAlign = TextAlign.Center,
    )

/**
 * Composable that renders two synchronized lyric tracks with word-level highlighting.
 *
 * The primary track uses the full Kyrics canvas renderer (same as the original KyricsViewer)
 * for rich character-level animations. The secondary track uses simple text rendering.
 *
 * @param state Combined highlight state for both tracks
 * @param modifier Modifier for the root layout
 * @param primaryConfig Kyrics configuration for the primary track's canvas rendering
 * @param secondaryStyle Text style for the secondary (bottom) track
 * @param showSecondary Whether to display the secondary track
 * @param onWordClick Callback when a word is tapped, with the word text and its track
 */
@Composable
fun DualSyncLyricsView(
    state: DualSyncState,
    modifier: Modifier = Modifier,
    primaryConfig: KyricsConfig = KyricsConfig.Default,
    secondaryStyle: TextStyle = DefaultSecondaryStyle,
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
                    secondaryStyle = secondaryStyle,
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
    secondaryStyle: TextStyle,
    showSecondary: Boolean,
    onWordClick: ((word: String, track: TrackIdentifier) -> Unit)?,
) {
    val primaryLine = primaryHighlight.lines.getOrNull(index)
    val secondaryLine = if (showSecondary) secondaryHighlight.lines.getOrNull(index) else null
    val primaryLineState = primaryHighlight.getLineState(index)
    val secondaryLineState = secondaryHighlight.getLineState(index)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (primaryLine != null) {
            KyricsSingleLine(
                line = primaryLine,
                lineUiState = primaryLineState,
                currentTimeMs = primaryHighlight.currentTimeMs,
                config = primaryConfig,
                onLineClick =
                    onWordClick?.let { callback ->
                        { _ -> callback(primaryLine.getContent(), TrackIdentifier.PRIMARY) }
                    },
            )
        }
        if (secondaryLine != null) {
            SecondaryLine(
                line = secondaryLine,
                lineState = secondaryLineState,
                currentTimeMs = secondaryHighlight.currentTimeMs,
                style = secondaryStyle,
            )
        }
    }
}

@Composable
private fun SecondaryLine(
    line: KyricsLine,
    lineState: LineUiState,
    currentTimeMs: Int,
    style: TextStyle,
) {
    val animatedOpacity by animateFloatAsState(
        targetValue = lineState.opacity,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "secondaryOpacity",
    )

    val annotatedText =
        buildSecondaryText(
            line = line,
            currentTimeMs = currentTimeMs,
            isPlaying = lineState.isPlaying,
            hasPlayed = lineState.hasPlayed,
            baseStyle = style,
        )

    Text(
        text = annotatedText,
        style = style,
        modifier =
            Modifier
                .padding(horizontal = 8.dp)
                .alpha(animatedOpacity),
    )
}

private fun buildSecondaryText(
    line: KyricsLine,
    currentTimeMs: Int,
    isPlaying: Boolean,
    hasPlayed: Boolean,
    baseStyle: TextStyle,
): AnnotatedString =
    buildAnnotatedString {
        line.syllables.forEach { syllable ->
            val syllablePlaying = currentTimeMs >= syllable.start && currentTimeMs <= syllable.end
            val syllablePast = currentTimeMs > syllable.end
            val baseColor = baseStyle.color.takeIf { it != Color.Unspecified } ?: Color.White

            val spanStyle =
                when {
                    isPlaying && syllablePlaying ->
                        SpanStyle(
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                        )
                    hasPlayed || (isPlaying && syllablePast) ->
                        SpanStyle(color = baseColor.copy(alpha = 0.4f))
                    else ->
                        SpanStyle(color = baseColor.copy(alpha = 0.6f))
                }

            withStyle(spanStyle) {
                append(syllable.content)
            }
        }
    }
