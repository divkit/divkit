package com.yandex.div.compose.focus

import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

@DivViewScope
internal class FocusBindingStorage @Inject constructor() {
    class Binding {
        var hasFocus: Boolean = false
        var focusManager: FocusManager? = null
        var keyboardController: SoftwareKeyboardController? = null
    }

    private val bindings = mutableSetOf<Binding>()
    private val focusRequesters = mutableMapOf<String, FocusRequester>()
    private val pendingActions = mutableSetOf<() -> Unit>()
    private var wasBound = false
    var isClearing: Boolean = false

    fun register(
        binding: Binding,
        focusManager: FocusManager,
        keyboardController: SoftwareKeyboardController?,
    ) {
        binding.focusManager = focusManager
        binding.keyboardController = keyboardController
        bindings.add(binding)
        wasBound = true
        val actions = pendingActions.toList()
        pendingActions.clear()
        actions.forEach { it() }
    }

    fun unregister(binding: Binding) {
        bindings.remove(binding)
        binding.focusManager = null
        binding.keyboardController = null
    }

    fun get(): List<Binding> = bindings.toList()

    fun runAfterFirstRootBinding(action: () -> Unit) {
        if (wasBound) {
            action()
        } else {
            pendingActions.add(action)
        }
    }

    fun register(id: String, requester: FocusRequester) {
        focusRequesters[id] = requester
    }

    fun unregister(id: String) {
        focusRequesters.remove(id)
    }

    fun getFocusRequester(id: String): FocusRequester? = focusRequesters[id]
}
