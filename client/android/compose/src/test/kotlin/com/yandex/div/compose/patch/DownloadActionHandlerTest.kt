package com.yandex.div.compose.patch

import android.net.Uri
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.ActionHandlerEnvironment
import com.yandex.div.compose.actions.DivActionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.DownloadActionHandler
import com.yandex.div.test.data.action
import com.yandex.div.test.data.downloadAction
import com.yandex.div2.DivDownloadCallbacks
import com.yandex.div2.DivPatch
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import org.junit.runner.RunWith
import java.io.IOException
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DownloadActionHandlerTest {
    private val environment = ActionHandlerEnvironment()
    private val reporter = environment.reporter
    private val requestedUrls = mutableListOf<Uri>()
    private val response = CompletableDeferred<DivPatch>()
    private val scope = TestScope()
    private val testHandler = TestExternalActionHandler()

    private val handledActions: List<DivActionData>
        get() = testHandler.handledActions

    private val downloadManager = DivPatchDownloadManager(
        downloader = {
            requestedUrls.add(it)
            response.await()
        },
        mainCoroutineScope = scope,
        reporter = reporter
    )

    private val actionHandler = DownloadActionHandler(
        actionHandler = { environment.actionHandler },
        downloadManager = downloadManager,
    )

    @BeforeTest
    fun setUp() {
        downloadManager.init { _, onApplied ->
            onApplied()
            true
        }
        environment.init(
            downloadActionHandler = actionHandler,
            externalActionHandler = testHandler,
        )
    }

    @Test
    fun `div-action sends request and executes onSuccessActions`() {
        environment.handle(
            action(
                url = "div-action://download?url=https%3A%2F%2Fexample.test%2Fpatch%3Fid%3D1%26version%3D2",
                downloadCallbacks = DivDownloadCallbacks(
                    onSuccessActions = listOf(action(id = "legacy-success")),
                )
            )
        )

        completeDownload()

        assertEquals(
            listOf("https://example.test/patch?id=1&version=2".toUri()),
            requestedUrls
        )

        assertEquals(
            listOf(actionData(id = "legacy-success", source = DivActionSource.PATCH)),
            handledActions
        )
    }

    @Test
    fun `typed action sends request and executes onSuccessActions`() {
        environment.handle(
            action(
                typed = downloadAction(
                    url = "https://example.test/patch",
                    onSuccessActions = listOf(action(id = "success")),
                ),
            )
        )

        completeDownload()

        assertEquals(
            listOf("https://example.test/patch".toUri()),
            requestedUrls
        )

        assertEquals(
            listOf(actionData(id = "success", source = DivActionSource.PATCH)),
            handledActions
        )
    }

    @Test
    fun `typed action executes onFailActions after the request failure`() {
        reporter.failOnError = false

        environment.handle(
            action(
                typed = downloadAction(
                    url = "https://example.test/patch",
                    onFailActions = listOf(action(id = "failure")),
                ),
            )
        )

        response.completeExceptionally(IOException("Download failed"))
        scope.runCurrent()

        assertEquals(
            listOf(actionData(id = "failure", source = DivActionSource.PATCH)),
            handledActions
        )
    }

    @Test
    fun `typed action does not execute legacy downloadCallbacks`() {
        environment.handle(
            action(
                typed = downloadAction(url = "https://example.test/patch"),
                downloadCallbacks = DivDownloadCallbacks(
                    onSuccessActions = listOf(action(id = "legacy-success")),
                ),
            )
        )

        completeDownload()

        assertTrue(handledActions.isEmpty())
    }

    private fun completeDownload() {
        response.complete(DivPatch(changes = listOf(DivPatch.Change("target"))))
        scope.runCurrent()
    }
}
