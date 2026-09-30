package com.yandex.divkit.demo.screenshot

import coil3.EventListener
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult

class ComposeImageLoadingTracker : EventListener() {
    private var activeRequests = 0

    val isIdle: Boolean get() = activeRequests <= 0

    override fun onStart(request: ImageRequest) {
        activeRequests++
    }

    override fun onSuccess(request: ImageRequest, result: SuccessResult) {
        activeRequests--
    }

    override fun onError(request: ImageRequest, result: ErrorResult) {
        activeRequests--
    }

    override fun onCancel(request: ImageRequest) {
        activeRequests--
    }
}
