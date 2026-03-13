package com.flexitabs.layout

import android.graphics.RectF
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle

interface TabLayoutCalculator {
    fun calculate(
        availableWidth: Int,
        availableHeight: Int,
        density: Float,
        tabs: List<TabItem>,
        style: TabStyle,
        measureText: (String) -> Float,
        hasIcon: (TabItem) -> Boolean
    ): TabLayoutResult
}

data class TabLayoutResult(
    val tabBounds: List<RectF>,
    val contentWidth: Float,
    val displayMode: LayoutDisplayMode
)

enum class LayoutDisplayMode {
    FIXED,
    SCROLLABLE
}
