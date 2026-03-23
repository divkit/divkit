package com.yandex.divkit.demo.screenshot

import androidx.test.espresso.IdlingResource
import coil3.EventListener
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult

class ComposeImageLoadingTracker : EventListener(), IdlingResource {
    private var activeRequests = 0
    private var resourceCallback: IdlingResource.ResourceCallback? = null

    override fun getName(): String = "ComposeImageLoadingTracker"

    override fun isIdleNow(): Boolean = activeRequests <= 0

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        resourceCallback = callback
    }

    override fun onStart(request: ImageRequest) {
        activeRequests++
    }

    override fun onSuccess(request: ImageRequest, result: SuccessResult) {
        onRequestFinished()
    }

    override fun onError(request: ImageRequest, result: ErrorResult) {
        onRequestFinished()
    }

    override fun onCancel(request: ImageRequest) {
        onRequestFinished()
    }

    private fun onRequestFinished() {
        activeRequests--
        if (activeRequests <= 0) {
            resourceCallback?.onTransitionToIdle()
        }
    }
}
