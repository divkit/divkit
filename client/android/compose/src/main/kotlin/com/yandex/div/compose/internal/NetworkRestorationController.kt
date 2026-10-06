package com.yandex.div.compose.internal

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.MainThread
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Shares one system callback per DivContext and retries only consumers waiting after an error.
 * Obtains the connectivity manager and subscribes on the first pending retry.
 *
 * A `SharedFlow` collected by every painter would require a `LaunchedEffect` and a coroutine even
 * for successful or loading images. Keeping only failed requests here avoids those per-painter
 * subscriptions.
 *
 * Each network event retries consumers waiting when its coroutine runs. Consumers remove their
 * retry on state changes and when leaving composition; tokens reject attempts replaced during
 * batch processing.
 */
@DivContextScope
internal class NetworkRestorationController @Inject constructor(
    context: Context,
    private val coroutineScope: CoroutineScope,
) {
    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val pendingRetries = mutableMapOf<() -> Unit, Any>()

    private var isObserving = false

    @MainThread
    fun addPendingRetry(retry: () -> Unit) {
        pendingRetries.getOrPut(retry) { Any() }
        startObservingNetwork()
    }

    @MainThread
    fun removePendingRetry(retry: () -> Unit) {
        pendingRetries.remove(retry)
    }

    @MainThread
    private fun startObservingNetwork() {
        if (isObserving) {
            return
        }

        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            // Take a one-time baseline before subscribing, not a snapshot from inside a callback.
            @Suppress("DEPRECATION")
            val initialNetworks = connectivityManager.allNetworks
                .filterTo(mutableSetOf()) { network ->
                    connectivityManager.getNetworkCapabilities(network)
                        ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                }
            connectivityManager.registerNetworkCallback(
                request,
                createNetworkCallback(initialNetworks)
            )
            isObserving = true
        } catch (_: Throwable) {
        }
    }

    private fun createNetworkCallback(initialNetworks: MutableSet<Network>) =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // Registration also reports networks that were already available.
                if (initialNetworks.remove(network)) {
                    return
                }

                coroutineScope.launch {
                    pendingRetries.toList().forEach { (retry, token) ->
                        // Another retry may have replaced this consumer's pending attempt.
                        if (pendingRetries[retry] !== token) {
                            return@forEach
                        }
                        pendingRetries.remove(retry)
                        retry()
                    }
                }
            }

            override fun onLost(network: Network) {
                initialNetworks.remove(network)
            }
        }
}
