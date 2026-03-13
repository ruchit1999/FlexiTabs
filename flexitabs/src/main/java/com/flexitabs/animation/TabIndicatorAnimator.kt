package com.flexitabs.animation

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.doOnEnd
import com.flexitabs.model.TabStyle

class TabIndicatorAnimator {

    private var animator: ValueAnimator? = null

    fun animate(
        fromIndex: Float,
        toIndex: Float,
        style: TabStyle,
        onUpdate: (Float) -> Unit,
        onEnd: () -> Unit = {}
    ) {
        animator?.cancel()
        animator = ValueAnimator.ofFloat(fromIndex, toIndex).apply {
            duration = style.animationDurationMillis.toLong()
            interpolator = AccelerateDecelerateInterpolator()
            addUpdateListener { onUpdate(it.animatedValue as Float) }
            doOnEnd { onEnd() }
            start()
        }
    }

    fun cancel() {
        animator?.cancel()
        animator = null
    }
}
