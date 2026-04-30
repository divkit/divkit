package com.yandex.div.compose.images

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.asImage
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.network.HttpException
import coil3.network.NetworkResponse
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.ImageResult
import coil3.request.SuccessResult
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.internal.NetworkRestorationController
import com.yandex.div.compose.mockLocalComponent
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.shadows.ShadowConnectivityManager
import org.robolectric.shadows.ShadowNetwork
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class ImageNetworkRestorationTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val shadowConnectivityManager: ShadowConnectivityManager = Shadows.shadowOf(
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val attempts = AtomicInteger(0)

    @Volatile
    private var nextResult: (ImageRequest) -> ImageResult = { networkErrorResult(it) }

    private val localComponent = mockLocalComponent(
        networkRestorationController = NetworkRestorationController(context)
    )

    private val imageLoader: ImageLoader = ImageLoader.Builder(context)
        .components {
            add { chain ->
                attempts.incrementAndGet()
                nextResult(chain.request)
            }
        }
        .build()

    @Test
    fun `restarts on UnknownHostException`() = expectRestart {
        networkErrorResult(it)
    }

    @Test
    fun `restarts on HTTP 504`() = expectRestart {
        httpErrorResult(it, code = 504)
    }

    @Test
    fun `restarts on HTTP 408`() = expectRestart {
        httpErrorResult(it, code = 408)
    }

    @Test
    fun `does not restart on HTTP 500`() = expectNoRestart {
        httpErrorResult(it, code = 500)
    }

    @Test
    fun `does not restart on HTTP 404`() = expectNoRestart {
        httpErrorResult(it, code = 404)
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
    private fun rememberObservedPainter(): AsyncImagePainter {
        val painter = rememberAsyncImagePainter(
            model = "https://example/img",
            imageLoader = imageLoader,
        )
        painter.observeNetworkRestoration()
        Image(painter = painter, contentDescription = null)
        return painter
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
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
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        return SuccessResult(image = bitmap.asImage(), request = request)
    }

    private fun networkErrorResult(request: ImageRequest): ErrorResult =
        ErrorResult(
            image = null,
            request = request,
            throwable = UnknownHostException("offline"),
        )

    private fun httpErrorResult(request: ImageRequest, code: Int): ErrorResult =
        ErrorResult(
            image = null,
            request = request,
            throwable = HttpException(NetworkResponse().copy(code = code)),
        )
}
