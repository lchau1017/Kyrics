package com.kyrics.demo.presentation.screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kyrics.demo.presentation.model.ViewerTypeUiModel

/**
 * Stateless viewer type selector composable.
 */
@Composable
fun ViewerTypeSelector(
    viewerTypeOptions: List<ViewerTypeUiModel>,
    selectedIndex: Int,
    onSelectViewerType: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        itemsIndexed(viewerTypeOptions) { index, option ->
            FilterChip(
                selected = selectedIndex == index,
                onClick = { onSelectViewerType(index) },
                label = { Text(option.displayName, fontSize = 10.sp) },
            )
        }
    }
}
