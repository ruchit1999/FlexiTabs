package com.flexitabs.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.TextUtils
import android.util.TypedValue
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import kotlin.math.roundToInt

interface TabContentRenderer {
    fun render(
        canvas: Canvas,
        context: Context,
        tabBounds: RectF,
        item: TabItem,
        selected: Boolean,
        style: TabStyle
    )

    fun measureTextWidth(title: String, style: TabStyle): Float
}

class DefaultTabContentRenderer : TabContentRenderer {

    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.LEFT
    }

    override fun render(
        canvas: Canvas,
        context: Context,
        tabBounds: RectF,
        item: TabItem,
        selected: Boolean,
        style: TabStyle
    ) {
        val density = context.resources.displayMetrics.density
        val scaledDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, context.resources.displayMetrics)
        val iconSizePx = style.iconSizeDp * density
        val maxTextWidth = tabBounds.width() - (style.horizontalPaddingDp * density * 2f) - if (item.iconResId != null) iconSizePx + (8f * density) else 0f
        val fittedTextSize = calculateTextSize(item.title, scaledDensity, maxTextWidth, style)
        textPaint.textSize = fittedTextSize
        textPaint.color = if (selected) style.selectedTextColor else style.unselectedTextColor
        textPaint.alpha = if (item.enabled) 255 else 120

        val ellipsized = TextUtils.ellipsize(item.title, textPaint, maxTextWidth.coerceAtLeast(0f), TextUtils.TruncateAt.END).toString()
        val textWidth = textPaint.measureText(ellipsized)
        val iconDrawable = loadIcon(context, item, style, item.enabled)
        val totalWidth = textWidth + if (iconDrawable != null) iconSizePx + (8f * density) else 0f
        var cursorX = tabBounds.centerX() - (totalWidth / 2f)

        iconDrawable?.let { drawable ->
            val top = (tabBounds.centerY() - (iconSizePx / 2f)).roundToInt()
            drawable.setBounds(
                cursorX.roundToInt(),
                top,
                (cursorX + iconSizePx).roundToInt(),
                (top + iconSizePx).roundToInt()
            )
            drawable.draw(canvas)
            cursorX += iconSizePx + (8f * density)
        }

        val baseline = tabBounds.centerY() - ((textPaint.descent() + textPaint.ascent()) / 2f)
        canvas.drawText(ellipsized, cursorX, baseline, textPaint)
    }

    override fun measureTextWidth(title: String, style: TabStyle): Float {
        textPaint.textSize = style.textSizeSp.spToPx()
        return textPaint.measureText(title)
    }

    private fun calculateTextSize(
        title: String,
        scaledDensity: Float,
        maxTextWidth: Float,
        style: TabStyle
    ): Float {
        var currentSize = style.textSizeSp * scaledDensity
        val minSize = style.minTextSizeSp * scaledDensity
        while (currentSize > minSize) {
            textPaint.textSize = currentSize
            if (textPaint.measureText(title) <= maxTextWidth) {
                return currentSize
            }
            currentSize -= scaledDensity * 0.5f
        }
        return minSize
    }

    private fun loadIcon(
        context: Context,
        item: TabItem,
        style: TabStyle,
        enabled: Boolean
    ): Drawable? {
        val resId = item.iconResId ?: return null
        val drawable = AppCompatResources.getDrawable(context, resId)?.mutate() ?: return null
        val wrapped = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrapped, style.iconTintColor)
        wrapped.alpha = if (enabled) 255 else 120
        return wrapped
    }
}

private fun Float.spToPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    android.content.res.Resources.getSystem().displayMetrics
)
