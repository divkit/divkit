package com.yandex.div.compose.patch

import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.ActionHandlerEnvironment
import com.yandex.div.compose.actions.DivActionSource
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
    private val externalHandler = TestExternalActionHandler()
    private val scope = TestScope()
    private val response = CompletableDeferred<DivPatch>()
    private val requestedUrls = mutableListOf<Uri>()
    private val manager = DivPatchDownloadManager(
        downloader = DivPatchDownloader {
            requestedUrls.add(it)
            response.await()
        },
        coroutineScope = scope,
        reporter = environment.reporter
    )
    private val legacyCallbacks = DivDownloadCallbacks(
        onSuccessActions = listOf(action(id = "legacy-success")),
        onFailActions = listOf(action(id = "legacy-failure"))
    )

    @BeforeTest
    fun setUp() {
        manager.init { _, onApplied ->
            onApplied()
            true
        }
        environment.init(externalActionHandler = externalHandler, patchDownloadManager = manager)
    }

    @Test
    fun `url download decodes its address and runs callbacks as patch actions`() {
        environment.handle(action(
            url = "div-action://download?url=https%3A%2F%2Fexample.test%2Fpatch%3Fid%3D1%26version%3D2",
            downloadCallbacks = legacyCallbacks
        ))

        completeDownload()

        assertEquals(listOf(Uri.parse("https://example.test/patch?id=1&version=2")), requestedUrls)
        assertEquals(listOf(actionData(id = "legacy-success", source = DivActionSource.PATCH)), externalHandler.handledActions)
    }

    @Test
    fun `typed success callbacks override legacy callbacks`() {
        environment.handle(action(
            typed = downloadAction("https://example.test/patch", onSuccessActions = listOf(action(id = "typed-success"))),
            downloadCallbacks = legacyCallbacks
        ))

        completeDownload()

        assertEquals(listOf(actionData(id = "typed-success", source = DivActionSource.PATCH)), externalHandler.handledActions)
    }

    @Test
    fun `typed download falls back to legacy callbacks`() {
        environment.handle(action(
            typed = downloadAction("https://example.test/patch"),
            downloadCallbacks = legacyCallbacks
        ))

        completeDownload()

        assertEquals(listOf(actionData(id = "legacy-success", source = DivActionSource.PATCH)), externalHandler.handledActions)
    }

    @Test
    fun `empty typed callbacks suppress legacy callbacks`() {
        environment.handle(action(
            typed = downloadAction("https://example.test/patch", onSuccessActions = emptyList()),
            downloadCallbacks = legacyCallbacks
        ))

        completeDownload()

        assertTrue(externalHandler.handledActions.isEmpty())
    }

    @Test
    fun `typed failure callbacks override legacy callbacks`() {
        environment.reporter.failOnError = false
        environment.handle(action(
            typed = downloadAction("https://example.test/patch", onFailActions = listOf(action(id = "typed-failure"))),
            downloadCallbacks = legacyCallbacks
        ))

        response.completeExceptionally(IOException("Download failed"))
        scope.runCurrent()

        assertEquals(listOf(actionData(id = "typed-failure", source = DivActionSource.PATCH)), externalHandler.handledActions)
    }

    @Test
    fun `disabled download action does not start a request`() {
        environment.handle(action(isEnabled = false, typed = downloadAction("https://example.test/patch")))

        scope.runCurrent()

        assertTrue(requestedUrls.isEmpty())
    }

    @Test
    fun `download without url reports an error without starting a request`() {
        environment.reporter.failOnError = false

        environment.handle(action(url = "div-action://download"))
        scope.runCurrent()

        assertEquals("url param is required for download action", environment.reporter.lastError)
        assertTrue(requestedUrls.isEmpty())
    }

    private fun completeDownload() {
        response.complete(DivPatch(changes = listOf(DivPatch.Change("target"))))
        scope.runCurrent()
    }
}
