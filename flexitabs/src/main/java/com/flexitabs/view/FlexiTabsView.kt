package com.flexitabs.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.use
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.flexitabs.R
import com.flexitabs.animation.TabIndicatorAnimator
import com.flexitabs.layout.DefaultTabLayoutCalculator
import com.flexitabs.layout.TabLayoutCalculator
import com.flexitabs.layout.TabLayoutResult
import com.flexitabs.listener.OnTabSelectedListener
import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import com.flexitabs.render.BackgroundRenderer
import com.flexitabs.render.DefaultBackgroundRenderer
import com.flexitabs.render.DefaultIndicatorRenderer
import com.flexitabs.render.DefaultTabContentRenderer
import com.flexitabs.render.IndicatorRenderer
import com.flexitabs.render.TabContentRenderer
import com.flexitabs.touch.DefaultTabTouchResolver
import com.flexitabs.touch.TabTouchResolver
import kotlin.math.ceil
import kotlin.math.floor

class FlexiTabsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val layoutCalculator: TabLayoutCalculator = DefaultTabLayoutCalculator(),
    private val backgroundRenderer: BackgroundRenderer = DefaultBackgroundRenderer(),
    private val indicatorRenderer: IndicatorRenderer = DefaultIndicatorRenderer(),
    private val tabContentRenderer: TabContentRenderer = DefaultTabContentRenderer(),
    private val touchResolver: TabTouchResolver = DefaultTabTouchResolver(),
    private val indicatorAnimator: TabIndicatorAnimator = TabIndicatorAnimator()
) : View(context, attrs, defStyleAttr) {

    private val backgroundRect = RectF()
    private val indicatorRect = RectF()
    private val tabRects = mutableListOf<RectF>()
    private val tabBoundsForAccessibility = mutableListOf<Rect>()

    private var tabs: List<TabItem> = emptyList()
    private var onTabSelectedListener: OnTabSelectedListener? = null
    private var layoutResult: TabLayoutResult = TabLayoutResult(emptyList(), 0f, com.flexitabs.layout.LayoutDisplayMode.FIXED)
    private var selectedIndex = 0
    private var animatedIndicatorIndex = 0f
    private var contentOffsetX = 0f
    private var style = TabStyle()
    private val accessibilityHelper = FlexiTabsAccessibilityHelper(
        host = this,
        tabsProvider = { tabs },
        boundsProvider = { index -> tabBoundsForAccessibility[index] },
        selectedIndexProvider = { selectedIndex },
        tabClick = { index -> selectInternal(index, animate = true, fromUser = true) }
    )

    init {
        isClickable = true
        isFocusable = true
        minimumHeight = style.heightDp.dpToPxInt()
        parseAttrs(attrs)
        ViewCompat.setAccessibilityDelegate(this, accessibilityHelper)
    }

    fun setTabs(items: List<TabItem>) {
        require(items.size in 3..10) { "FlexiTabs supports between 3 and 10 tabs." }
        tabs = items
        selectedIndex = selectedIndex.coerceIn(0, tabs.lastIndex)
        animatedIndicatorIndex = selectedIndex.toFloat()
        requestLayout()
        invalidate()
        accessibilityHelper.invalidateRoot()
    }

    fun setSelectedIndex(index: Int) {
        selectInternal(index, animate = true, fromUser = false)
    }

    fun getSelectedIndex(): Int = selectedIndex

    fun setOnTabSelectedListener(listener: OnTabSelectedListener) {
        onTabSelectedListener = listener
    }

    fun setStyle(style: TabStyle) {
        this.style = style
        minimumHeight = style.heightDp.dpToPxInt()
        requestLayout()
        invalidate()
    }

    fun getStyle(): TabStyle = style

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredHeight = style.heightDp.dpToPxInt() + paddingTop + paddingBottom
        val resolvedHeight = resolveSize(desiredHeight, heightMeasureSpec)
        val resolvedWidth = resolveSize(suggestedMinimumWidth, widthMeasureSpec)
        setMeasuredDimension(resolvedWidth, resolvedHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        recalculateLayout(w, h)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (tabs.isEmpty() || tabRects.isEmpty()) return

        backgroundRect.set(0f, 0f, width.toFloat(), height.toFloat())
        backgroundRenderer.render(canvas, backgroundRect, style)

        updateIndicatorRect(animatedIndicatorIndex)
        indicatorRenderer.render(canvas, indicatorRect, style)

        canvas.save()
        canvas.translate(-contentOffsetX, 0f)
        tabRects.forEachIndexed { index, rect ->
            tabContentRenderer.render(
                canvas = canvas,
                context = context,
                tabBounds = rect,
                item = tabs[index],
                selected = index == selectedIndex,
                style = style
            )
        }
        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (tabs.isEmpty()) return super.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> return true
            MotionEvent.ACTION_UP -> {
                val resolvedX = event.x + contentOffsetX
                val index = touchResolver.resolveTabIndex(resolvedX, tabRects)
                if (index != null && tabs[index].enabled) {
                    performClick()
                    selectInternal(index, animate = true, fromUser = true)
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun dispatchHoverEvent(event: MotionEvent): Boolean {
        return accessibilityHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event)
    }

    override fun onInitializeAccessibilityNodeInfo(info: android.view.accessibility.AccessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(info)
        AccessibilityNodeInfoCompat.wrap(info).apply {
            className = "android.widget.HorizontalScrollView"
            contentDescription = "FlexiTabs"
        }
    }

    private fun parseAttrs(attrs: AttributeSet?) {
        if (attrs == null) return
        context.obtainStyledAttributes(attrs, R.styleable.FlexiTabsView).use { typedArray ->
            style = style.copy(
                backgroundColor = typedArray.getColor(R.styleable.FlexiTabsView_ft_backgroundColor, style.backgroundColor),
                indicatorColor = typedArray.getColor(R.styleable.FlexiTabsView_ft_indicatorColor, style.indicatorColor),
                selectedTextColor = typedArray.getColor(R.styleable.FlexiTabsView_ft_selectedTextColor, style.selectedTextColor),
                unselectedTextColor = typedArray.getColor(R.styleable.FlexiTabsView_ft_unselectedTextColor, style.unselectedTextColor),
                iconTintColor = typedArray.getColor(R.styleable.FlexiTabsView_ft_iconTintColor, style.iconTintColor),
                cornerRadiusDp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_cornerRadius, style.cornerRadiusDp.dpToPx()).pxToDp(context),
                heightDp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_height, style.heightDp.dpToPx()).pxToDp(context),
                textSizeSp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_textSize, style.textSizeSp.spToPx()).pxToSp(context),
                minTextSizeSp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_minTextSize, style.minTextSizeSp.spToPx()).pxToSp(context),
                iconSizeDp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_iconSize, style.iconSizeDp.dpToPx()).pxToDp(context),
                horizontalPaddingDp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_horizontalPadding, style.horizontalPaddingDp.dpToPx()).pxToDp(context),
                indicatorInsetDp = typedArray.getDimension(R.styleable.FlexiTabsView_ft_indicatorInset, style.indicatorInsetDp.dpToPx()).pxToDp(context),
                animationDurationMillis = typedArray.getInt(R.styleable.FlexiTabsView_ft_animationDuration, style.animationDurationMillis),
                displayMode = when (typedArray.getInt(R.styleable.FlexiTabsView_ft_displayMode, 2)) {
                    0 -> TabDisplayMode.FIXED
                    1 -> TabDisplayMode.SCROLLABLE
                    else -> TabDisplayMode.AUTO
                }
            )
        }
    }

    private fun recalculateLayout(availableWidth: Int, availableHeight: Int) {
        if (tabs.isEmpty() || availableWidth <= 0 || availableHeight <= 0) {
            tabRects.clear()
            tabBoundsForAccessibility.clear()
            return
        }

        layoutResult = layoutCalculator.calculate(
            availableWidth = availableWidth,
            availableHeight = availableHeight,
            density = resources.displayMetrics.density,
            tabs = tabs,
            style = style,
            measureText = { title -> tabContentRenderer.measureTextWidth(title, style) },
            hasIcon = { item -> item.iconResId != null }
        )

        tabRects.clear()
        tabRects += layoutResult.tabBounds.map { RectF(it) }
        tabBoundsForAccessibility.clear()
        tabBoundsForAccessibility += tabRects.map { rect ->
            Rect(
                (rect.left - contentOffsetX).toInt(),
                rect.top.toInt(),
                (rect.right - contentOffsetX).toInt(),
                rect.bottom.toInt()
            )
        }
        clampContentOffset()
        updateIndicatorRect(animatedIndicatorIndex)
        invalidate()
        accessibilityHelper.invalidateRoot()
    }

    private fun updateIndicatorRect(position: Float) {
        if (tabRects.isEmpty()) return
        val lower = floor(position).toInt().coerceIn(tabRects.indices)
        val upper = ceil(position).toInt().coerceIn(tabRects.indices)
        val fraction = position - lower
        val startRect = tabRects[lower]
        val endRect = tabRects[upper]
        val insetPx = style.indicatorInsetDp.dpToPx()
        indicatorRect.set(
            lerp(startRect.left, endRect.left, fraction) + insetPx - contentOffsetX,
            insetPx,
            lerp(startRect.right, endRect.right, fraction) - contentOffsetX - insetPx,
            height.toFloat() - insetPx
        )
        ensureSelectedTabVisible()
    }

    private fun ensureSelectedTabVisible() {
        if (layoutResult.contentWidth <= width || selectedIndex !in tabRects.indices) {
            contentOffsetX = 0f
            return
        }
        val selectedRect = tabRects[selectedIndex]
        val desired = selectedRect.centerX() - (width / 2f)
        contentOffsetX = desired.coerceIn(0f, (layoutResult.contentWidth - width).coerceAtLeast(0f))
    }

    private fun clampContentOffset() {
        contentOffsetX = contentOffsetX.coerceIn(0f, (layoutResult.contentWidth - width).coerceAtLeast(0f))
    }

    private fun selectInternal(index: Int, animate: Boolean, fromUser: Boolean) {
        if (index !in tabs.indices || !tabs[index].enabled || (index == selectedIndex && animate)) return
        val previous = animatedIndicatorIndex
        selectedIndex = index
        if (animate) {
            indicatorAnimator.animate(
                fromIndex = previous,
                toIndex = index.toFloat(),
                style = style,
                onUpdate = { value ->
                    animatedIndicatorIndex = value
                    updateIndicatorRect(value)
                    updateAccessibilityBounds()
                    invalidate()
                },
                onEnd = {
                    animatedIndicatorIndex = index.toFloat()
                    updateAccessibilityBounds()
                }
            )
        } else {
            animatedIndicatorIndex = index.toFloat()
            updateIndicatorRect(animatedIndicatorIndex)
        }
        updateAccessibilityBounds()
        invalidate()
        if (fromUser) {
            sendAccessibilityEvent(android.view.accessibility.AccessibilityEvent.TYPE_VIEW_SELECTED)
        }
        onTabSelectedListener?.onTabSelected(index)
    }

    private fun updateAccessibilityBounds() {
        tabBoundsForAccessibility.clear()
        tabBoundsForAccessibility += tabRects.map { rect ->
            Rect(
                (rect.left - contentOffsetX).toInt(),
                rect.top.toInt(),
                (rect.right - contentOffsetX).toInt(),
                rect.bottom.toInt()
            )
        }
        accessibilityHelper.invalidateRoot()
    }

    override fun onDetachedFromWindow() {
        indicatorAnimator.cancel()
        super.onDetachedFromWindow()
    }

    private fun lerp(start: Float, stop: Float, fraction: Float): Float = start + ((stop - start) * fraction)
}

private fun Float.dpToPx(): Float = this * android.content.res.Resources.getSystem().displayMetrics.density

private fun Float.spToPx(): Float = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP,
    this,
    android.content.res.Resources.getSystem().displayMetrics
)

private fun Float.pxToDp(context: Context): Float = this / context.resources.displayMetrics.density

private fun Float.pxToSp(context: Context): Float {
    val oneSp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 1f, context.resources.displayMetrics)
    return this / oneSp
}

private fun Float.dpToPxInt(): Int = dpToPx().toInt()
