package com.yandex.div.compose.images

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import coil3.ImageLoader
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.network.HttpException
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.internal.NetworkRestorationController
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
internal fun rememberNetworkRestoringImagePainter(
    model: Any?,
    imageLoader: ImageLoader,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
): AsyncImagePainter {
    val networkRestorationController = LocalComponent.current.networkRestorationController
    val restoration = remember(networkRestorationController) {
        ImageNetworkRestoration(networkRestorationController)
    }
    restoration.beginInputUpdate()
    val painter = rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
        onState = { state ->
            restoration.onState(state)
            onState?.invoke(state)
        },
    )
    restoration.bind(painter)
    return painter
}

private class ImageNetworkRestoration(
    private val networkRestorationController: NetworkRestorationController,
) : RememberObserver {
    private lateinit var painter: AsyncImagePainter
    private var remembered = false
    private var updatingInput = false
    private var failedInput: AsyncImagePainter.Input? = null
    private var error: AsyncImagePainter.State.Error? = null

    private val retry: () -> Unit = retry@ {
        val input = failedInput
        val state = error
        clear()
        if (!remembered || state == null) {
            return@retry
        }
        if (painter.state.value === state && painter.input.value === input) {
            painter.restart()
        }
    }

    fun beginInputUpdate() {
        updatingInput = true
    }

    fun bind(painter: AsyncImagePainter) {
        val firstBind = !::painter.isInitialized
        this.painter = painter
        updatingInput = false
        if (firstBind && error == null) {
            onState(painter.state.value)
        } else if (error != null && failedInput == null) {
            // Coil can report an error synchronously before publishing the new input.
            enqueue()
        } else if (failedInput != null && failedInput !== painter.input.value) {
            clear()
        }
    }

    fun onState(state: AsyncImagePainter.State) {
        clear()
        if (state is AsyncImagePainter.State.Error && state.result.throwable.isNetworkConnectivityError()) {
            error = state
            if (!updatingInput) {
                enqueue()
            }
        }
    }

    private fun enqueue() {
        failedInput = painter.input.value
        if (remembered) {
            networkRestorationController.addPendingRetry(retry)
        }
    }

    override fun onRemembered() {
        remembered = true
        if (error != null) {
            networkRestorationController.addPendingRetry(retry)
        }
    }

    override fun onForgotten() {
        remembered = false
        clear()
    }

    override fun onAbandoned() = onForgotten()

    private fun clear() {
        if (error != null) {
            networkRestorationController.removePendingRetry(retry)
        }
        failedInput = null
        error = null
    }
}

private fun Throwable?.isNetworkConnectivityError(): Boolean {
    var cause: Throwable? = this
    while (cause != null) {
        when (cause) {
            is UnknownHostException,
            is ConnectException,
            is SocketTimeoutException,
            is SocketException,
            is InterruptedIOException,
            is HttpException -> return true
        }
        cause = cause.cause
    }
    return false
}
