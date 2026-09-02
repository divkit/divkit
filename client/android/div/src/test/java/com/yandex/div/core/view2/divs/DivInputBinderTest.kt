package com.yandex.div.core.view2.divs

import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.AccessibilityStateProvider
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.atLeastOnce
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivInputBinderTest : DivBinderTest() {

    private val divTypefaceResolver = mock<DivTypefaceResolver> {
        on { getTypefaceProvider(anyOrNull()) } doReturn DivTypefaceProvider.DEFAULT
    }
    private val variableBinder = mock<TwoWayStringVariableBinder> {
        on { bindVariable(any(), any(), any(), any()) } doReturn mock()
    }
    private val accessibilityStateProvider = mock<AccessibilityStateProvider> {
        on { isAccessibilityEnabled(any()) } doReturn false
    }

    private val underTest = DivInputBinder(
        baseBinder = baseBinder,
        typefaceResolver = divTypefaceResolver,
        variableBinder = variableBinder,
        actionPerformer = actionPerformer,
        accessibilityStateProvider = accessibilityStateProvider,
        variableMutationHandler = mock(),
    )

    private val path = DivStatePath(0)

    @Test
    fun `enter_key_type search without enter_key_actions - listener returns true for search action`() {
        val (div, view) = createDivAndView(INPUT_SEARCH_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_SEARCH)

        assertTrue("Listener should return true to consume the IME action", consumed)
    }

    @Test
    fun `enter_key_type search without enter_key_actions - focus does not move to next field`() {
        val (div, view) = createDivAndView(INPUT_SEARCH_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        // IME_ACTION_SEARCH should be consumed (returns true), preventing focus traversal
        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_SEARCH)

        assertTrue("Search IME action must be consumed to prevent focus traversal", consumed)
    }

    @Test
    fun `enter_key_type done without enter_key_actions - listener returns true for done action`() {
        val (div, view) = createDivAndView(INPUT_DONE_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_DONE)

        assertTrue("Listener should return true for DONE action", consumed)
    }

    @Test
    fun `enter_key_type go without enter_key_actions - listener returns true for go action`() {
        val (div, view) = createDivAndView(INPUT_GO_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_GO)

        assertTrue("Listener should return true for GO action", consumed)
    }

    @Test
    fun `enter_key_type send without enter_key_actions - listener returns true for send action`() {
        val (div, view) = createDivAndView(INPUT_SEND_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_SEND)

        assertTrue("Listener should return true for SEND action", consumed)
    }

    @Test
    fun `enter_key_type default - imeOptions set to unspecified`() {
        val (div, view) = createDivAndView(INPUT_DEFAULT_NO_ACTIONS)
        underTest.bindView(view, div, divView)

        // For DEFAULT, imeOptions should use IME_ACTION_UNSPECIFIED to allow standard traversal
        val imeAction = view.imeOptions and EditorInfo.IME_MASK_ACTION
        assertTrue(
            "Default enter key type should use IME_ACTION_UNSPECIFIED",
            imeAction == EditorInfo.IME_ACTION_UNSPECIFIED
        )
    }

    @Test
    fun `rebind with different enter_key_type - imeOptions correctly replaced not accumulated`() {
        val (divSearch, viewSearch) = createDivAndView(INPUT_SEARCH_NO_ACTIONS)
        underTest.bindView(viewSearch, divSearch, divView)

        // Rebind with "done" type using the same view to simulate rebind
        val (divDone, _) = createDivAndView(INPUT_DONE_NO_ACTIONS)
        underTest.bindView(viewSearch, divDone, divView)

        val imeOptionsAfterRebind = viewSearch.imeOptions and EditorInfo.IME_MASK_ACTION
        assertTrue(
            "After rebind with done, imeOptions action bits should be exactly IME_ACTION_DONE (not accumulated with previous IME_ACTION_SEARCH)",
            imeOptionsAfterRebind == EditorInfo.IME_ACTION_DONE
        )
        assertFalse(
            "After rebind with done, IME_ACTION_SEARCH bits should no longer be set",
            imeOptionsAfterRebind == EditorInfo.IME_ACTION_SEARCH
        )
    }

    @Test
    fun `enter_key_type search with enter_key_actions - listener returns true`() {
        val (div, view) = createDivAndView(INPUT_SEARCH_WITH_ACTIONS)
        underTest.bindView(view, div, divView)

        val consumed = view.invokeEditorAction(EditorInfo.IME_ACTION_SEARCH)

        assertTrue("Listener should return true for search action even with enter_key_actions", consumed)
    }

    @Test
    fun `cursor stays after the typed character when mask appends a separator`() {
        val (div, view) = createDivAndView(INPUT_PHONE_MASK)
        underTest.bindView(view, div, divView)
        view.startListeningToTextChanges()

        view.type("999")

        assertEquals("+7 999 ", view.text.toString())
        assertEquals("cursor must stay at the end of the typed text", 7, view.selectionStart)
    }

    @Test
    fun `cursor follows the whole typed text`() {
        val (div, view) = createDivAndView(INPUT_PHONE_MASK)
        underTest.bindView(view, div, divView)
        view.startListeningToTextChanges()

        view.type("9991234567")

        assertEquals("+7 999 123 45 67", view.text.toString())
        assertEquals(16, view.selectionStart)
    }

    private fun DivInputView.startListeningToTextChanges() {
        val captor = argumentCaptor<TwoWayStringVariableBinder.Callbacks>()
        verify(variableBinder, atLeastOnce()).bindVariable(any(), any(), any(), captor.capture())
        captor.lastValue.setViewStateChangeListener { }
    }

    private fun DivInputView.type(digits: String) = digits.forEach { digit ->
        editableText.insert(selectionStart, digit.toString())
    }

    private fun DivInputView.invokeEditorAction(actionId: Int): Boolean {
        val captor = argumentCaptor<TextView.OnEditorActionListener>()
        verify(this, atLeastOnce()).setOnEditorActionListener(captor.capture())
        val listener = captor.lastValue
        return listener.onEditorAction(this, actionId, null)
    }

    private fun createDivAndView(jsonString: String): Pair<DivBlock.Input, DivInputView> {
        val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
        val div = Div(environment, JSONObject(jsonString)).toBlock(resolver, path) as DivBlock.Input
        val view = spy(viewCreator.create(div.div, ExpressionResolver.EMPTY) as DivInputView).apply {
            layoutParams = defaultLayoutParams()
        }
        return div to view
    }

    companion object {
        private val INPUT_PHONE_MASK = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "phone",
              "mask": {
                "type": "fixed_length",
                "always_visible": false,
                "pattern": "+7 ### ### ## ##",
                "pattern_elements": [ { "key": "#", "regex": "[0-9]" } ],
                "raw_text_variable": "raw_text_variable"
              }
            }
        """.trimIndent()

        private val INPUT_SEARCH_NO_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "search"
            }
        """.trimIndent()

        private val INPUT_DONE_NO_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "done"
            }
        """.trimIndent()

        private val INPUT_GO_NO_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "go"
            }
        """.trimIndent()

        private val INPUT_SEND_NO_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "send"
            }
        """.trimIndent()

        private val INPUT_DEFAULT_NO_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "default"
            }
        """.trimIndent()

        private val INPUT_SEARCH_WITH_ACTIONS = """
            {
              "type": "input",
              "width": { "type": "match_parent" },
              "height": { "type": "wrap_content" },
              "text_variable": "text_variable",
              "keyboard_type": "single_line_text",
              "enter_key_type": "search",
              "enter_key_actions": [
                {
                  "log_id": "search_action",
                  "url": "div-action://set_variable?name=text_variable&value=searched"
                }
              ]
            }
        """.trimIndent()
    }
}
