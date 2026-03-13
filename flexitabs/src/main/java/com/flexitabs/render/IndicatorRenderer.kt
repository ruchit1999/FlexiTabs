package com.flexitabs.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.flexitabs.model.TabStyle

interface IndicatorRenderer {
    fun render(canvas: Canvas, indicatorBounds: RectF, style: TabStyle)
}

class DefaultIndicatorRenderer(
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
) : IndicatorRenderer {

    override fun render(canvas: Canvas, indicatorBounds: RectF, style: TabStyle) {
        paint.color = style.indicatorColor
        canvas.drawRoundRect(
            indicatorBounds,
            (style.cornerRadiusDp - style.indicatorInsetDp).coerceAtLeast(0f).dpToPx(),
            (style.cornerRadiusDp - style.indicatorInsetDp).coerceAtLeast(0f).dpToPx(),
            paint
        )
    }
}

private fun Float.dpToPx(): Float = this * android.content.res.Resources.getSystem().displayMetrics.density
