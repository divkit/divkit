package com.yandex.div.compose.images

import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.core.graphics.createBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ComponentRegistry
import coil3.asImage
import coil3.compose.AsyncImagePainter
import coil3.network.HttpException
import coil3.network.NetworkResponse
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.network.NetworkRestorationController
import com.yandex.div.compose.setContentWithDivContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowNetwork
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ImageNetworkRestorationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val attempts = AtomicInteger(0)
    private val context: Context = ApplicationProvider.getApplicationContext()

    private val shadowConnectivityManager = Shadows.shadowOf(
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val localComponent = mockLocalComponent(
        networkRestorationController = NetworkRestorationController(
            context = context,
            mainCoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
        )
    )

    @Volatile
    private var nextResult: (ImageRequest) -> ImageResult = { networkErrorResult(it) }

    private var loadingGate: CompletableDeferred<Unit>? = null

    private val imageLoaderConfiguration = object : ImageLoaderConfiguration {
        override val reportErrors = false

        override fun applyComponents(builder: ComponentRegistry.Builder) {
            builder.add { chain ->
                attempts.incrementAndGet()
                loadingGate?.await()
                nextResult(chain.request)
            }
        }
    }

    @Test
    fun `restarts on UnknownHostException`() = expectRestart {
        networkErrorResult(it)
    }

    @Test
    fun `restarts on HTTP error result`() = expectRestart {
        ErrorResult(
            image = null,
            request = it,
            throwable = HttpException(NetworkResponse()),
        )
    }

    @Test
    fun `does not restart on non-network error`() = expectNoRestart {
        ErrorResult(
            image = null,
            request = it,
            throwable = IllegalStateException("decoding failed"),
        )
    }

    @Test
    fun `does not restart when painter is in success state`() {
        nextResult = ::successResult
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(1, attempts.get())
    }

    @Test
    fun `restarts on every network event while still in network error`() {
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()

        triggerNetworkAvailable()
        composeRule.waitForIdle()
        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(3, attempts.get())
    }

    @Test
    fun `stops observing when painter leaves composition`() {
        val visible = mutableStateOf(true)
        setContent {
            if (visible.value) rememberObservedPainter()
        }
        composeRule.waitForIdle()
        val attemptsBefore = attempts.get()

        visible.value = false
        composeRule.waitForIdle()
        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(attemptsBefore, attempts.get())
    }

    @Test
    fun `does not restart an in-flight retry on another network event`() {
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()
        loadingGate = CompletableDeferred()
        triggerNetworkAvailable()
        composeRule.waitForIdle()

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(2, attempts.get())
    }

    @Test
    fun `does not restart after retry succeeds`() {
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()
        nextResult = ::successResult
        triggerNetworkAvailable()
        composeRule.waitForIdle()

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(2, attempts.get())
    }

    @Test
    fun `does not restart a new loading model because the old model failed`() {
        val model = mutableStateOf("https://example/old")
        setContent { rememberObservedPainter(model.value) }
        composeRule.waitForIdle()
        loadingGate = CompletableDeferred()
        model.value = "https://example/new"
        composeRule.waitForIdle()

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(2, attempts.get())
    }

    @Test
    fun `retries a new model after it fails`() {
        val model = mutableStateOf("https://example/old")
        setContent { rememberObservedPainter(model.value) }
        composeRule.waitForIdle()
        model.value = "https://example/new"
        composeRule.waitForIdle()
        val retriedModels = mutableListOf<String>()
        nextResult = { request ->
            retriedModels.add(request.data.toString())
            networkErrorResult(request)
        }

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(listOf("https://example/new"), retriedModels)
    }

    @Test
    fun `forwards retry state to the current state listener after recomposition`() {
        val received = mutableListOf<String>()
        val listener = mutableStateOf("old")
        setContent {
            val name = listener.value
            rememberObservedPainter(onState = { state ->
                if (state is AsyncImagePainter.State.Success) received.add(name)
            })
        }
        composeRule.waitForIdle()
        listener.value = "new"
        composeRule.waitForIdle()
        nextResult = ::successResult

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(listOf("new"), received)
    }

    @Test
    fun `retries every failed painter sharing one controller`() {
        val retriedModels = mutableListOf<String>()
        setContent {
            rememberObservedPainter("https://example/first")
            rememberObservedPainter("https://example/second")
        }
        composeRule.waitForIdle()
        nextResult = { request ->
            retriedModels.add(request.data.toString())
            networkErrorResult(request)
        }

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(listOf("https://example/first", "https://example/second"), retriedModels.sorted())
    }

    private fun expectRestart(error: (ImageRequest) -> ErrorResult) {
        nextResult = error
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(2, attempts.get())
    }

    private fun expectNoRestart(error: (ImageRequest) -> ErrorResult) {
        nextResult = error
        setContent { rememberObservedPainter() }
        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        triggerNetworkAvailable()
        composeRule.waitForIdle()

        assertEquals(1, attempts.get())
    }

    @Composable
    private fun rememberObservedPainter(
        model: String = "https://example/img",
        onState: ((AsyncImagePainter.State) -> Unit)? = null,
    ): AsyncImagePainter {
        val painter = rememberNetworkRestoringImagePainter(
            model = model,
            onState = onState,
        )
        Image(painter = painter, contentDescription = null)
        return painter
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContentWithDivContext(
            configuration = divConfiguration {
                imageLoaderConfiguration = this@ImageNetworkRestorationTest.imageLoaderConfiguration
            }
        ) {
            whenever(localComponent.imageLoader) doReturn LocalDivContext.current.component.imageLoader
            CompositionLocalProvider(LocalComponent provides localComponent) {
                content()
            }
        }
    }

    private fun triggerNetworkAvailable() {
        val network = ShadowNetwork.newInstance(1)
        shadowConnectivityManager.networkCallbacks.toList().forEach { callback ->
            callback.onAvailable(network)
        }
    }

    private fun successResult(request: ImageRequest): SuccessResult {
        return SuccessResult(
            image = createBitmap(1, 1).asImage(),
            request = request,
        )
    }

    private fun networkErrorResult(request: ImageRequest): ErrorResult {
        return ErrorResult(
            image = null,
            request = request,
            throwable = UnknownHostException("offline"),
        )
    }
}
