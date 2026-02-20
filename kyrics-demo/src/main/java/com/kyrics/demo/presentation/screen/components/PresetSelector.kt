package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.demo.domain.model.PresetType

/**
 * Stateless preset selector composable using type-safe [PresetType] enum.
 */
@Composable
fun PresetSelector(
    onSelectPreset: (PresetType) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        PresetButton(
            presetType = PresetType.CLASSIC,
            onClick = { onSelectPreset(PresetType.CLASSIC) },
            modifier = Modifier.weight(1f),
        )
        PresetButton(
            presetType = PresetType.NEON,
            onClick = { onSelectPreset(PresetType.NEON) },
            modifier = Modifier.weight(1f),
        )
        PresetButton(
            presetType = PresetType.MINIMAL,
            onClick = { onSelectPreset(PresetType.MINIMAL) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PresetButton(
    presetType: PresetType,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(presetType.displayName, fontSize = 10.sp)
    }
}
