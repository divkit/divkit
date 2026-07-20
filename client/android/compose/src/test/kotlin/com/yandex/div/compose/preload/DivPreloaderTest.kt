package com.yandex.div.compose.preload

import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.yandex.div.compose.DivComposeConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.PreloadMode
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.image
import com.yandex.div.test.data.separator
import com.yandex.div.test.data.state
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div.test.data.video
import com.yandex.div.test.data.videoSource
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import com.yandex.div2.DivState
import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class DivPreloaderTest {

    private val extensionIds = mutableListOf<String>()
    private val videoUrls = mutableListOf<List<Uri>>()
    private val imageLoads = mutableListOf<Uri>()

    private val imagePreloader = object : ImagePreloader {
        override suspend fun preloadImages(
            div: Div,
            resolver: ExpressionResolver,
            downloadAll: Boolean,
        ) {
            if (div !is Div.Image) return
            if (!downloadAll && !div.value.preloadRequired.evaluate(resolver)) return
            imageLoads.add(div.value.imageUrl.evaluate(resolver))
        }
    }

    private val extensionHandler = object : DivExtensionHandler {
        @androidx.compose.runtime.Composable
        override fun Content(
            modifier: Modifier,
            environment: DivExtensionEnvironment,
            content: @androidx.compose.runtime.Composable (Modifier) -> Unit
        ) = Unit

        override suspend fun preload(environment: DivExtensionEnvironment) {
            extensionIds.add(environment.extension.id)
        }
    }

    private val divContext = DivContext(
        baseContext = RuntimeEnvironment.getApplication(),
        configuration = DivComposeConfiguration(
            extensionHandlers = mapOf(
                "lottie" to extensionHandler,
                "other" to extensionHandler,
                "tag" to extensionHandler,
            ),
        )
    )

    private val preloader = DivPreloader(
        imagePreloader = imagePreloader,
        customPreloader = CustomResourcePreloader(emptyMap()),
        extensionPreloader = ExtensionPreloader(
            handlers = mapOf(
                "lottie" to extensionHandler,
                "other" to extensionHandler,
                "tag" to extensionHandler,
            ),
            reporter = TestReporter(),
        ),
        videoPreloader = object : DivVideoPreloader {
            override suspend fun preloadVideo(sources: List<Uri>) {
                videoUrls.add(sources)
            }
        },
        viewContextFactory = divContext.component.viewContextFactory,
    )

    @Test
    fun `preloads video when preloadRequired is true`() = runTest {
        val videoUri = "https://example.com/v.mp4".toUri()
        val data = data(
            content = video(
                preloadRequired = constant(true),
                videoSources = listOf(videoSource(url = constant(videoUri))),
            )
        )
        preloader.preload(data)
        assertEquals(listOf(listOf(videoUri)), videoUrls)
    }

    @Test
    fun `does not preload video when preloadRequired is false`() = runTest {
        val data = data(
            content = video(
                preloadRequired = constant(false),
                videoSources = listOf(
                    videoSource(url = constant("https://example.com/v.mp4".toUri()))
                ),
            )
        )
        preloader.preload(data)
        assertEquals(emptyList(), videoUrls)
    }

    @Test
    fun `preload with activeStateOnly preloads video when preloadRequired is false`() = runTest {
        val videoUri = "https://example.com/v.mp4".toUri()
        val data = data(
            content = video(
                preloadRequired = constant(false),
                videoSources = listOf(videoSource(url = constant(videoUri))),
            )
        )
        preloader.preload(data, PreloadMode.ACTIVE_STATE_ONLY)
        assertEquals(listOf(listOf(videoUri)), videoUrls)
    }

    @Test
    fun `preload with activeStateOnly loads images when preloadRequired is false`() = runTest {
        val imageUrl = "https://example.com/img.jpg".toUri()
        preloader.preload(
            data(
                content = image(
                    imageUrl = constant(imageUrl),
                    preloadRequired = constant(false),
                )
            ),
            PreloadMode.ACTIVE_STATE_ONLY,
        )
        assertEquals(listOf(imageUrl), imageLoads)
    }

    @Test
    fun `preload does not load images when preloadRequired is false`() = runTest {
        preloader.preload(
            data(
                content = image(
                    imageUrl = constant("https://example.com/img.jpg".toUri()),
                    preloadRequired = constant(false),
                )
            )
        )
        assertEquals(emptyList(), imageLoads)
    }

    @Test
    fun `preload with activeStateOnly visits only first root state`() = runTest {
        val image1Url = "https://example.com/state0.jpg".toUri()
        val image2Url = "https://example.com/state1.jpg".toUri()
        preloader.preload(
            data(
                states = listOf(
                    DivData.State(
                        stateId = 0,
                        div = image(
                            imageUrl = constant(image1Url),
                            preloadRequired = constant(false),
                        )
                    ),
                    DivData.State(
                        stateId = 1,
                        div = image(
                            imageUrl = constant(image2Url),
                            preloadRequired = constant(false),
                        )
                    ),
                )
            ),
            PreloadMode.ACTIVE_STATE_ONLY,
        )
        assertEquals(listOf(image1Url), imageLoads)
    }

    @Test
    fun `preload with activeStateOnly visits only active div-state variant`() = runTest {
        val image1Url = "https://example.com/s1.jpg".toUri()
        val image2Url = "https://example.com/s2.jpg".toUri()
        preloader.preload(
            data(
                content = Div.State(
                    state(
                        id = "id",
                        defaultStateId = constant("s1"),
                        states = listOf(
                            DivState.State(
                                stateId = "s1",
                                div = image(
                                    imageUrl = constant(image1Url),
                                    preloadRequired = constant(false),
                                )
                            ),
                            DivState.State(
                                stateId = "s2",
                                div = image(
                                    imageUrl = constant(image2Url),
                                    preloadRequired = constant(false),
                                )
                            ),
                        )
                    )
                )
            ),
            PreloadMode.ACTIVE_STATE_ONLY,
        )
        assertEquals(listOf(image1Url), imageLoads)
    }

    @Test
    fun `calls extension preload for each extension`() = runTest {
        val data = data(
            content = separator(
                extensions = listOf(
                    DivExtension(id = "lottie"),
                    DivExtension(id = "other")
                )
            )
        )
        preloader.preload(data)
        assertEquals(listOf("lottie", "other"), extensionIds)
    }

    @Test
    fun `preloads all div-data states`() = runTest {
        val taggedSep = separator(extensions = listOf(DivExtension(id = "tag")))
        val data = data(
            states = listOf(
                DivData.State(stateId = 0, div = taggedSep),
                DivData.State(stateId = 1, div = taggedSep),
            ),
        )
        preloader.preload(data)
        assertEquals(2, extensionIds.size)
    }

    @Test
    fun `visits all div-state variants`() = runTest {
        val taggedSep = separator(extensions = listOf(DivExtension(id = "tag")))
        val data = data(
            content = Div.State(
                state(
                    id = "id",
                    states = listOf(
                        DivState.State(stateId = "s1", div = taggedSep),
                        DivState.State(stateId = "s2", div = taggedSep),
                    )
                )
            )
        )
        preloader.preload(data)
        assertEquals(2, extensionIds.size)
    }

    @Test
    fun `passes expression resolver to image preloader`() = runTest {
        val data = data(
            content = image(
                imageUrl = constant("https://example.com/img.jpg".toUri()),
                preloadRequired = constant(true),
            )
        )
        preloader.preload(data)
        assertEquals(1, imageLoads.size)
    }

    @Test
    fun `resolves image url from local variable in container`() = runTest {
        val localUrl = "https://example.com/local.jpg".toUri()
        val data = data(
            content = container(
                variables = listOf(variable("img_url", localUrl.toString())),
                items = listOf(
                    image(
                        imageUrl = uriExpression("@{img_url}"),
                        preloadRequired = constant(true),
                    )
                ),
            )
        )
        preloader.preload(data)
        assertEquals(localUrl, imageLoads.single())
    }
}
