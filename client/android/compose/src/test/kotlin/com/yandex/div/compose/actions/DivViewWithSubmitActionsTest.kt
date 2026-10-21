package com.yandex.div.compose.actions

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.compose.utils.TestNetworkClient
import com.yandex.div.core.network.DivNetworkRequest
import com.yandex.div.core.network.DivNetworkRequestHeader
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.state
import com.yandex.div.test.data.submitAction
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivActionSubmit
import com.yandex.div2.DivActionTyped
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.verify
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DivViewWithSubmitActionsTest {
    private val isVisible = mutableStateOf(true)
    private val networkClient = TestNetworkClient()
    private val reporter = TestReporter()
    private val testActionHandler = TestExternalActionHandler()

    private val handledActions: List<DivActionData>
        get() = testActionHandler.handledActions

    private val requests: List<DivNetworkRequest>
        get() = networkClient.requests

    private val configuration = divConfiguration {
        actionHandler = testActionHandler
        networkClient = this@DivViewWithSubmitActionsTest.networkClient
        reporter = this@DivViewWithSubmitActionsTest.reporter
    }

    private val form = text(
        id = "form",
        text = "Form",
        variables = listOf(variable("value", "target")),
    )

    private val submitAction = submitAction(
        containerId = constant("form"),
        url = uriExpression("https://example.test/@{value}"),
        onSuccessActions = listOf(action(id = "success")),
        onFailActions = listOf(action(id = "failure")),
    )

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun `action submits request`() {
        setContent(
            action = submitAction(
                containerId = constant("form"),
                url = uriExpression("https://example.test/@{value}"),
            )
        )

        submit()

        val request = requests.single()
        assertEquals("https://example.test/origin", request.url)
        assertEquals("POST", request.method)
    }

    @Test
    fun `headers are evaluated and repeated names are preserved`() {
        setContent(
            action = submitAction(
                containerId = "form",
                url = "https://example.test/",
                headers = listOf(
                    DivActionSubmit.Request.Header(
                        name = expression("X-@{value}"),
                        value = expression("@{value}"),
                    ),
                    DivActionSubmit.Request.Header(
                        name = constant("X-origin"),
                        value = constant("second"),
                    ),
                ),
            )
        )

        submit()

        assertEquals(
            listOf(
                DivNetworkRequestHeader("X-origin", "origin"),
                DivNetworkRequestHeader("X-origin", "second"),
            ),
            requests.single().headers
        )
    }

    @Test
    fun `body uses current values of only the selected element variables`() {
        setContent(
            text(
                id = "form",
                text = "Change value",
                variables = listOf(variable("value", "target")),
                action = action(url = "div-action://set_variable?name=value&value=changed"),
            ),
            action = submitAction,
        )
        rule.onNodeWithTag("form").performClick()

        submit()

        assertEquals(
            "{\"value\":\"changed\"}",
            requests.single().body?.decodeToString()
        )
    }

    @Test
    fun `container id is evaluated in the initiating scope`() {
        setContent(
            action = submitAction(
                containerId = expression("@{value == 'origin' ? 'form' : 'missing'}"),
                url = uriExpression("https://example.test/@{value}"),
            )
        )

        submit()

        assertEquals(
            "{\"value\":\"target\"}",
            requests.single().body?.decodeToString()
        )
    }

    @Test
    fun `get request has no body`() {
        setContent(
            action = submitAction(
                containerId = "form",
                url = "https://example.test/",
                method = DivActionSubmit.Request.Method.GET,
            )
        )

        submit()

        assertNull(requests.single().body)
    }

    @Test
    fun `head request has no body`() {
        setContent(
            action = submitAction(
                containerId = "form",
                url = "https://example.test/",
                method = DivActionSubmit.Request.Method.HEAD,
            )
        )

        submit()

        assertNull(requests.single().body)
    }

    @Test
    fun `missing container reports an error without sending a request`() {
        reporter.failOnError = false

        setContent(
            action = submitAction(
                containerId = "missing",
                url = "https://example.test/",
            )
        )

        submit()

        assertEquals(
            "Failed to perform submit action. Container not found: missing",
            reporter.lastError
        )
        assertTrue(requests.isEmpty())
        assertTrue(handledActions.isEmpty())
    }

    @Test
    fun `empty container reports an error without sending a request`() {
        reporter.failOnError = false

        setContent(
            text(id = "empty", text = ""),
            action = submitAction(
                containerId = "empty",
                url = "https://example.test/",
            ),
        )

        submit()

        assertEquals(
            "Failed to perform submit action. Container component not found: empty",
            reporter.lastError
        )
        assertTrue(requests.isEmpty())
        assertTrue(handledActions.isEmpty())
    }

    @Test
    fun `duplicate id in inactive state reports an error without sending a request`() {
        reporter.failOnError = false

        setContent(
            state(
                states = listOf(
                    state(stateId = "first", div = text(text = "", id = "form")),
                    state(stateId = "second", div = text(text = "", id = "form")),
                )
            ),
            action = submitAction,
        )

        submit()

        assertEquals(
            "Failed to perform submit action. Found multiple elements that respond to id: form",
            reporter.lastError
        )
        assertTrue(requests.isEmpty())
        assertTrue(handledActions.isEmpty())
    }

    @Test
    fun `onSuccessActions are executed after successful request`() {
        setContent(action = submitAction)

        submit()

        completeResponse(successful = true)

        assertEquals(
            listOf(actionData(id = "success", source = DivActionSource.SUBMIT)),
            handledActions
        )
    }

    @Test
    fun `onFailureActions are executed after unsuccessful response`() {
        reporter.failOnError = false

        setContent(action = submitAction)

        submit()

        completeResponse(successful = false)

        assertEquals(
            listOf(actionData(id = "failure", source = DivActionSource.SUBMIT)),
            handledActions
        )
    }

    @Test
    fun `onFailureActions are executed after network exception`() {
        reporter.failOnError = false

        setContent(action = submitAction)

        submit()

        networkClient.response.completeExceptionally(IOException("offline"))
        rule.waitForIdle()

        assertEquals(
            listOf(actionData(id = "failure", source = DivActionSource.SUBMIT)),
            handledActions
        )
    }

    @Test
    fun `response is closed after request completion`() {
        setContent(action = submitAction)

        submit()
        completeResponse(successful = true)

        verify(networkClient.response.getCompleted()).close()
    }

    @Test
    fun `uncreated target component reports an error without sending a request`() {
        reporter.failOnError = false

        setContent(
            state(
                states = listOf(
                    state(stateId = "first", div = text(text = "Active")),
                    state(stateId = "second", div = form),
                )
            ),
            action = submitAction(
                containerId = "form",
                url = "https://example.test/",
            ),
        )

        submit()

        assertEquals(
            "Failed to perform submit action. Container component not found: form",
            reporter.lastError
        )
        assertTrue(requests.isEmpty())
        assertTrue(handledActions.isEmpty())
    }

    @Test
    fun `leaving composition cancels in-flight request without invoking callbacks`() {
        setContent(action = submitAction)

        submit()

        assertEquals(1, requests.size)

        rule.runOnIdle { isVisible.value = false }
        rule.onNodeWithTag("submit").assertDoesNotExist()

        completeResponse(successful = true)

        assertTrue(handledActions.isEmpty())
    }

    @Test
    fun `submit works after the same card returns to composition`() {
        setContent(action = submitAction)

        submit()

        rule.runOnIdle { isVisible.value = false }
        rule.waitForIdle()
        rule.runOnIdle { isVisible.value = true }

        submit()

        completeResponse(successful = true)

        assertEquals(2, requests.size)
        assertEquals(
            listOf(actionData(id = "success", source = DivActionSource.SUBMIT)),
            handledActions
        )
    }

    private fun setContent(
        content: Div = form,
        action: DivActionTyped,
    ) {
        val data = data(
            container(
                items = listOf(
                    content,
                    text(text = "Submit", action = action(typed = action)),
                )
            ),
            variables = listOf(variable("value", "origin")),
        )
        rule.setContentWithDivContext(configuration) {
            if (isVisible.value) {
                DivView(data)
            }
        }
    }

    private fun submit() {
        rule.onNodeWithText("Submit").performClick()
        rule.waitForIdle()
    }

    private fun completeResponse(successful: Boolean) {
        networkClient.completeResponse(isSuccessful = successful)
        rule.waitForIdle()
    }
}
