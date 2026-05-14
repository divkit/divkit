package com.yandex.div.compose.views.video

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.ImageLoader
import coil3.asImage
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.internal.ImageLoaderProvider
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@OptIn(DivModelInternalApi::class)
@RunWith(AndroidJUnit4::class)
class DivVideoViewTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val attempts = AtomicInteger(0)

    private val countingImageLoader: ImageLoader = ImageLoader.Builder(context)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .diskCachePolicy(CachePolicy.DISABLED)
        .components {
            add { chain ->
                attempts.incrementAndGet()
                successResult(chain.request)
            }
        }
        .build()

    private val divContext = DivContext(
        baseContext = context,
        configuration = DivComposeConfiguration(reporter = TestReporter()),
        debugConfiguration = DivDebugConfiguration(
            imageLoaderProvider = ImageLoaderProvider { countingImageLoader }
        )
    )

    private val localComponent = mockLocalComponent()

    @Test
    fun `no fetch when preview is null`() {
        val data = videoData(preview = null)

        setContent {
            DivVideoView(modifier = Modifier.size(50.dp), data = data)
        }

        composeRule.waitForIdle()
        assertEquals(0, attempts.get())
    }

    @Test
    fun `single fetch when DivVideo instance changes but preview content stays the same`() {
        var recomposeTrigger by mutableStateOf(0)

        setContent {
            @Suppress("UNUSED_VARIABLE") val touch = recomposeTrigger
            DivVideoView(
                modifier = Modifier.size(50.dp),
                data = videoData(preview = SAMPLE_PREVIEW),
            )
        }

        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        repeat(5) {
            recomposeTrigger++
            composeRule.waitForIdle()
        }

        assertEquals(1, attempts.get())
    }

    @Test
    fun `extra fetch when preview content changes`() {
        var preview by mutableStateOf(SAMPLE_PREVIEW)

        setContent {
            DivVideoView(
                modifier = Modifier.size(50.dp),
                data = videoData(preview = preview),
            )
        }

        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        preview = OTHER_PREVIEW
        composeRule.waitForIdle()

        assertEquals(2, attempts.get())
    }

    @Test
    fun `no fetch when DivVideoView leaves composition`() {
        var visible by mutableStateOf(false)

        setContent {
            if (visible) {
                DivVideoView(
                    modifier = Modifier.size(50.dp),
                    data = videoData(preview = SAMPLE_PREVIEW),
                )
            }
        }

        composeRule.waitForIdle()
        assertEquals(0, attempts.get())

        visible = true
        composeRule.waitForIdle()
        assertEquals(1, attempts.get())

        visible = false
        composeRule.waitForIdle()
        assertEquals(1, attempts.get())
    }

    private fun successResult(request: ImageRequest): SuccessResult {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
        return SuccessResult(image = bitmap.asImage(), request = request)
    }

    private fun setContent(content: @Composable () -> Unit) {
        composeRule.setContent {
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalComponent provides localComponent,
            ) {
                content()
            }
        }
    }

    private fun videoData(
        preview: String? = null,
        scale: DivVideoScale = DivVideoScale.FIT,
    ): DivVideo = DivVideo(
        preview = preview?.let { Expression.constant(it) },
        scale = Expression.constant(scale),
    )

    companion object {
        private const val SAMPLE_PREVIEW = "AAAA"
        private const val OTHER_PREVIEW = "BBBB"
    }
}
