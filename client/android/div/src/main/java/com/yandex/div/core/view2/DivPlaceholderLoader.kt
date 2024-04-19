package com.yandex.div.core.view2

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.annotation.MainThread
import com.yandex.div.core.DecodeBase64ImageTask
import com.yandex.div.core.Div2ImageStubProvider
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.LoadableImage
import com.yandex.div.core.view2.errors.ErrorCollector
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future
import javax.inject.Inject

private const val PREVIEW_IS_NOT_BASE_64_IMAGE = "Preview doesn't contain base64 image"

@DivScope
@Mockable
internal class DivPlaceholderLoader @Inject constructor(
    private val imageStubProvider: Div2ImageStubProvider,
    private val executorService: ExecutorService
) {

    @MainThread
    fun applyPlaceholder(
        imageView: LoadableImage,
        errorCollector: ErrorCollector,
        currentPreview: String?,
        currentPlaceholderColor: Int,
        synchronous: Boolean,
        onSetPlaceholder: (Drawable?) -> Unit,
        onSetPreview: (Bitmap?) -> Unit
    ) {
        currentPreview?.let {
            enqueueDecoding(it, imageView, synchronous) { bitmap ->
                if (bitmap == null) {
                    errorCollector.logWarning(Throwable(PREVIEW_IS_NOT_BASE_64_IMAGE))
                    onSetPlaceholder(imageStubProvider.getImageStubDrawable(currentPlaceholderColor))
                } else {
                    onSetPreview(bitmap)
                }
            }
        } ?: onSetPlaceholder(imageStubProvider.getImageStubDrawable(currentPlaceholderColor))
    }

    private fun enqueueDecoding(
        preview: String,
        loadableImage: LoadableImage,
        synchronous: Boolean,
        onDecoded: (Bitmap?) -> Unit
    ) {
        loadableImage.getLoadingTask()?.cancel(true)

        val future = preview.decodeBase64ToBitmap(synchronous) {
            onDecoded(it)
            loadableImage.cleanLoadingTask()
        }

        future?.let { loadableImage.saveLoadingTask(it) }
    }

    private fun String.decodeBase64ToBitmap(
        synchronous: Boolean,
        onDecoded: (Bitmap?) -> Unit
    ): Future<*>? {
        val decodeTask = DecodeBase64ImageTask(this, synchronous, onDecoded)

        return if (synchronous) {
            decodeTask.run()
            null
        } else {
            executorService.submit(decodeTask)
        }
    }
}
