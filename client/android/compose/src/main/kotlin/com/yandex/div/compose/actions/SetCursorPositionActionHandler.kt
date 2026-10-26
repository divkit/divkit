package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div2.DivActionSetCursorPosition
import javax.inject.Inject

@DivViewScope
internal class SetCursorPositionActionHandler @Inject constructor(
    private val bindingStorage: FocusBindingStorage,
    private val reporter: DivReporter,
) {
    fun handle(context: DivActionHandlingContext, action: DivActionSetCursorPosition) {
        val resolver = context.expressionResolver
        val id = action.id.evaluate(resolver)
        val input = bindingStorage.findInput(id) ?: return

        val length = input.text.length()
        val start = action.position.start.evaluate(resolver).let {
            it.normalizePosition(length) ?: return reporter.reportError("Wrong start value $it")
        }
        val end = action.position.end?.evaluate(resolver)?.let {
            it.normalizePosition(length) ?: return reporter.reportError("Wrong end value $it")
        } ?: start

        input.text.setCursorPosition(start, end)
    }

    /** A position of the shown text; -1 is its end, as in the View renderer. */
    private fun Long.normalizePosition(length: Int): Int? = when (this) {
        in 0..length -> toInt()
        -1L -> length
        else -> null
    }
}
