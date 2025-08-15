package com.yandex.div.core.view2.divs.widgets

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.yandex.div.core.annotations.PublicApi
import java.util.concurrent.Future

/**
 * Priority of showing drawables: image -> preview -> placeholder.
 */
@PublicApi
interface LoadableImage {
    val isImageLoaded: Boolean
        get() = false

    val isImagePreview: Boolean
        get() = false

    fun saveLoadingTask(task: Future<*>)
    fun getLoadingTask(): Future<*>?
    fun cleanLoadingTask()

    fun imageLoaded()
    fun previewLoaded()
    fun resetImageLoaded()

    /**
     * Very cheap drawable that can be received on ui thread. Typically ColorDrawable or smth like this.
     * This would be shown when there's no web connection, some error on receiving other images.
     */
    fun setPlaceholder(drawable: Drawable?)

    /**
     * Fast to receive preview image. Usually used when main image is heavy and take long time to process: - an
     * animation or very big bitmap, otherwise can be omitted.
     */
    fun setPreview(drawable: Drawable?)
    fun setPreview(bitmap: Bitmap?)

    /**
     * When image received [isImageLoaded] should be set true.
     */
    fun setImage(bitmap: Bitmap?)
    fun setImage(drawable: Drawable?)
}
