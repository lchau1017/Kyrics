package com.kyrics.config

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Predefined configurations showcasing different visual styles.
 */
object KyricsPresets {
    /**
     * Classic karaoke style - simple and clean
     */
    val Classic =
        KyricsConfig(
            visual =
                VisualConfig(
                    playingTextColor = Color.Yellow,
                    playedTextColor = Color.Green,
                    upcomingTextColor = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    backgroundColor = Color.Black,
                    gradientEnabled = false,
                ),
        )

    /**
     * Neon style with gradient effects
     */
    val Neon =
        KyricsConfig(
            visual =
                VisualConfig(
                    playingTextColor = Color.Cyan,
                    playedTextColor = Color.Magenta,
                    upcomingTextColor = Color.White.copy(alpha = 0.6f),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    gradientEnabled = true,
                    backgroundColor = Color.Black,
                    gradientAngle = 45f,
                    colors =
                        ColorConfig(
                            sung = Color.Magenta,
                            unsung = Color.Cyan,
                            active = Color.Yellow,
                        ),
                ),
        )

    /**
     * Get all presets as a list
     */
    val allPresets =
        listOf(
            "Classic" to Classic,
            "Neon" to Neon,
        )
}
