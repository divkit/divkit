package com.yandex.div.compose.actions

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div.compose.focus.FocusBindingStorage
import javax.inject.Inject

@DivViewScope
internal class ClearFocusActionHandler @Inject constructor(
    private val bindingStorage: FocusBindingStorage,
    private val reporter: DivReporter,
) {
    fun handle() {
        bindingStorage.runAfterFirstRootBinding(::clearFocus)
    }

    private fun clearFocus() {
        val activeBindings = bindingStorage.get()
        if (activeBindings.isEmpty()) {
            reporter.reportError("No focus manager is bound for clear_focus action")
            return
        }

        val wasClearing = bindingStorage.isClearing
        bindingStorage.isClearing = true
        try {
            activeBindings.filter { it.hasFocus }.mapNotNull { it.focusManager }.toSet()
                .forEach { it.clearFocus(force = true) }
        } finally {
            bindingStorage.isClearing = wasClearing
        }
        activeBindings.mapNotNull { it.keyboardController }.toSet().forEach { it.hide() }
    }
}
