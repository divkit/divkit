package com.yandex.div.zoom

import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView

internal class WindowImageHost(
    private val window: Window
) : ImageHost {

    private val decorView
        get() = window.decorView as ViewGroup

    override fun addImage(imageView: ImageView) {
        decorView.addView(imageView)
    }

    override fun removeImage(imageView: ImageView) {
        decorView.removeView(imageView)
    }
}
