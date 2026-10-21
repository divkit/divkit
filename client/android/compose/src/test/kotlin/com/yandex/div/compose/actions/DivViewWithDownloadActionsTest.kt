package com.yandex.div.compose.actions

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.internal.DivDebugConfiguration
import com.yandex.div.compose.utils.TestNetworkClient
import com.yandex.div.test.data.action
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.downloadAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div2.DivData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DivViewWithDownloadActionsTest {

    @get:Rule
    val rule = createComposeRule()

    private val actionHandler = TestExternalActionHandler()
    private val isVisible = mutableStateOf(true)
    private val networkClient = TestNetworkClient()
    private val testScope = TestScope()

    private val divContext = DivContext(
        baseContext = getApplicationContext(),
        configuration = divConfiguration {
            actionHandler = this@DivViewWithDownloadActionsTest.actionHandler
            networkClient = this@DivViewWithDownloadActionsTest.networkClient
            reporter = TestReporter()
        },
        debugConfiguration = DivDebugConfiguration(
            coroutineScope = testScope,
            ioDispatcher = StandardTestDispatcher(testScope.testScheduler),
        ),
    )

    private val downloadAction = action(
        typed = downloadAction(
            url = "https://example.test/patch",
            onSuccessActions = listOf(action(id = "success")),
            onFailActions = listOf(action(id = "failure")),
        )
    )

    private val card = data(
        container(
            items = listOf(
                text(id = "target", text = "Before"),
                text(id = "download", text = "Download", action = downloadAction),
            )
        )
    )

    @Test
    fun `leaving composition cancels download without applying patch or invoking callbacks`() {
        setContent(card)

        startDownload()
        hideCard()
        completeDownload()
        showCard()

        rule.onNodeWithTag("target").assertTextEquals("Before")
        assertTrue(actionHandler.handledActions.isEmpty())
    }

    @Test
    fun `download works after the same card returns to composition`() {
        setContent(card)

        startDownload()
        hideCard()
        showCard()
        startDownload()

        assertEquals(2, networkClient.requests.size)

        completeDownload()

        assertPatchApplied()
    }

    @Test
    fun `initially true trigger downloads and applies patch`() {
        setContent(
            data(
                text(id = "target", text = "Before"),
                triggers = listOf(trigger(action = downloadAction, condition = "@{true}")),
            )
        )
        testScope.runCurrent()

        assertEquals(1, networkClient.requests.size)

        completeDownload()

        assertPatchApplied()
    }

    private fun assertPatchApplied() {
        assertEquals(
            listOf(actionData(id = "success", source = DivActionSource.PATCH)),
            actionHandler.handledActions
        )

        rule.onNodeWithTag("target").assertTextEquals("After")
    }

    private fun setContent(data: DivData) {
        rule.setContent {
            CompositionLocalProvider(LocalContext provides divContext) {
                if (isVisible.value) {
                    DivView(data)
                }
            }
        }
    }

    private fun hideCard() {
        rule.runOnIdle { isVisible.value = false }
        rule.waitForIdle()
    }

    private fun showCard() {
        rule.runOnIdle { isVisible.value = true }
        rule.waitForIdle()
    }

    private fun startDownload() {
        rule.onNodeWithTag("download").performClick()
        testScope.runCurrent()
    }

    private fun completeDownload() {
        networkClient.completeResponse(
            body = """
            {
              "patch": {
                "changes": [
                  {
                    "id": "target",
                    "items": [{"type": "text", "id": "target", "text": "After"}]
                  }
                ]
              }
            }
            """
        )
        testScope.runCurrent()
        rule.waitForIdle()
    }
}
