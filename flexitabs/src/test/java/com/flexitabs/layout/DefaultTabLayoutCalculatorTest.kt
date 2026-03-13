package com.flexitabs.layout

import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultTabLayoutCalculatorTest {

    private val calculator = DefaultTabLayoutCalculator()

    @Test
    fun `auto mode uses fixed layout for five tabs or less`() {
        val result = calculator.calculate(
            availableWidth = 500,
            availableHeight = 96,
            density = 1f,
            tabs = List(5) { index -> TabItem(id = index.toString(), title = "Tab $index") },
            style = TabStyle(displayMode = TabDisplayMode.AUTO),
            measureText = { 64f },
            hasIcon = { false }
        )

        assertEquals(LayoutDisplayMode.FIXED, result.displayMode)
        assertEquals(5, result.tabBounds.size)
        assertEquals(500f, result.contentWidth, 0.01f)
    }

    @Test
    fun `scrollable mode calculates content width larger than container`() {
        val result = calculator.calculate(
            availableWidth = 400,
            availableHeight = 96,
            density = 1f,
            tabs = List(8) { index -> TabItem(id = index.toString(), title = "Long Tab $index") },
            style = TabStyle(displayMode = TabDisplayMode.SCROLLABLE),
            measureText = { 120f },
            hasIcon = { it.iconResId != null }
        )

        assertEquals(LayoutDisplayMode.SCROLLABLE, result.displayMode)
        assertEquals(true, result.contentWidth > 400f)
    }
}
