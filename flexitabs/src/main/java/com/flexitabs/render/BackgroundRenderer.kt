package com.flexitabs.render

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.flexitabs.model.TabStyle

interface BackgroundRenderer {
    fun render(canvas: Canvas, bounds: RectF, style: TabStyle)
}

class DefaultBackgroundRenderer(
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
) : BackgroundRenderer {

    override fun render(canvas: Canvas, bounds: RectF, style: TabStyle) {
        paint.color = style.backgroundColor
        canvas.drawRoundRect(bounds, style.cornerRadiusDp.dpToPx(), style.cornerRadiusDp.dpToPx(), paint)
    }
}

private fun Float.dpToPx(): Float = this * android.content.res.Resources.getSystem().displayMetrics.density
