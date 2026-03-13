package com.flexitabs.model

import androidx.annotation.ColorInt

data class TabStyle(
    @param:ColorInt val backgroundColor: Int = 0xFFE9EEF6.toInt(),
    @param:ColorInt val indicatorColor: Int = 0xFFFFFFFF.toInt(),
    @param:ColorInt val selectedTextColor: Int = 0xFF101828.toInt(),
    @param:ColorInt val unselectedTextColor: Int = 0xFF667085.toInt(),
    @param:ColorInt val iconTintColor: Int = 0xFF344054.toInt(),
    val cornerRadiusDp: Float = 24f,
    val heightDp: Float = 48f,
    val textSizeSp: Float = 14f,
    val minTextSizeSp: Float = 10f,
    val iconSizeDp: Float = 18f,
    val horizontalPaddingDp: Float = 16f,
    val indicatorInsetDp: Float = 4f,
    val animationDurationMillis: Int = 280,
    val displayMode: TabDisplayMode = TabDisplayMode.AUTO
)
