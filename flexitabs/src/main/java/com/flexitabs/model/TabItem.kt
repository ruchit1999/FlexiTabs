package com.flexitabs.model

import androidx.annotation.DrawableRes

data class TabItem(
    val id: String,
    val title: String,
    @param:DrawableRes val iconResId: Int? = null,
    val enabled: Boolean = true,
    val contentDescription: String = title
)
