package com.yandex.div.view.actions

import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import com.yandex.div.view.ViewActions.tapWithRetries
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

/**
 * Taps the field, then enters [inputs] one by one through an input connection, as a software
 * keyboard does, so input filters, the key listener, masks and validators of the field handle them
 * as user input.
 */
class EditTextThroughInputConnection(
    private val replace: Boolean,
    private val inputs: List<String>,
) : ViewAction {

    private val tap = tapWithRetries()

    override fun getConstraints(): Matcher<View> =
        allOf(isAssignableFrom(EditText::class.java), tap.constraints)

    override fun getDescription() =
        "Tap and ${if (replace) "replace" else "append"} text through an input connection"

    override fun perform(uiController: UiController, view: View) {
        val input = view as EditText
        tap.perform(uiController, input)
        uiController.loopMainThreadUntilIdle()
        check(input.hasFocus()) { "The input did not get focus by a tap" }
        // DivScreenshotActivity hides the caret only in inputs that exist when a card is bound.
        input.isCursorVisible = false
        val connection = checkNotNull(input.onCreateInputConnection(EditorInfo())) {
            "The input does not accept text"
        }
        // A keyboard may set a composing region, which a commit replaces instead of the selection.
        connection.finishComposingText()
        val length = input.length()
        connection.setSelection(if (replace) 0 else length, length)
        inputs.forEachIndexed { index, text ->
            check(input.hasFocus()) { "The input lost focus before character ${index + 1}" }
            connection.finishComposingText()
            connection.commitText(text, 1)
            uiController.loopMainThreadUntilIdle()
        }
    }
}
