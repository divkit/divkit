package com.yandex.div.compose.actions

import android.content.ClipData
import android.content.Context
import androidx.core.net.toUri
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.system.ClipboardManager
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.copyTextToClipboardAction
import com.yandex.div.test.data.copyUrlToClipboardAction
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import android.content.ClipboardManager as AndroidClipboardManager

@RunWith(AndroidJUnit4::class)
class CopyToClipboardActionHandlerTest {
    private val environment = ActionHandlerEnvironment()
    private val reporter = environment.reporter

    private val clipboard = getApplicationContext<Context>()
        .getSystemService(Context.CLIPBOARD_SERVICE) as AndroidClipboardManager

    private val clipboardManager = ClipboardManager(
        context = getApplicationContext(),
        reporter = reporter
    )

    @BeforeTest
    fun setUp() {
        environment.init(
            copyToClipboardActionHandler = CopyToClipboardActionHandler(
                clipboardManager = clipboardManager,
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
            action(
                typed = copyUrlToClipboardAction(constant("https://divkit.tech/path?q=1".toUri()))
            )
        )

        val clip = assertNotNull(clipboard.primaryClip)
        assertEquals("Copied url", clip.description.label)
        assertEquals("text/uri-list", clip.description.getMimeType(0))
        assertTrue(clip.description.hasMimeType("text/plain"))
        assertEquals(1, clip.itemCount)
        assertEquals("https://divkit.tech/path?q=1".toUri(), clip.getItemAt(0).uri)
        assertEquals("https://divkit.tech/path?q=1", clip.getItemAt(0).text)
    }

    @Test
    fun `empty text replaces the previous clipboard content`() {
        environment.handle(action(typed = copyTextToClipboardAction("")))

        assertEquals("", clipboard.primaryClip?.getItemAt(0)?.text)
    }

    @Test
    fun `unavailable clipboard is reported`() {
        reporter.failOnError = false

        environment.init(
            copyToClipboardActionHandler = CopyToClipboardActionHandler(
                clipboardManager = ClipboardManager(context = mock(), reporter = reporter)
            )
        )

        environment.handle(action(typed = copyTextToClipboardAction("Text")))

        assertEquals(listOf("Failed to access clipboard manager"), reporter.errors)
        assertEquals("previous", clipboard.primaryClip?.getItemAt(0)?.text)
    }
}
