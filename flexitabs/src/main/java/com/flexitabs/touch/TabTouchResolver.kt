package com.flexitabs.touch

import android.graphics.RectF

interface TabTouchResolver {
    fun resolveTabIndex(x: Float, tabWidth: Float, tabCount: Int): Int?
    fun resolveTabIndex(x: Float, tabBounds: List<RectF>): Int?
}
