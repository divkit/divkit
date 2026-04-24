package com.yandex.div.core.actions

import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionSetCursorPosition
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedSetCursorPositionHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        return when (action) {
            is DivActionTyped.SetCursorPosition -> {
                handleSetCursorPosition(action.value, view, resolver)
                true
            }
            else -> false
        }
    }

    private fun handleSetCursorPosition(
        action: DivActionSetCursorPosition,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val id = action.id.evaluate(resolver)
        val targets = ViewLocator.findViewsWithTag(view, id)
            .filterIsInstance<DivInputView>()

        val target = when {
            targets.isEmpty() -> return view.logError("No input view with id '$id'")
            targets.size > 1 -> return view.logError("Found multiple input views with id '$id'")
            else -> targets.first()
        }

        val textLength = target.length()
        val start = action.position.start.evaluate(resolver).let {
            it.normalizePosition(textLength)
                ?: return view.logError("Wrong start value $it")
        }

        val end = action.position.end?.evaluate(resolver)?.let {
            it.normalizePosition(textLength)
                ?: return view.logError("Wrong end value $it")
        } ?: start

        target.setCursorPosition(start, end)
    }

    private fun Long.normalizePosition(textLength: Int): Int? {
        return when (this) {
            in 0 .. textLength -> toIntSafely()
            -1L -> textLength
            else -> null
        }
    }

    private fun DivInputView.setCursorPosition(start: Int, end: Int) {
        if (!isFocused) {
            requestFocus()
            openKeyboard()
        }
        setSelection(start, end)
    }

    private fun Div2View.logError(message: String) = logError(message.toActionError())

    private fun String.toActionError() =
        RuntimeException("Failed to handle set_cursor_action", IllegalArgumentException(this))
}
