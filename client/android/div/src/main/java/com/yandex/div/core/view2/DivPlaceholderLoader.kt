package com.yandex.div.core.view2

import androidx.annotation.MainThread
import com.yandex.div.core.DecodeBase64ImageTask
import com.yandex.div.core.Div2ImageStubProvider
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.divs.widgets.LoadableImage
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@DivScope
@Mockable
internal class DivPlaceholderLoader @Inject constructor(
    private val imageStubProvider: Div2ImageStubProvider,
    private val executorService: ExecutorService
) {

    @MainThread
    fun applyPlaceholder(
        imageView: LoadableImage,
        currentPreview: String?,
        currentPlaceholderColor: Int,
        synchronous: Boolean,
        onPreviewSet: () -> Unit = { }
    ) {
        val previewLoaded = currentPreview != null
        if (!previewLoaded) {
            val imageStub = imageStubProvider.getImageStubDrawable(currentPlaceholderColor)
            imageView.setPlaceholder(imageStub)
        }
        currentPreview.decodeBase64ToBitmap(imageView, synchronous, onPreviewSet)
    }

    private fun String?.decodeBase64ToBitmap(
        loadableImage: LoadableImage,
        synchronous: Boolean,
        onPreviewSet: () -> Unit
    ) {
        val base64string = this ?: return
        loadableImage.getLoadingTask()?.cancel(true)
        val decodeTask = DecodeBase64ImageTask(
            base64string, loadableImage, synchronous, onPreviewSet)
        if (synchronous) {
            decodeTask.run()
            loadableImage.cleanLoadingTask()
        } else {
            val future = executorService.submit(decodeTask)
            loadableImage.saveLoadingTask(future)
        }
    }
}
