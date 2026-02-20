package com.kyrics.config

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Layout configuration for the karaoke display.
 * Controls spacing, padding, and interaction.
 */
data class LayoutConfig(
    val viewerConfig: ViewerConfig = ViewerConfig(),
    val linePadding: PaddingValues = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
    val lineSpacing: Dp = 12.dp,
    val containerPadding: PaddingValues = PaddingValues(16.dp),
    val enableLineClick: Boolean = true,
)
