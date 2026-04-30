package com.yandex.div.compose.internal

import android.content.Context
import android.net.ConnectivityManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetwork

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class NetworkRestorationControllerTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val shadowConnectivityManager: ShadowConnectivityManager = Shadows.shadowOf(
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    @Test
    fun `registers exactly one network callback on creation`() {
        NetworkRestorationController(context)

        assertEquals(1, shadowConnectivityManager.networkCallbacks.size)
    }

    @Test
    fun `emits networkRestored when system reports onAvailable`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val controller = NetworkRestorationController(context)
        val received = mutableListOf<Unit>()
        val job = launch { controller.networkRestored.toList(received) }

        triggerNetworkAvailable()

        assertEquals(1, received.size)
        job.cancel()
    }

    @Test
    fun `emits separately for each onAvailable event`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val controller = NetworkRestorationController(context)
        val received = mutableListOf<Unit>()
        val job = launch { controller.networkRestored.toList(received) }

        triggerNetworkAvailable()
        triggerNetworkAvailable()
        triggerNetworkAvailable()

        assertEquals(3, received.size)
        job.cancel()
    }

    @Test
    fun `does not emit before any onAvailable event`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val controller = NetworkRestorationController(context)
        val received = mutableListOf<Unit>()
        val job = launch { controller.networkRestored.toList(received) }

        assertTrue(received.isEmpty())
        job.cancel()
    }

    @Test
    fun `multiple subscribers all receive the same event`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val controller = NetworkRestorationController(context)
        val firstReceived = mutableListOf<Unit>()
        val secondReceived = mutableListOf<Unit>()
        val job1 = launch { controller.networkRestored.toList(firstReceived) }
        val job2 = launch { controller.networkRestored.toList(secondReceived) }

        triggerNetworkAvailable()

        assertEquals(1, firstReceived.size)
        assertEquals(1, secondReceived.size)
        job1.cancel()
        job2.cancel()
    }

    @Test
    fun `late subscriber does not receive earlier events`() = runTest(
        UnconfinedTestDispatcher()
    ) {
        val controller = NetworkRestorationController(context)

        triggerNetworkAvailable()
        triggerNetworkAvailable()

        val received = mutableListOf<Unit>()
        val job = launch { controller.networkRestored.toList(received) }

        assertTrue(received.isEmpty())

        triggerNetworkAvailable()

        assertEquals(1, received.size)
        job.cancel()
    }

    @Test
    fun `each controller registers its own callback`() {
        NetworkRestorationController(context)
        NetworkRestorationController(context)

        assertEquals(2, shadowConnectivityManager.networkCallbacks.size)
    }

    private fun triggerNetworkAvailable() {
        val network = ShadowNetwork.newInstance(1)
        shadowConnectivityManager.networkCallbacks.toList().forEach { callback ->
            callback.onAvailable(network)
        }
    }
}
