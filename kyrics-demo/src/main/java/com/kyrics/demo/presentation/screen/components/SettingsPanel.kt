package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.demo.presentation.model.ColorPickerTarget
import com.kyrics.demo.presentation.model.DemoUiState
import com.kyrics.demo.presentation.viewmodel.DemoIntent
import java.util.Locale

/**
 * Stateless settings panel composable containing all demo controls.
 */
@Composable
fun SettingsPanel(
    state: DemoUiState,
    onIntent: (DemoIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        // Playback controls
        ControlPanel(
            isPlaying = state.isPlaying,
            currentTimeMs = state.currentTimeMs,
            totalDurationMs = state.totalDurationMs,
            onPlayPause = { onIntent(DemoIntent.Playback.TogglePlayPause) },
            onReset = { onIntent(DemoIntent.Playback.Reset) },
            onSeek = { onIntent(DemoIntent.Playback.Seek(it)) },
        )

        SectionDivider()

        // Viewer Type
        SectionTitle("Viewer Type")
        ViewerTypeSelector(
            viewerTypeOptions = state.viewerTypeOptions,
            selectedIndex = state.viewerTypeIndex,
            onSelectViewerType = { onIntent(DemoIntent.Selection.SelectViewerType(it)) },
        )

        SectionDivider()

        // Font settings
        FontSettingsSection(
            fontSize = state.fontSize,
            fontWeight = state.fontWeight,
            fontFamily = state.fontFamily,
            textAlign = state.textAlign,
            lineSpacing = state.lineSpacing,
            onIntent = onIntent,
        )

        SectionDivider()

        // Colors
        ColorsSection(
            sungColor = state.sungColor,
            unsungColor = state.unsungColor,
            activeColor = state.activeColor,
            backgroundColor = state.backgroundColor,
            onIntent = onIntent,
        )

        SectionDivider()

        // Visual Effects
        VisualEffectsSection(
            gradientEnabled = state.gradientEnabled,
            gradientAngle = state.gradientAngle,
            blurEnabled = state.blurEnabled,
            blurIntensity = state.blurIntensity,
            onIntent = onIntent,
        )

        SectionDivider()

        // Animations
        AnimationsSection(
            charAnimEnabled = state.charAnimEnabled,
            charMaxScale = state.charMaxScale,
            charFloatOffset = state.charFloatOffset,
            charRotationDegrees = state.charRotationDegrees,
            lineAnimEnabled = state.lineAnimEnabled,
            lineScaleOnPlay = state.lineScaleOnPlay,
            pulseEnabled = state.pulseEnabled,
            pulseMinScale = state.pulseMinScale,
            pulseMaxScale = state.pulseMaxScale,
            onIntent = onIntent,
        )

        SectionDivider()

        // Presets
        SectionTitle("Load Preset")
        PresetSelector(
            onSelectPreset = { onIntent(DemoIntent.LoadPreset(preset = it)) },
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium)
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun FontSettingsSection(
    fontSize: Float,
    fontWeight: FontWeight,
    fontFamily: FontFamily,
    textAlign: TextAlign,
    lineSpacing: Float,
    onIntent: (DemoIntent) -> Unit,
) {
    SectionTitle("Font Settings")

    Text("Size: ${fontSize.toInt()}sp")
    Slider(
        value = fontSize,
        onValueChange = { onIntent(DemoIntent.Font.UpdateSize(it)) },
        valueRange = 12f..60f,
    )

    Text("Font Weight")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            FontWeight.Light to "Light",
            FontWeight.Normal to "Normal",
            FontWeight.Bold to "Bold",
            FontWeight.Black to "Black",
        ).forEach { (weight, label) ->
            FilterChip(
                selected = fontWeight == weight,
                onClick = { onIntent(DemoIntent.Font.UpdateWeight(weight)) },
                label = { Text(label, fontSize = 10.sp) },
            )
        }
    }

    Text("Font Family")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            FontFamily.Default to "Default",
            FontFamily.Serif to "Serif",
            FontFamily.Monospace to "Mono",
            FontFamily.Cursive to "Cursive",
        ).forEach { (family, label) ->
            FilterChip(
                selected = fontFamily == family,
                onClick = { onIntent(DemoIntent.Font.UpdateFamily(family)) },
                label = { Text(label, fontSize = 10.sp) },
            )
        }
    }

    Text("Text Alignment")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        listOf(
            TextAlign.Start to "Left",
            TextAlign.Center to "Center",
            TextAlign.End to "Right",
        ).forEach { (align, label) ->
            FilterChip(
                selected = textAlign == align,
                onClick = { onIntent(DemoIntent.Font.UpdateAlign(align)) },
                label = { Text(label, fontSize = 10.sp) },
            )
        }
    }

    Text("Line Spacing: ${lineSpacing.toInt()}dp")
    Slider(
        value = lineSpacing,
        onValueChange = { onIntent(DemoIntent.Layout.UpdateLineSpacing(it)) },
        valueRange = 0f..150f,
    )
}

@Composable
private fun ColorsSection(
    sungColor: Color,
    unsungColor: Color,
    activeColor: Color,
    backgroundColor: Color,
    onIntent: (DemoIntent) -> Unit,
) {
    SectionTitle("Colors")

    ColorRow("Sung", sungColor) {
        onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.SUNG_COLOR))
    }
    ColorRow("Unsung", unsungColor) {
        onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.UNSUNG_COLOR))
    }
    ColorRow("Active", activeColor) {
        onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.ACTIVE_COLOR))
    }
    ColorRow("Background", backgroundColor) {
        onIntent(DemoIntent.ColorPicker.Show(ColorPickerTarget.BACKGROUND_COLOR))
    }
}

@Composable
private fun ColorRow(
    label: String,
    color: Color,
    onClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 12.sp)
        Box(
            modifier =
                Modifier
                    .size(30.dp)
                    .background(color)
                    .border(1.dp, Color.Gray),
        )
    }
}

@Composable
private fun VisualEffectsSection(
    gradientEnabled: Boolean,
    gradientAngle: Float,
    blurEnabled: Boolean,
    blurIntensity: Float,
    onIntent: (DemoIntent) -> Unit,
) {
    SectionTitle("Visual Effects")

    // Gradient
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = gradientEnabled,
            onCheckedChange = { onIntent(DemoIntent.VisualEffect.ToggleGradient(it)) },
        )
        Text("Gradient", modifier = Modifier.padding(start = 8.dp))
    }
    if (gradientEnabled) {
        Text("Angle: ${gradientAngle.toInt()}°", fontSize = 12.sp)
        Slider(
            value = gradientAngle,
            onValueChange = { onIntent(DemoIntent.VisualEffect.UpdateGradientAngle(it)) },
            valueRange = 0f..360f,
        )
    }

    // Blur
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = blurEnabled,
            onCheckedChange = { onIntent(DemoIntent.VisualEffect.ToggleBlur(it)) },
        )
        Text("Blur (for non-active lines)", modifier = Modifier.padding(start = 8.dp))
    }
    if (blurEnabled) {
        Text("Intensity: ${String.format(Locale.US, "%.1f", blurIntensity)}", fontSize = 12.sp)
        Slider(
            value = blurIntensity,
            onValueChange = { onIntent(DemoIntent.VisualEffect.UpdateBlurIntensity(it)) },
            valueRange = 0.1f..3f,
        )
    }
}

@Composable
private fun AnimationsSection(
    charAnimEnabled: Boolean,
    charMaxScale: Float,
    charFloatOffset: Float,
    charRotationDegrees: Float,
    lineAnimEnabled: Boolean,
    lineScaleOnPlay: Float,
    pulseEnabled: Boolean,
    pulseMinScale: Float,
    pulseMaxScale: Float,
    onIntent: (DemoIntent) -> Unit,
) {
    SectionTitle("Animations")

    // Character animations
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = charAnimEnabled,
            onCheckedChange = { onIntent(DemoIntent.Animation.ToggleCharAnimation(it)) },
        )
        Text("Character Animation", modifier = Modifier.padding(start = 8.dp))
    }
    if (charAnimEnabled) {
        Text("Max Scale: ${String.format(Locale.US, "%.2f", charMaxScale)}", fontSize = 12.sp)
        Slider(
            value = charMaxScale,
            onValueChange = { onIntent(DemoIntent.Animation.UpdateCharMaxScale(it)) },
            valueRange = 1f..2f,
        )
        Text("Float Offset: ${charFloatOffset.toInt()}", fontSize = 12.sp)
        Slider(
            value = charFloatOffset,
            onValueChange = { onIntent(DemoIntent.Animation.UpdateCharFloatOffset(it)) },
            valueRange = 0f..20f,
        )
        Text("Rotation: ${charRotationDegrees.toInt()}°", fontSize = 12.sp)
        Slider(
            value = charRotationDegrees,
            onValueChange = { onIntent(DemoIntent.Animation.UpdateCharRotation(it)) },
            valueRange = 0f..15f,
        )
    }

    // Line animations
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = lineAnimEnabled,
            onCheckedChange = { onIntent(DemoIntent.Animation.ToggleLineAnimation(it)) },
        )
        Text("Line Animation", modifier = Modifier.padding(start = 8.dp))
    }
    if (lineAnimEnabled) {
        Text("Scale on Play: ${String.format(Locale.US, "%.2f", lineScaleOnPlay)}", fontSize = 12.sp)
        Slider(
            value = lineScaleOnPlay,
            onValueChange = { onIntent(DemoIntent.Animation.UpdateLineScaleOnPlay(it)) },
            valueRange = 1f..1.5f,
        )
    }

    // Pulse animation
    Row(verticalAlignment = Alignment.CenterVertically) {
        Switch(
            checked = pulseEnabled,
            onCheckedChange = { onIntent(DemoIntent.Animation.TogglePulse(it)) },
        )
        Text("Pulse Effect", modifier = Modifier.padding(start = 8.dp))
    }
    if (pulseEnabled) {
        Text(
            "Pulse Range: ${String.format(Locale.US, "%.2f", pulseMinScale)} - ${String.format(Locale.US, "%.2f", pulseMaxScale)}",
            fontSize = 12.sp,
        )
        Slider(
            value = pulseMinScale,
            onValueChange = { onIntent(DemoIntent.Animation.UpdatePulseMinScale(it)) },
            valueRange = 0.9f..1f,
        )
        Slider(
            value = pulseMaxScale,
            onValueChange = { onIntent(DemoIntent.Animation.UpdatePulseMaxScale(it)) },
            valueRange = 1f..1.1f,
        )
    }
}
