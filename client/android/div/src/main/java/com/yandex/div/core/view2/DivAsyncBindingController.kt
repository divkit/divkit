package com.yandex.div.core.view2

import com.yandex.div.DivDataTag
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.binding.AsyncBindingObserver
import com.yandex.div.core.util.binding.AsyncBindingRequest
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseMedia
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch

internal class DivAsyncBindingController(
    private val divView: Div2View,
    private val rootStates: DivRootStateController,
    private val bindData: (
        data: DivData?,
        oldData: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>?,
        temporary: Boolean,
        request: AsyncBindingRequest?,
    ) -> Boolean,
) {
    private val bindingDispatcher get() = divView.viewComponent.bindingDispatcher
    private var mediaWasReleased = false
    @Volatile
    private var bindingRequest: AsyncBindingRequest? = null

    fun onAttachedToWindow(bindOnAttach: () -> Unit) {
        val request = bindingRequest?.takeIf { it.isPending }
        val error = runCatching(bindOnAttach).exceptionOrNull()
        if (error != null) {
            if (request == null) {
                throw error
            }
            rethrowBindingFailure(request, error)
        }
        if (bindingRequest === request && request?.isPrepared == true &&
            rootStates.bindingPhase == DivBindingPhase.APPLIED) {
            request.report(DivBindingPhase.APPLIED)
        }
    }

    fun onBindingStarted(request: AsyncBindingRequest?) {
        mediaWasReleased = false
        if (bindingRequest !== request) {
            cancelBindingRequest()
        }
    }

    fun setDataAsync(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>? = null,
        temporary: Boolean = true,
        onComplete: ((Boolean) -> Unit)?,
    ): Unit = with(divView) {
        if (paths == null && !bindingDispatcher.hasPendingAsyncBindings && data === divData) {
            divView.loadMedia()
            onComplete?.invoke(true)
            return
        }
        val onError: (Throwable) -> Unit = { onComplete?.invoke(false) }
        val timer = histogramReporter.onAsyncBindingQueued()
        bindingDispatcher.runOnBindingThread(BindingTaskType.DATA, onComplete, onError, timer) {
            if (paths == null && data === divData) {
                divView.loadMedia()
                true
            } else {
                bindData(data, oldDivData ?: divData, tag, paths, temporary, null)
            }
        }
    }

    fun setDataAsyncWithPhases(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        observer: AsyncBindingObserver?,
    ): Unit = with(divView) {
        val request = AsyncBindingRequest(observer, ::logError, tag, bindingDispatcher.currentGeneration)
        if (!hasPendingAsyncBindings() && data === divData) {
            replaceBindingRequest(request)
            val generation = bindingDispatcher.currentGeneration
            runCatching { loadMedia() }.onFailure { rethrowBindingFailure(request, it) }
            UiThreadHandler.executeOnMainThread {
                if (bindingDispatcher.currentGeneration != generation || bindingRequest !== request) {
                    request.cancel()
                } else {
                    reportBindingPhase(request, data)
                }
            }
            return
        }
        bindingDispatcher.runOnBindingThread(
            BindingTaskType.DATA,
            onComplete = { _: Boolean -> reportBindingPhase(request, data) },
            onError = { request.report(DivBindingPhase.FAILED) },
            onCancelled = request::cancel,
            timer = histogramReporter.onAsyncBindingQueued(),
        ) {
            replaceBindingRequest(request)
            if (data === divData) {
                loadMedia()
                true
            } else {
                bindData(data, oldDivData ?: divData, tag, null, true, request)
            }
        }
    }

    fun loadMedia(): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.DATA) {
            if (mediaWasReleased) {
                mediaWasReleased = false
                viewComponent.mediaLoadViewVisitor.loadMedia()
            }
        }
    }

    fun releaseMedia(): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            mediaWasReleased = true
            cancelImageLoads()
            releaseMedia(this)
        }
    }

    fun applyPatchAsync(
        patch: DivPatch,
        onComplete: ((Boolean) -> Unit)?,
        applyPatch: (DivPatch) -> Boolean,
    ) {
        val onError: (Throwable) -> Unit = { onComplete?.invoke(false) }
        bindingDispatcher.runOnBindingThread(BindingTaskType.DATA, onComplete, onError) {
            applyPatch(patch)
        }
    }

    private fun replaceBindingRequest(request: AsyncBindingRequest) {
        val previous = bindingRequest
        bindingRequest = request
        previous?.cancel()
    }

    fun cancelBindingRequest() {
        val request = bindingRequest
        bindingRequest = null
        request?.cancel()
    }

    private fun reportBindingPhase(request: AsyncBindingRequest, data: DivData?) {
        val phase = if (data != null && data === divView.divData) {
            rootStates.bindingPhase
        } else {
            null
        }
        val reason = when {
            data == null -> "NO_DATA"
            phase == null -> "ROOT_NOT_PREPARED"
            else -> null
        }
        request.report(phase ?: DivBindingPhase.FAILED, reason)
    }

    private fun rethrowBindingFailure(request: AsyncBindingRequest, error: Throwable): Nothing {
        // Reporting must not replace the original binding error.
        runCatching { divView.logError(error) }
        request.report(DivBindingPhase.FAILED)
        throw error
    }
}
