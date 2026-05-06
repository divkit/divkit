@file:SuppressLint("ComposableNaming")

package com.yandex.div.compose.images

import android.Manifest
import android.annotation.SuppressLint
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import coil3.compose.AsyncImagePainter
import coil3.network.HttpException
import com.yandex.div.compose.dagger.LocalComponent
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@Composable
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
internal fun AsyncImagePainter.observeNetworkRestoration() {
    val controller = LocalComponent.current.networkRestorationController
    LaunchedEffect(this, controller) {
        controller.networkRestored.collect {
            val state = state.value
            if (state is AsyncImagePainter.State.Error && state.result.throwable.isNetworkConnectivityError()) {
                restart()
            }
        }
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
