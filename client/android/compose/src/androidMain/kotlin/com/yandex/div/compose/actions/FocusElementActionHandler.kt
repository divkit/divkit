package com.yandex.div.compose.actions

import com.yandex.div.compose.di.DivViewScope
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div2.DivActionFocusElement
import dev.zacsweers.metro.Inject

@DivViewScope
@Inject
internal class FocusElementActionHandler(
    private val bindingStorage: FocusBindingStorage,
) {
    fun handle(context: DivActionHandlingContext, action: DivActionFocusElement) {
        val id = action.elementId.evaluate(context.expressionResolver)
        bindingStorage.findTarget(id)?.focusRequester?.requestFocus()
    }
}
