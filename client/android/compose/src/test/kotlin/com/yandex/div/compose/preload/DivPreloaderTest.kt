package com.yandex.div.compose.preload

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.PreloadMode
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.video.DivVideoPreloader
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
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
import com.yandex.div2.DivImage
import com.yandex.div2.DivState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.yield
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
class DivPreloaderTest {
    private val extensionIds = mutableListOf<String>()
    private val videoUrls = mutableListOf<List<Uri>>()
    private val imageLoads = mutableListOf<String>()
    private val customTypes = mutableListOf<String>()
    private var imageResult = true
    private var extensionResult = true
    private var videoResult = true
    private var customResult = true
    private var beforeImagePreload: suspend (Div) -> Unit = {}
    private var beforeExtensionPreload: suspend () -> Unit = {}

    private val imagePreloader = object : ImagePreloader {
        override suspend fun preloadImages(
            div: Div,
            resolver: ExpressionResolver,
            downloadAll: Boolean,
        ): PreloadResult {
            beforeImagePreload(div)
            when (div) {
                is Div.Image -> {
                    if (downloadAll || div.value.preloadRequired.evaluate(resolver)) {
                        div.value.imageUrl?.let {
                            imageLoads.add(it.evaluate(resolver).toString())
                        }
                    }
                }

                else -> Unit
            }
            return PreloadResult(imageResult)
        }
    }

    private val extensionHandler = object : DivExtensionHandler {
        @Composable
        override fun Content(
            modifier: Modifier,
            environment: DivExtensionEnvironment,
            content: @Composable (Modifier) -> Unit
        ) = Unit

        override suspend fun preloadWithResult(environment: DivExtensionEnvironment): PreloadResult {
            extensionIds.add(environment.extension.id)
            beforeExtensionPreload()
            return PreloadResult(extensionResult)
        }
    }

    private val customFactory = object : DivCustomViewFactory {
        @Composable
        override fun Content(modifier: Modifier, environment: DivCustomEnvironment) = Unit

        override suspend fun preloadWithResult(environment: DivCustomEnvironment): PreloadResult {
            customTypes.add(environment.data.customType)
            return PreloadResult(customResult)
        }
    }

    private val divContext = DivContext(
        baseContext = RuntimeEnvironment.getApplication(),
        configuration = DivConfiguration(
            customViewFactories = mapOf("test" to customFactory),
            extensionHandlers = mapOf(
                "lottie" to extensionHandler,
                "other" to extensionHandler,
                "tag" to extensionHandler,
            ),
        )
    )

    private val preloader = DivPreloader(
        imagePreloader = imagePreloader,
        customPreloader = CustomResourcePreloader(mapOf("test" to customFactory)),
        extensionPreloader = ExtensionPreloader(
            handlers = mapOf(
                "lottie" to extensionHandler,
                "other" to extensionHandler,
                "tag" to extensionHandler,
            ),
            reporter = TestReporter(),
            animationConfiguration = divContext.component.animationConfiguration,
        ),
        videoPreloader = object : DivVideoPreloader {
            override suspend fun preloadVideoWithResult(sources: List<Uri>): PreloadResult {
                videoUrls.add(sources)
                return PreloadResult(videoResult)
            }
        },
        viewContextFactory = divContext.component.viewContextFactory,
    )

    @Test
    fun `returns true when every preload succeeds`() = runTest {
        val result = preloader.preload(
            data(
                content = imageWithExtension("https://example.com/img.jpg")
            )
        )

        assertTrue(result.isSuccessful)
    }

    @Test
    fun `waits for sibling preload after another preload fails`() = runTest {
        imageResult = false
        val extensionCanFinish = CompletableDeferred<Unit>()
        beforeExtensionPreload = { extensionCanFinish.await() }

        val result = async {
            preloader.preload(
                data(
                    content = imageWithExtension("https://example.com/img.jpg")
                )
            )
        }
        yield()

        assertFalse(result.isCompleted)
        extensionCanFinish.complete(Unit)
        assertFalse(result.await().isSuccessful)
        assertEquals(listOf("lottie"), extensionIds)
    }

    @Test
    fun `returns true when no resources match`() = runTest {
        val result = preloader.preload(data(content = separator()))

        assertTrue(result.isSuccessful)
    }

    @Test
    fun `returns true when matching resource is not selected`() = runTest {
        val result = preloader.preload(
            data(
                content = image(
                    imageUrl = "https://example.com/img.jpg",
                    preloadRequired = false,
                )
            )
        )

        assertTrue(result.isSuccessful)
        assertEquals(emptyList(), imageLoads)
    }

    @Test
    fun `returns true without loading when preload is disabled`() = runTest {
        val result = preloader.preload(
            data(
                content = image(
                    imageUrl = "https://example.com/img.jpg",
                    preloadRequired = true,
                )
            ),
            PreloadMode.DISABLED,
        )

        assertTrue(result.isSuccessful)
        assertEquals(emptyList(), imageLoads)
    }

    @Test
    fun `returns false when video preload fails`() = runTest {
        videoResult = false
        val videoUri = "https://example.com/v.mp4".toUri()

        val result = preloader.preload(
            data(
                content = video(
                    preloadRequired = constant(true),
                    videoSources = listOf(videoSource(url = constant(videoUri))),
                )
            )
        )

        assertFalse(result.isSuccessful)
    }

    @Test
    fun `returns false when extension preload fails`() = runTest {
        extensionResult = false

        val result = preloader.preload(
            data(content = separator(extensions = listOf(DivExtension(id = "lottie"))))
        )

        assertFalse(result.isSuccessful)
    }

    @Test
    fun `returns false when custom preload fails`() = runTest {
        customResult = false

        val result = preloader.preload(data(content = custom(type = "test")))

        assertFalse(result.isSuccessful)
        assertEquals(listOf("test"), customTypes)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `result preload calls legacy extension preload by default`() = runTest {
        var legacyPreloadCalled = false
        val legacyHandler = object : DivExtensionHandler {
            @Composable
            override fun Content(
                modifier: Modifier,
                environment: DivExtensionEnvironment,
                content: @Composable (Modifier) -> Unit,
            ) = Unit

            @Suppress("OVERRIDE_DEPRECATION")
            override suspend fun preload(environment: DivExtensionEnvironment) {
                legacyPreloadCalled = true
            }
        }
        val legacyPreloader = ExtensionPreloader(
            handlers = mapOf("legacy" to legacyHandler),
            reporter = TestReporter(),
            animationConfiguration = divContext.component.animationConfiguration,
        )

        val result = legacyPreloader.preloadExtensions(
            separator(extensions = listOf(DivExtension(id = "legacy"))),
            ExpressionResolver.EMPTY,
        )

        assertTrue(result.isSuccessful)
        assertTrue(legacyPreloadCalled)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `result preload calls legacy custom preload by default`() = runTest {
        var legacyPreloadCalled = false
        val legacyFactory = object : DivCustomViewFactory {
            @Composable
            override fun Content(modifier: Modifier, environment: DivCustomEnvironment) = Unit

            @Suppress("OVERRIDE_DEPRECATION")
            override suspend fun preload(environment: DivCustomEnvironment) {
                legacyPreloadCalled = true
            }
        }
        val custom = custom(type = "legacy") as Div.Custom
        val environment = DivCustomEnvironment(
            data = custom.value,
            expressionResolver = ExpressionResolver.EMPTY,
            items = {},
            item = { _, _ -> },
        )

        val result = CustomResourcePreloader(mapOf("legacy" to legacyFactory)).preload(environment)

        assertTrue(result.isSuccessful)
        assertTrue(legacyPreloadCalled)
    }

    @Suppress("DEPRECATION")
    @Test
    fun `result preload calls legacy video preload by default`() = runTest {
        var legacyPreloadCalled = false
        val source = "https://example.com/legacy.mp4".toUri()
        val legacyPreloader = object : DivVideoPreloader {
            @Suppress("OVERRIDE_DEPRECATION")
            override suspend fun preloadVideo(sources: List<Uri>) {
                legacyPreloadCalled = true
            }
        }

        val result = legacyPreloader.preloadVideoWithResult(listOf(source))

        assertTrue(result.isSuccessful)
        assertTrue(legacyPreloadCalled)
    }

    @Test
    fun `propagates resource cancellation and cancels outstanding work`() = runTest {
        val imageCancelled = CompletableDeferred<Unit>()
        beforeImagePreload = {
            try {
                awaitCancellation()
            } finally {
                imageCancelled.complete(Unit)
            }
        }
        beforeExtensionPreload = { throw CancellationException("extension cancelled") }

        val error = assertFailsWith<CancellationException> {
            preloader.preload(
                data(
                    content = imageWithExtension("https://example.com/img.jpg")
                )
            )
        }

        assertEquals("extension cancelled", error.message)
        assertTrue(imageCancelled.isCompleted)
    }

    @Test
    fun `parent resource cancellation promptly cancels hanging descendant`() = runTest {
        val descendantStarted = CompletableDeferred<Unit>()
        val descendantCancelled = CompletableDeferred<Unit>()
        beforeImagePreload = { div ->
            if (div is Div.Image) {
                descendantStarted.complete(Unit)
                try {
                    awaitCancellation()
                } finally {
                    descendantCancelled.complete(Unit)
                }
            }
        }
        beforeExtensionPreload = {
            descendantStarted.await()
            throw CancellationException("parent resource cancelled")
        }

        val error = assertFailsWith<CancellationException> {
            withTimeout(1_000) {
                preloader.preload(
                    data(
                        content = container(
                            extensions = listOf(DivExtension(id = "lottie")),
                            items = listOf(
                                image(
                                    imageUrl = "https://example.com/img.jpg",
                                    preloadRequired = true,
                                )
                            ),
                        )
                    )
                )
            }
        }

        assertEquals("parent resource cancelled", error.message)
        assertTrue(descendantCancelled.isCompleted)
    }

    @Test
    fun `caller cancellation cancels outstanding work`() = runTest {
        val imageStarted = CompletableDeferred<Unit>()
        val imageCancelled = CompletableDeferred<Unit>()
        beforeImagePreload = {
            imageStarted.complete(Unit)
            try {
                awaitCancellation()
            } finally {
                imageCancelled.complete(Unit)
            }
        }
        val job = launch {
            preloader.preload(
                data(
                    content = image(
                        imageUrl = "https://example.com/img.jpg",
                        preloadRequired = true,
                    )
                )
            )
        }

        imageStarted.await()
        job.cancelAndJoin()

        assertTrue(imageCancelled.isCompleted)
    }

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
        val imageUrl = "https://example.com/img.jpg"
        preloader.preload(
            data(
                content = image(
                    imageUrl = imageUrl,
                    preloadRequired = false
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
                    imageUrl = "https://example.com/img.jpg",
                    preloadRequired = false
                )
            )
        )
        assertEquals(emptyList(), imageLoads)
    }

    @Test
    fun `preload with activeStateOnly visits only first root state`() = runTest {
        val image1Url = "https://example.com/state0.jpg"
        val image2Url = "https://example.com/state1.jpg"
        preloader.preload(
            data(
                states = listOf(
                    DivData.State(
                        stateId = 0,
                        div = image(
                            imageUrl = image1Url,
                            preloadRequired = false
                        )
                    ),
                    DivData.State(
                        stateId = 1,
                        div = image(
                            imageUrl = image2Url,
                            preloadRequired = false
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
        val image1Url = "https://example.com/s1.jpg"
        val image2Url = "https://example.com/s2.jpg"
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
                                    imageUrl = image1Url,
                                    preloadRequired = false
                                )
                            ),
                            DivState.State(
                                stateId = "s2",
                                div = image(
                                    imageUrl = image2Url,
                                    preloadRequired = false
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
                imageUrl = "https://example.com/img.jpg",
                preloadRequired = true
            )
        )
        preloader.preload(data)
        assertEquals(1, imageLoads.size)
    }

    @Test
    fun `resolves image url from local variable in container`() = runTest {
        val localUrl = "https://example.com/local.jpg"
        val data = data(
            content = container(
                variables = listOf(variable("img_url", localUrl)),
                items = listOf(
                    image(
                        imageUrl = uriExpression("@{img_url}"),
                        preloadRequired = true
                    )
                ),
            )
        )
        preloader.preload(data)

        assertEquals(localUrl, imageLoads.single())
    }

    private fun imageWithExtension(url: String): Div {
        return Div.Image(
            DivImage(
                extensions = listOf(DivExtension(id = "lottie")),
                imageUrl = constant(url.toUri()),
                preloadRequired = constant(true),
            )
        )
    }
}
