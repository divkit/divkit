package com.yandex.div.compose.internal

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.yandex.div.compose.dagger.DivContextScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

/**
 * Listens to system connectivity events and exposes a hot stream of "network restored" pulses.
 *
 * Exists as a process-wide bus so consumers (image painters, video players, etc.) don't have to
 * each register their own [ConnectivityManager.NetworkCallback]. Subscribers decide locally whether
 * to react.
 */
@DivContextScope
internal class NetworkRestorationController @Inject constructor(context: Context) {

    private val _networkRestored = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val networkRestored: SharedFlow<Unit> = _networkRestored.asSharedFlow()

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            _networkRestored.tryEmit(Unit)
        }
    }

    init {
        val connectivityManager = context.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        try {
            val request: NetworkRequest = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()

            connectivityManager.registerNetworkCallback(request, callback)
        } catch (_: Throwable) { }
    }
}
