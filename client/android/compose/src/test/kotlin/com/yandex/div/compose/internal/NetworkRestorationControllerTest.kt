package com.yandex.div.compose.internal

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verifyNoInteractions
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNetwork
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NetworkRestorationControllerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val internetCapabilities = NetworkCapabilities().apply {
        Shadows.shadowOf(this).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    private val coroutineScope = TestScope()
    private val controller = NetworkRestorationController(context, coroutineScope)
    private val retried = mutableListOf<String>()
    private val firstRetry: () -> Unit = { retried.add("first") }
    private val secondRetry: () -> Unit = { retried.add("second") }

    private val shadowConnectivityManager = Shadows.shadowOf(
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    @Test
    fun `does not retry on initial availability delivered during registration`() {
        val network = ShadowNetwork.newInstance(10)
        val connectivityManager = mock<ConnectivityManager> {
            on { allNetworks } doReturn arrayOf(network)
            on { getNetworkCapabilities(network) } doReturn internetCapabilities
            on { registerNetworkCallback(any(), any<ConnectivityManager.NetworkCallback>()) } doAnswer {
                it.getArgument<ConnectivityManager.NetworkCallback>(1).onAvailable(network)
                null
            }
        }
        val context = mock<Context> {
            on { getSystemService(Context.CONNECTIVITY_SERVICE) } doReturn connectivityManager
        }
        val controller = NetworkRestorationController(context, coroutineScope)

        controller.addPendingRetry(firstRetry)
        coroutineScope.runCurrent()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `does not retry for any network already available at registration`() {
        val wifi = addAvailableNetwork(10)
        val mobile = addAvailableNetwork(20)
        controller.addPendingRetry(firstRetry)

        signalNetworkAvailable(wifi)
        signalNetworkAvailable(mobile)
        coroutineScope.runCurrent()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `retries on first available network when registered offline`() {
        shadowConnectivityManager.clearAllNetworks()
        controller.addPendingRetry(firstRetry)

        triggerNetworkAvailable()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `retries for a new network before initial availability is delivered`() {
        addAvailableNetwork(10)
        controller.addPendingRetry(firstRetry)

        signalNetworkAvailable(ShadowNetwork.newInstance(20))
        coroutineScope.runCurrent()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `does not retry a new failure on a delayed initial notification`() {
        val initialNetwork = addAvailableNetwork(10)
        controller.addPendingRetry(firstRetry)
        signalNetworkAvailable(ShadowNetwork.newInstance(20))
        coroutineScope.runCurrent()
        controller.addPendingRetry(firstRetry)

        signalNetworkAvailable(initialNetwork)
        coroutineScope.runCurrent()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `retries when an initially available network is restored`() {
        val network = addAvailableNetwork(10)
        controller.addPendingRetry(firstRetry)
        signalNetworkAvailable(network)
        coroutineScope.runCurrent()
        shadowConnectivityManager.networkCallbacks.forEach { it.onLost(network) }

        signalNetworkAvailable(network)
        coroutineScope.runCurrent()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `retries when an existing network gains internet capability`() {
        val network = addAvailableNetwork(10)
        shadowConnectivityManager.setNetworkCapabilities(network, NetworkCapabilities())
        controller.addPendingRetry(firstRetry)

        signalNetworkAvailable(network)
        coroutineScope.runCurrent()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `does not obtain connectivity manager on creation or removal`() {
        val context = mock<Context>()
        val controller = NetworkRestorationController(context, coroutineScope)

        controller.removePendingRetry(firstRetry)

        verifyNoInteractions(context)
    }

    @Test
    fun `does not deliver queued retries after scope cancellation`() {
        controller.addPendingRetry(firstRetry)
        signalNetworkAvailable()

        coroutineScope.cancel()
        coroutineScope.runCurrent()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `does not register a network callback on creation`() {
        assertEquals(0, shadowConnectivityManager.networkCallbacks.size)
    }

    @Test
    fun `registers exactly one callback for multiple pending retries`() {
        controller.addPendingRetry(firstRetry)
        controller.addPendingRetry(secondRetry)

        assertEquals(1, shadowConnectivityManager.networkCallbacks.size)
    }

    @Test
    fun `does not register another callback after all retries are removed`() {
        controller.addPendingRetry(firstRetry)
        controller.removePendingRetry(firstRetry)

        controller.addPendingRetry(secondRetry)

        assertEquals(1, shadowConnectivityManager.networkCallbacks.size)
    }

    @Test
    fun `retries all waiting consumers when network is available`() {
        controller.addPendingRetry(firstRetry)
        controller.addPendingRetry(secondRetry)

        triggerNetworkAvailable()

        assertEquals(listOf("first", "second"), retried)
    }

    @Test
    fun `does not retry before a network event`() {
        controller.addPendingRetry(firstRetry)

        coroutineScope.runCurrent()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `does not retry removed consumer`() {
        controller.addPendingRetry(firstRetry)
        controller.addPendingRetry(secondRetry)
        controller.removePendingRetry(firstRetry)

        triggerNetworkAvailable()

        assertEquals(listOf("second"), retried)
    }

    @Test
    fun `retries a consumer only once until it registers another error`() {
        controller.addPendingRetry(firstRetry)
        controller.addPendingRetry(firstRetry)
        triggerNetworkAvailable()

        triggerNetworkAvailable()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `keeps a synchronous retry failure for the next network event`() {
        lateinit var retry: () -> Unit
        retry = {
            retried.add("failed")
            controller.addPendingRetry(retry)
        }
        controller.addPendingRetry(retry)
        triggerNetworkAvailable()

        triggerNetworkAvailable()

        assertEquals(listOf("failed", "failed"), retried)
    }

    @Test
    fun `does not replay past network events to a new consumer`() {
        triggerNetworkAvailable()

        controller.addPendingRetry(firstRetry)
        coroutineScope.runCurrent()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `does not retry a consumer removed by another retry`() {
        controller.addPendingRetry { controller.removePendingRetry(secondRetry) }
        controller.addPendingRetry(secondRetry)

        triggerNetworkAvailable()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `retries consumers added before the queued event is processed`() {
        controller.addPendingRetry(firstRetry)
        signalNetworkAvailable()
        controller.addPendingRetry(secondRetry)

        coroutineScope.runCurrent()

        assertEquals(listOf("first", "second"), retried)
    }

    @Test
    fun `retries the latest attempt when the queued event is processed`() {
        controller.addPendingRetry(firstRetry)
        signalNetworkAvailable()
        controller.removePendingRetry(firstRetry)
        controller.addPendingRetry(firstRetry)

        coroutineScope.runCurrent()

        assertEquals(listOf("first"), retried)
    }

    @Test
    fun `does not retry an attempt replaced by another retry in the same batch`() {
        controller.addPendingRetry {
            controller.removePendingRetry(secondRetry)
            controller.addPendingRetry(secondRetry)
        }
        controller.addPendingRetry(secondRetry)

        triggerNetworkAvailable()

        assertEquals(emptyList(), retried)
    }

    @Test
    fun `each controller registers its own callback`() {
        controller.addPendingRetry(firstRetry)
        NetworkRestorationController(context, coroutineScope).addPendingRetry(secondRetry)

        assertEquals(2, shadowConnectivityManager.networkCallbacks.size)
    }

    private fun addAvailableNetwork(id: Int): Network {
        val network = ShadowNetwork.newInstance(id)
        shadowConnectivityManager.addNetwork(network, mock())
        shadowConnectivityManager.setNetworkCapabilities(
            network,
            internetCapabilities,
        )
        return network
    }

    private fun triggerNetworkAvailable() {
        signalNetworkAvailable()
        coroutineScope.runCurrent()
    }

    private fun signalNetworkAvailable(network: Network = ShadowNetwork.newInstance(1)) {
        shadowConnectivityManager.networkCallbacks.toList().forEach { callback ->
            callback.onAvailable(network)
        }
    }
}
