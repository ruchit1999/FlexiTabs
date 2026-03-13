package com.flexitabs.touch

import android.graphics.RectF

class DefaultTabTouchResolver : TabTouchResolver {

    override fun resolveTabIndex(x: Float, tabWidth: Float, tabCount: Int): Int? {
        if (tabWidth <= 0f || tabCount <= 0) return null
        val index = (x / tabWidth).toInt()
        return index.takeIf { it in 0 until tabCount }
    }

    override fun resolveTabIndex(x: Float, tabBounds: List<RectF>): Int? {
        return tabBounds.indexOfFirst { x >= it.left && x < it.right }.takeIf { it >= 0 }
    }
}
