package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.demo.domain.model.Preset

/**
 * Stateless preset selector composable using type-safe [Preset] sealed class.
 */
@Composable
fun PresetSelector(
    onSelectPreset: (Preset) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // First row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                preset = Preset.Classic,
                onClick = { onSelectPreset(Preset.Classic) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Neon,
                onClick = { onSelectPreset(Preset.Neon) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Minimal,
                onClick = { onSelectPreset(Preset.Minimal) },
                modifier = Modifier.weight(1f),
            )
        }

        // Second row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                preset = Preset.Rainbow,
                onClick = { onSelectPreset(Preset.Rainbow) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Fire,
                onClick = { onSelectPreset(Preset.Fire) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Ocean,
                onClick = { onSelectPreset(Preset.Ocean) },
                modifier = Modifier.weight(1f),
            )
        }

        // Third row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                preset = Preset.Retro,
                onClick = { onSelectPreset(Preset.Retro) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Elegant,
                onClick = { onSelectPreset(Preset.Elegant) },
                modifier = Modifier.weight(1f),
            )
            PresetButton(
                preset = Preset.Party,
                onClick = { onSelectPreset(Preset.Party) },
                modifier = Modifier.weight(1f),
            )
        }

        // Fourth row
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            PresetButton(
                preset = Preset.Matrix,
                onClick = { onSelectPreset(Preset.Matrix) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun PresetButton(
    preset: Preset,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(preset.displayName, fontSize = 10.sp)
    }
}
