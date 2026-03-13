package com.flexitabs.view

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.flexitabs.model.TabItem

internal class FlexiTabsAccessibilityHelper(
    host: View,
    private val tabsProvider: () -> List<TabItem>,
    private val boundsProvider: (Int) -> Rect,
    private val selectedIndexProvider: () -> Int,
    private val tabClick: (Int) -> Unit
) : ExploreByTouchHelper(host) {

    override fun getVirtualViewAt(x: Float, y: Float): Int {
        return tabsProvider().indices.firstOrNull { boundsProvider(it).contains(x.toInt(), y.toInt()) }
            ?: ExploreByTouchHelper.INVALID_ID
    }

    override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
        virtualViewIds.addAll(tabsProvider().indices.toList())
    }

    override fun onPopulateNodeForVirtualView(virtualViewId: Int, node: AccessibilityNodeInfoCompat) {
        val item = tabsProvider()[virtualViewId]
        node.className = "android.widget.Button"
        node.contentDescription = item.contentDescription
        node.isClickable = item.enabled
        node.isEnabled = item.enabled
        node.isSelected = selectedIndexProvider() == virtualViewId
        node.setBoundsInParent(boundsProvider(virtualViewId))
        node.addAction(AccessibilityNodeInfoCompat.ACTION_CLICK)
    }

    override fun onPerformActionForVirtualView(virtualViewId: Int, action: Int, arguments: Bundle?): Boolean {
        if (action == AccessibilityNodeInfoCompat.ACTION_CLICK && tabsProvider().getOrNull(virtualViewId)?.enabled == true) {
            tabClick(virtualViewId)
            sendEventForVirtualView(virtualViewId, android.view.accessibility.AccessibilityEvent.TYPE_VIEW_CLICKED)
            return true
        }
        return false
    }
}
