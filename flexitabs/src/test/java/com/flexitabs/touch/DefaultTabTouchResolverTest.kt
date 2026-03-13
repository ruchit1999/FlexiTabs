package com.flexitabs.touch

import android.graphics.RectF
import org.junit.Assert.assertEquals
import org.junit.Test

class DefaultTabTouchResolverTest {

    private val resolver = DefaultTabTouchResolver()

    @Test
    fun `resolves fixed width index`() {
        assertEquals(2, resolver.resolveTabIndex(x = 220f, tabWidth = 100f, tabCount = 4))
    }

    @Test
    fun `resolves bounds based index`() {
        val bounds = listOf(
            RectF().apply { left = 0f; top = 0f; right = 100f; bottom = 48f },
            RectF().apply { left = 100f; top = 0f; right = 200f; bottom = 48f },
            RectF().apply { left = 200f; top = 0f; right = 320f; bottom = 48f }
        )
        assertEquals(2, resolver.resolveTabIndex(250f, bounds))
    }
}
