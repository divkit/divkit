package com.yandex.div.zoom

import android.widget.ImageView

internal interface ImageHost {

    fun addImage(imageView: ImageView)

    fun removeImage(imageView: ImageView)
}
