package com.yandex.div.compose.network

import coil3.network.HttpException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal fun isNetworkConnectivityError(error: Throwable): Boolean {
    var cause: Throwable? = error
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
