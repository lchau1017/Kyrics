@file:Suppress("DEPRECATION")

package com.kyrics.demo.presentation.wordtap

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import kotlinx.coroutines.launch

private const val WORD_TAG = "WORD"
private val HighlightColor = Color(0xFF1DB954)

/**
 * Custom lyrics view for the Word Tap demo.
 * Shows line-level highlighting (active line brighter) with specific
 * clickable words visually marked in green with underline.
 * No syllable-by-syllable progress animation.
 */
@Composable
fun WordTapLyricsView(
    lines: List<KyricsLine>,
    currentTimeMs: Long,
    clickableWords: Set<String>,
    selectedWord: String?,
    onWordClick: (KyricsSyllable, KyricsLine) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    val activeIndex =
        lines
            .indexOfFirst { line ->
                currentTimeMs >= line.start && currentTimeMs <= line.end
            }.takeIf { it >= 0 }

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
                    top = screenHeight * 0.35f,
                    bottom = screenHeight * 0.5f,
                    start = 24.dp,
                    end = 24.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            itemsIndexed(lines) { index, line ->
                val isActive = index == activeIndex
                val isPast = activeIndex != null && index < activeIndex
                WordTapLine(
                    line = line,
                    isActive = isActive,
                    isPast = isPast,
                    clickableWords = clickableWords,
                    selectedWord = selectedWord,
                    onWordClick = onWordClick,
                )
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
private fun WordTapLine(
    line: KyricsLine,
    isActive: Boolean,
    isPast: Boolean,
    clickableWords: Set<String>,
    selectedWord: String?,
    onWordClick: (KyricsSyllable, KyricsLine) -> Unit,
) {
    val targetOpacity =
        when {
            isActive -> 1f
            isPast -> 0.3f
            else -> 0.5f
        }
    val animatedOpacity by animateFloatAsState(
        targetValue = targetOpacity,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "lineOpacity",
    )

    val baseStyle =
        TextStyle(
            fontSize = if (isActive) 28.sp else 24.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center,
        )

    val annotatedText = buildLineAnnotatedText(line, clickableWords, selectedWord)

    ClickableText(
        text = annotatedText,
        style = baseStyle,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .alpha(animatedOpacity),
        onClick = { offset ->
            annotatedText
                .getStringAnnotations(WORD_TAG, offset, offset)
                .firstOrNull()
                ?.let { annotation ->
                    val syllableIndex = annotation.item.toIntOrNull() ?: return@let
                    val syllable = line.syllables.getOrNull(syllableIndex) ?: return@let
                    onWordClick(syllable, line)
                }
        },
    )
}

private fun buildLineAnnotatedText(
    line: KyricsLine,
    clickableWords: Set<String>,
    selectedWord: String?,
) = buildAnnotatedString {
    line.syllables.forEachIndexed { syllableIndex, syllable ->
        val wordKey = syllable.content.lowercase().trim()
        val isClickable = clickableWords.contains(wordKey)

        if (isClickable) {
            pushStringAnnotation(WORD_TAG, syllableIndex.toString())
            withStyle(syllableSpanStyle(wordKey == selectedWord)) {
                append(syllable.content)
            }
            pop()
        } else {
            append(syllable.content)
        }
    }
}

private fun syllableSpanStyle(isSelected: Boolean): SpanStyle =
    if (isSelected) {
        SpanStyle(
            color = Color.White,
            background = HighlightColor,
            fontWeight = FontWeight.Bold,
        )
    } else {
        SpanStyle(
            color = HighlightColor,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
        )
    }
