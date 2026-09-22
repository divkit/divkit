package com.yandex.div.compose.actions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.copyTextToClipboardAction
import com.yandex.div.test.data.copyUrlToClipboardAction
import com.yandex.div.test.data.data
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.text
import com.yandex.div.test.data.uriExpression
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@RunWith(AndroidJUnit4::class)
class CopyToClipboardActionHandlerTest {
    private val environment = ActionHandlerEnvironment()
    private val clipboard = getApplicationContext<Context>()
        .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val reporter = environment.reporter

    @BeforeTest
    fun setUp() {
        environment.init(
            copyToClipboardActionHandler = CopyToClipboardActionHandler(
                context = getApplicationContext(),
                reporter = reporter
            )
        )
        clipboard.setPrimaryClip(ClipData.newPlainText("Previous", "previous"))
    }

    @Test
    fun `text is copied as a plain text clip`() {
        environment.handle(action(typed = copyTextToClipboardAction("Hello\nПривет 👋")))

        val clip = assertNotNull(clipboard.primaryClip)
        assertEquals("Copied text", clip.description.label)
        assertEquals("text/plain", clip.description.getMimeType(0))
        assertEquals(1, clip.itemCount)
        assertEquals("Hello\nПривет 👋", clip.getItemAt(0).text)
        assertNull(clip.getItemAt(0).uri)
    }

    @Test
    fun `url is copied as a URI clip`() {
        environment.handle(
            action(typed = copyUrlToClipboardAction(constant("https://divkit.tech/path?q=1".toUri())))
        )

        val clip = assertNotNull(clipboard.primaryClip)
        assertEquals("Copied url", clip.description.label)
        assertEquals("text/uri-list", clip.description.getMimeType(0))
        assertEquals(1, clip.itemCount)
        assertEquals("https://divkit.tech/path?q=1".toUri(), clip.getItemAt(0).uri)
        assertNull(clip.getItemAt(0).text)
    }

    @Test
    fun `empty text replaces the previous clipboard content`() {
        environment.handle(action(typed = copyTextToClipboardAction("")))

        assertEquals("", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `text expression is evaluated again on the next action`() {
        val variable = Variable.StringVariable("value", "first")
        environment.variableController.declare(variable)
        val action = action(typed = copyTextToClipboardAction(expression("@{value}!")))
        environment.handle(action)
        variable.setValueDirectly("second")

        environment.handle(action)

        assertEquals("second!", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `url expression is evaluated again on the next action`() {
        val variable = Variable.StringVariable("path", "first")
        environment.variableController.declare(variable)
        val action = action(typed = copyUrlToClipboardAction(uriExpression("https://divkit.tech/@{path}")))
        environment.handle(action)
        variable.setValueDirectly("second")

        environment.handle(action)

        assertEquals("https://divkit.tech/second".toUri(), clipboard.primaryClip?.getItemAt(0)?.uri)
    }

    @Test
    fun `variable update alone does not change the clipboard`() {
        val variable = Variable.StringVariable("value", "copied")
        environment.variableController.declare(variable)
        environment.handle(action(typed = copyTextToClipboardAction(expression("@{value}"))))

        variable.setValueDirectly("not copied")

        assertEquals("copied", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `missing text variable reports an error and copies the default value`() {
        reporter.failOnError = false

        environment.handle(
            action(typed = copyTextToClipboardAction(expression("@{missing}", failOnError = false)))
        )

        assertEquals("", clipboard.primaryClip?.getItemAt(0)?.text)
        assertContains(reporter.lastError.orEmpty(), "missing")
    }

    @Test
    fun `invalid url expression reports an error and copies the default URI`() {
        reporter.failOnError = false

        environment.handle(
            action(typed = copyUrlToClipboardAction(uriExpression("@{true}", failOnError = false)))
        )

        assertEquals(Uri.EMPTY, clipboard.primaryClip?.getItemAt(0)?.uri)
        assertContains(reporter.lastError.orEmpty(), "true")
    }

    @Test
    fun `failed expression copies its last valid value`() {
        reporter.failOnError = false
        val variable = Variable.StringVariable("value", "17")
        environment.variableController.declare(variable)
        val action = action(
            typed = copyTextToClipboardAction(expression("@{toString(toInteger(value))}", failOnError = false))
        )
        environment.handle(action)
        variable.setValueDirectly("invalid")
        clipboard.setPrimaryClip(ClipData.newPlainText("Other", "other"))

        environment.handle(action)

        assertEquals("17", clipboard.primaryClip?.getItemAt(0)?.text)
        assertContains(reporter.lastError.orEmpty(), "toInteger(value)")
    }

    @Test
    fun `disabled action does not evaluate content or change the clipboard`() {
        environment.handle(
            action(isEnabled = false, typed = copyTextToClipboardAction(expression("@{missing}")))
        )

        assertEquals("previous", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `unavailable clipboard is reported before evaluating content`() {
        reporter.failOnError = false
        environment.init(
            copyToClipboardActionHandler = CopyToClipboardActionHandler(mock(), reporter)
        )

        environment.handle(action(typed = copyTextToClipboardAction(expression("@{missing}"))))

        assertEquals(listOf("Failed to access clipboard manager"), reporter.errors)
        assertEquals("previous", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `content is evaluated using the action context`() {
        environment.variableController.declare(Variable.StringVariable("value", "root"))
        val localEnvironment = ActionHandlerEnvironment()
        localEnvironment.variableController.declare(Variable.StringVariable("value", "local"))

        environment.actionHandler.handle(
            context = localEnvironment.context,
            action = action(typed = copyTextToClipboardAction(expression("@{value}"))),
            source = DivActionSource.TAP
        )

        assertEquals("local", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `clipboard action is available through the DivContext graph`() {
        val divContext = DivContext(getApplicationContext(), DivConfiguration(reporter = reporter))
        val data = data(text(text = "Content"))
        divContext.getViewContext(data)

        divContext.debugFeatures.performAction(
            data = data,
            action = action(typed = copyTextToClipboardAction("copied"), url = "custom://url")
        )

        assertEquals("copied", clipboard.primaryClip?.getItemAt(0)?.text)
    }
}
