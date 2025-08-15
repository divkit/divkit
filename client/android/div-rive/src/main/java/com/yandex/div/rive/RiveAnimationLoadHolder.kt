package com.yandex.div.rive

import android.os.Handler
import androidx.annotation.MainThread
import app.rive.runtime.kotlin.core.errors.RiveException
import com.yandex.div.core.DivPreloader
import java.lang.ref.WeakReference
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Future

internal class RiveAnimationLoadHolder(
    private val repo: DivRiveRepository,
    private val loadExecutor: ExecutorService,
    private val handler: Handler,
    private val riveAnimationProps: DivRiveCustomProps,
    private val logger: DivRiveLogger
) {
    private var future: Future<DivRiveRepository.RiveResult>? = null
    private var viewWeekRef = WeakReference<DivRiveContainer>(null)
    private var callBack: DivPreloader.Callback? = null
    private var setAnimationRunnable: Runnable? = null

    @MainThread
    fun setView(view: DivRiveContainer) {
        viewWeekRef = WeakReference(view)
        checkLoadingState()
        getLoadedResultSync()?.let { tryBindAnimationView(it) }
    }

    private fun checkLoadingState() {
        if (!isLoadingStarted()) {
            future?.cancel(true)
            val resultCallable = {
                repo.receiveRiveAnimation(riveAnimationProps.url).also {
                    callBack?.finish(it.throwable != null)
                    tryBindAnimationView(it)
                }
            }
            future = loadExecutor.submit(resultCallable)
        }
    }

    fun preload(callback: DivPreloader.Callback) {
        this.callBack = callback
        checkLoadingState()
        getLoadedResultSync()?.let {
            this.callBack?.finish(it.throwable != null)
        }
    }

    private fun isLoadingStarted(): Boolean = future?.isCancelled == false

    fun cancel() {
        future?.cancel(true)
    }

    private fun getLoadedResultSync(): DivRiveRepository.RiveResult? {
        try {
            return future?.takeIf { it.isDone }?.get()
        } catch (interruptedException: InterruptedException) {
            logger.error("load operation for ${riveAnimationProps.url} was canceled", interruptedException)
        } catch (executionException: ExecutionException) {
            logger.error("load operation threw an exception for ${riveAnimationProps.url}", executionException)
        }
        return null
    }

    private fun tryBindAnimationView(loadResult: DivRiveRepository.RiveResult) {
        val byteArray = loadResult.byteArray
        if (byteArray != null) {
            setAnimationRunnable?.let { handler.removeCallbacks(it) }
            handler.post(setAnimationRunnable ?: Runnable {
                try {
                    viewWeekRef.get()?.setAnimationBytes(
                        bytes = byteArray,
                        fit = riveAnimationProps.fit,
                        alignment = riveAnimationProps.alignment,
                        loop = riveAnimationProps.loop
                    )
                } catch (riveException: RiveException) {
                    logger.error("failed to set rive animation at ${riveAnimationProps.url}", riveException)
                }
            }.also { setAnimationRunnable = it })
        } else {
            logger.error("failed to receive rive animation at ${riveAnimationProps.url}", loadResult.throwable)
        }
    }
}
