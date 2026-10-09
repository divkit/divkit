@file:OptIn(coil3.annotation.ExperimentalCoilApi::class)

package com.yandex.div.compose.views.image

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.core.graphics.createBitmap
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.asImage
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.request.ImageRequest
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestImageLoaderConfiguration
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.data
import com.yandex.div.test.data.image
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Rule
import org.junit.runner.RunWith
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DivImageContentTest {

    @get:Rule
    val rule = createComposeRule()

    private val testScope = CoroutineScope(UnconfinedTestDispatcher())
    private val imageLoaderConfiguration = TestImageLoaderConfiguration()
    private val imageLoadedCapture = ImageLoadedCaptureExtensionHandler()
    private val reporter = TestReporter()
    private val captureExtension = DivExtension(id = CAPTURE_EXTENSION_ID)

    @Test
    fun `retained success updates storage consumed by extension after data rebind`() {
        val initialImage = imageWithCaptureExtension("Initial image")
        val replacementImage = imageWithCaptureExtension("Replacement image")
        val initialData = data(initialImage)
        val replacementData = data(replacementImage).copy(logId = "replacement")
        val divContext = createDivContext()
        val initialViewContext = divContext.getViewContext(initialData)
        val replacementViewContext = divContext.getViewContext(replacementData)
        val currentImage = mutableStateOf(initialImage, referentialEqualityPolicy())
        val currentImageUrl = mutableStateOf(IMAGE_URL)
        val currentViewContext = mutableStateOf(initialViewContext, referentialEqualityPolicy())

        setImageContent(divContext, currentImage, currentImageUrl, currentViewContext)
        rule.waitUntil {
            imageLoadedCapture.isLoaded(initialImage.value)
        }
        rule.runOnIdle {
            assertEquals(1, imageLoaderConfiguration.capturedRequests.size)
            assertFalse(
                replacementViewContext.component.imageStateStorage.isLoaded(replacementImage.value)
            )
        }

        rule.runOnIdle {
            currentImage.value = replacementImage
            currentViewContext.value = replacementViewContext
        }
        rule.waitForIdle()
        rule.runOnIdle {
            assertTrue(
                replacementViewContext.component.imageStateStorage.isLoaded(replacementImage.value)
            )
            assertTrue(imageLoadedCapture.isLoaded(replacementImage.value))
            assertEquals(1, imageLoaderConfiguration.capturedRequests.size)
        }
    }

    @Test
    fun `stale success does not mark replacement loaded before new request succeeds`() {
        val controlledPreviewHandler = ControlledImagePreviewHandler(REPLACEMENT_IMAGE_URL)
        val image = imageWithCaptureExtension("Image")
        val divContext = createDivContext()
        val viewContext = divContext.getViewContext(data(image))
        val currentImage = mutableStateOf(image, referentialEqualityPolicy())
        val currentImageUrl = mutableStateOf(IMAGE_URL)
        val currentViewContext = mutableStateOf(viewContext, referentialEqualityPolicy())
        var previewInvoked = false
        var previewInvokedBeforeFirstReplacementCommit: Boolean? = null

        setImageContent(
            divContext = divContext,
            currentImage = currentImage,
            currentImageUrl = currentImageUrl,
            currentViewContext = currentViewContext,
            previewHandler = controlledPreviewHandler.handler,
            preview = {
                previewInvoked = true
                null
            },
            onCompositionCommitted = { imageUrl ->
                if (imageUrl == REPLACEMENT_IMAGE_URL &&
                    previewInvokedBeforeFirstReplacementCommit == null
                ) {
                    previewInvokedBeforeFirstReplacementCommit = previewInvoked
                }
            },
        )
        rule.waitUntil {
            imageLoadedCapture.isLoaded(image.value)
        }
        rule.runOnIdle {
            assertTrue(controlledPreviewHandler.hasObserved(IMAGE_URL))
            assertTrue(viewContext.component.imageStateStorage.isLoaded(image.value))
            previewInvoked = false
        }

        rule.runOnIdle {
            currentImageUrl.value = REPLACEMENT_IMAGE_URL
        }
        rule.waitForIdle()
        rule.waitUntil {
            controlledPreviewHandler.hasObserved(REPLACEMENT_IMAGE_URL)
        }
        rule.runOnIdle {
            assertTrue(previewInvoked)
            assertEquals(true, previewInvokedBeforeFirstReplacementCommit)
            assertTrue(imageLoadedCapture.isObserving(image.value))
            assertFalse(imageLoadedCapture.isCurrentImageLoaded)
            assertFalse(viewContext.component.imageStateStorage.isLoaded(image.value))
        }

        controlledPreviewHandler.completeBlockedLoad()
        rule.waitUntil {
            viewContext.component.imageStateStorage.isLoaded(image.value)
        }
        rule.runOnIdle {
            assertTrue(imageLoadedCapture.isLoaded(image.value))
        }
    }

    private fun setImageContent(
        divContext: DivContext,
        currentImage: State<Div.Image>,
        currentImageUrl: State<String>,
        currentViewContext: State<DivViewContext>,
        previewHandler: AsyncImagePreviewHandler? = null,
        preview: @Composable () -> Any? = { null },
        onCompositionCommitted: (imageUrl: String) -> Unit = {},
    ) {
        rule.setContent {
            val image = currentImage.value
            val imageUrl = currentImageUrl.value
            val viewContext = currentViewContext.value
            val actualPreviewHandler = previewHandler ?: LocalAsyncImagePreviewHandler.current
            CompositionLocalProvider(
                LocalContext provides divContext,
                LocalDivViewContext provides viewContext,
                LocalComponent provides viewContext.rootLocalComponent,
                LocalInspectionMode provides (previewHandler != null),
                LocalAsyncImagePreviewHandler provides actualPreviewHandler,
            ) {
                imageLoadedCapture.Content(
                    modifier = Modifier,
                    environment = DivExtensionEnvironment(
                        data = image,
                        extension = captureExtension,
                        expressionResolver = viewContext.rootLocalComponent.expressionResolver,
                        reporter = reporter,
                        animationsEnabled = false,
                        storedValuesStorage = viewContext.component.storedValuesStorage,
                    )
                ) { modifier ->
                    DivImageContent(
                        modifier = modifier,
                        data = image.value,
                        imageUrl = imageUrl.toUri(),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.Center,
                        placeholderColor = Color.Transparent,
                        preview = preview,
                    )
                }
                SideEffect {
                    onCompositionCommitted(imageUrl)
                }
            }
        }
    }

    private fun imageWithCaptureExtension(description: String): Div.Image {
        val image = image(
            accessibility = accessibility(description = description),
            imageUrl = IMAGE_URL,
        ) as Div.Image
        return Div.Image(
            image.value.copy(
                extensions = listOf(DivExtension(id = CAPTURE_EXTENSION_ID))
            )
        )
    }

    private fun createDivContext(): DivContext {
        return DivContext(
            baseContext = ApplicationProvider.getApplicationContext<Context>(),
            configuration = DivConfiguration.Builder().apply {
                extensionHandlers = mapOf(CAPTURE_EXTENSION_ID to imageLoadedCapture)
                imageLoaderConfiguration = this@DivImageContentTest.imageLoaderConfiguration
                reporter = this@DivImageContentTest.reporter
            }.build(),
            debugConfiguration = DivDebugConfiguration(coroutineScope = testScope),
        )
    }

    private companion object {
        const val IMAGE_URL = "https://divkit.tech/image.png"
        const val REPLACEMENT_IMAGE_URL = "https://divkit.tech/replacement.png"
        const val CAPTURE_EXTENSION_ID = "image_loaded_capture"
    }
}

private class ControlledImagePreviewHandler(
    private val blockedUrl: String,
) {
    private val observedRequests = CopyOnWriteArrayList<ImageRequest>()
    private val continueBlockedLoad = CompletableDeferred<Unit>()

    val handler = AsyncImagePreviewHandler { request ->
        observedRequests.add(request)
        if (request.data.toString() == blockedUrl) {
            continueBlockedLoad.await()
        }
        createBitmap(1, 1).asImage()
    }

    fun hasObserved(url: String): Boolean {
        return observedRequests.any { it.data.toString() == url }
    }

    fun completeBlockedLoad() {
        continueBlockedLoad.complete(Unit)
    }
}

private class ImageLoadedCaptureExtensionHandler : DivExtensionHandler {
    private var observedData: DivBase? = null
    var isCurrentImageLoaded = false
        private set

    fun isLoaded(data: DivBase): Boolean {
        return isObserving(data) && isCurrentImageLoaded
    }

    fun isObserving(data: DivBase): Boolean {
        return observedData === data
    }

    @Composable
    @NonSkippableComposable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val data = environment.data.value()
        val isLoaded = LocalDivViewContext.current.component.imageStateStorage.isLoaded(data)
        SideEffect {
            observedData = data
            isCurrentImageLoaded = isLoaded
        }
        content(modifier)
    }
}
