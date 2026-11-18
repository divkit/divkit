package com.yandex.div.compose.images

import coil3.request.ErrorResult
import coil3.request.ImageRequest
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class ImageRequestListener @Inject constructor(
    private val configuration: ImageLoaderConfiguration,
    private val reporter: DivReporter
) : ImageRequest.Listener {

    override fun onError(request: ImageRequest, result: ErrorResult) {
        if (configuration.reportErrors) {
            reporter.reportError(result.throwable.message ?: "Failed to load image")
        }
    }
}
