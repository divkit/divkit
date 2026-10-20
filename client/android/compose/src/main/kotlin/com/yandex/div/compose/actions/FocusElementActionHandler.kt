package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div2.DivActionFocusElement
import javax.inject.Inject

@DivViewScope
internal class FocusElementActionHandler @Inject constructor(
    private val bindingStorage: FocusBindingStorage,
    private val reporter: DivReporter,
) {
    fun handle(context: DivActionHandlingContext, action: DivActionFocusElement) {
        val id = action.elementId.evaluate(context.expressionResolver)
        val requester = bindingStorage.getFocusRequester(id)
        if (requester == null) {
            reporter.reportError("Element with id '$id' not found for focus_element action")
            return
        }
        requester.requestFocus()
    }
}
