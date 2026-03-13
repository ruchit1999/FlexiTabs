package com.flexitabs.layout

import android.graphics.RectF
import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import kotlin.math.max

class DefaultTabLayoutCalculator : TabLayoutCalculator {

    override fun calculate(
        availableWidth: Int,
        availableHeight: Int,
        density: Float,
        tabs: List<TabItem>,
        style: TabStyle,
        measureText: (String) -> Float,
        hasIcon: (TabItem) -> Boolean
    ): TabLayoutResult {
        if (tabs.isEmpty() || availableWidth <= 0 || availableHeight <= 0) {
            return TabLayoutResult(emptyList(), 0f, LayoutDisplayMode.FIXED)
        }

        val horizontalPaddingPx = style.horizontalPaddingDp.dpToPx(density)
        val iconSizePx = style.iconSizeDp.dpToPx(density)
        val iconGapPx = 8f.dpToPx(density)
        val desiredMode = when (style.displayMode) {
            TabDisplayMode.FIXED -> LayoutDisplayMode.FIXED
            TabDisplayMode.SCROLLABLE -> LayoutDisplayMode.SCROLLABLE
            TabDisplayMode.AUTO -> if (tabs.size <= 5) LayoutDisplayMode.FIXED else LayoutDisplayMode.SCROLLABLE
        }

        return if (desiredMode == LayoutDisplayMode.FIXED) {
            val widthPerTab = availableWidth / tabs.size.toFloat()
            val bounds = tabs.indices.map { index ->
                val left = widthPerTab * index
                RectF(left, 0f, left + widthPerTab, availableHeight.toFloat())
            }
            TabLayoutResult(bounds, availableWidth.toFloat(), LayoutDisplayMode.FIXED)
        } else {
            val bounds = ArrayList<RectF>(tabs.size)
            var cursorX = 0f
            tabs.forEach { item ->
                val iconWidth = if (hasIcon(item)) iconSizePx + iconGapPx else 0f
                val titleWidth = measureText(item.title)
                val tabWidth = max(availableWidth / 5f, titleWidth + iconWidth + (horizontalPaddingPx * 2f))
                bounds += RectF(cursorX, 0f, cursorX + tabWidth, availableHeight.toFloat())
                cursorX += tabWidth
            }
            TabLayoutResult(bounds, cursorX, LayoutDisplayMode.SCROLLABLE)
        }
    }
}

private fun Float.dpToPx(density: Float): Float = this * density
