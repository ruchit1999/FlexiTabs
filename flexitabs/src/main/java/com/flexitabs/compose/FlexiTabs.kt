package com.flexitabs.compose

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import kotlin.math.roundToInt

@Composable
fun FlexiTabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    style: TabStyle = FlexiTabsDefaults.style(),
    onTabSelected: (Int) -> Unit
) {
    require(tabs.size in 3..10) { "FlexiTabs supports between 3 and 10 tabs." }

    val shape = RoundedCornerShape(style.cornerRadiusDp.dp)
    val scrollState = rememberScrollState()
    val density = LocalDensity.current
    val tabWidths = remember(tabs) { mutableStateListOf<Dp>().apply { repeat(tabs.size) { add(0.dp) } } }
    val displayMode = when (style.displayMode) {
        TabDisplayMode.FIXED -> TabDisplayMode.FIXED
        TabDisplayMode.SCROLLABLE -> TabDisplayMode.SCROLLABLE
        TabDisplayMode.AUTO -> if (tabs.size <= 5) TabDisplayMode.FIXED else TabDisplayMode.SCROLLABLE
    }
    val indicatorOffset by animateFloatAsState(
        targetValue = widthPrefix(tabWidths, selectedIndex, density),
        animationSpec = tween(durationMillis = style.animationDurationMillis),
        label = "flexitabs_indicator_offset"
    )
    val indicatorWidth by animateFloatAsState(
        targetValue = widthAt(tabWidths, selectedIndex, density),
        animationSpec = tween(durationMillis = style.animationDurationMillis),
        label = "flexitabs_indicator_width"
    )

    LaunchedEffect(selectedIndex, displayMode, tabWidths.sumOf { with(density) { it.toPx().toDouble() } }) {
        if (displayMode == TabDisplayMode.SCROLLABLE) {
            val targetCenter = indicatorOffset + (indicatorWidth / 2f)
            scrollState.animateScrollTo((targetCenter - with(density) { 160.dp.toPx() }).roundToInt().coerceAtLeast(0))
        }
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(Color(style.backgroundColor))
            .height(style.heightDp.dp)
            .then(if (displayMode == TabDisplayMode.SCROLLABLE) Modifier.horizontalScroll(scrollState) else Modifier)
    ) {
        Box(
            modifier = Modifier
                .offset { IntOffset(indicatorOffset.roundToInt(), with(density) { style.indicatorInsetDp.dp.roundToPx() }) }
                .size(
                    width = with(density) { indicatorWidth.toDp() } - style.indicatorInsetDp.dp * 2,
                    height = style.heightDp.dp - style.indicatorInsetDp.dp * 2
                )
                .clip(RoundedCornerShape((style.cornerRadiusDp - style.indicatorInsetDp).coerceAtLeast(0f).dp))
                .background(Color(style.indicatorColor))
        )

        Row(
            modifier = if (displayMode == TabDisplayMode.FIXED) Modifier.fillMaxWidth() else Modifier
        ) {
            tabs.forEachIndexed { index, item ->
                val tabModifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        tabWidths[index] = with(density) { coordinates.size.width.toDp() }
                    }
                    .height(style.heightDp.dp)
                    .clickable(enabled = item.enabled) { onTabSelected(index) }
                    .padding(horizontal = style.horizontalPaddingDp.dp)

                if (displayMode == TabDisplayMode.FIXED) {
                    TabSlot(
                        modifier = Modifier.weight(1f).then(tabModifier),
                        item = item,
                        selected = index == selectedIndex,
                        style = style
                    )
                } else {
                    TabSlot(
                        modifier = tabModifier,
                        item = item,
                        selected = index == selectedIndex,
                        style = style
                    )
                }
            }
        }
    }
}

@Composable
private fun RowScope.TabSlot(
    modifier: Modifier,
    item: TabItem,
    selected: Boolean,
    style: TabStyle
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.iconResId?.let { iconRes ->
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = Color(style.iconTintColor).copy(alpha = if (item.enabled) 1f else 0.45f),
                modifier = Modifier.size(style.iconSizeDp.dp)
            )
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(8.dp))
        }
        Text(
            text = item.title,
            color = Color(if (selected) style.selectedTextColor else style.unselectedTextColor).copy(alpha = if (item.enabled) 1f else 0.45f),
            fontSize = style.textSizeSp.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            softWrap = false,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium
        )
    }
}

private fun widthPrefix(widths: SnapshotStateList<Dp>, selectedIndex: Int, density: Density): Float {
    return with(density) { widths.take(selectedIndex.coerceAtLeast(0)).sumOf { it.toPx().toDouble() }.toFloat() }
}

private fun widthAt(widths: SnapshotStateList<Dp>, index: Int, density: Density): Float {
    return with(density) { widths.getOrNull(index)?.toPx() ?: 0f }
}

object FlexiTabsDefaults {
    fun style(): TabStyle = TabStyle()
}
