package com.yandex.div.compose.images

import coil3.request.ErrorResult
import coil3.request.ImageRequest
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

@DivContextScope
internal class ImageRequestListener @Inject constructor(
    private val reporter: DivReporter
) : ImageRequest.Listener {

    override fun onError(request: ImageRequest, result: ErrorResult) {
        reporter.reportError("Failed to load image. ${result.throwable.message}")
    }
}
