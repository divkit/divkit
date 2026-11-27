package com.yandex.div.compose.focus

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalFocusManager
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

/**
 * Focused text inputs of all views that share this registry.
 *
 * A select opened by touch calls [clear], causing the input to hide its keyboard and dispatch blur actions.
 * Clearing focus through each input's own [FocusManager] reaches other Compose roots and preserves focus
 * on other elements.
 */
@DivContextScope
@Inject
internal class InputFocus {
    private val focusedInputs = mutableSetOf<FocusedInput>()

    fun register(input: FocusedInput) {
        focusedInputs += input
    }

    fun unregister(input: FocusedInput) {
        focusedInputs -= input
    }

    fun clear() {
        // Clearing focus can synchronously unregister inputs through focus callbacks.
        focusedInputs.toList().forEach { input ->
            if (input in focusedInputs) input.clearFocus()
        }
    }

    fun interface FocusedInput {
        fun clearFocus()
    }

    /**
     * A text input, kept in [focusedInputs] while it has focus. It clears the focus through the
     * [FocusManager] of its own window.
     */
    internal class Input(
        private var findInputFocus: (CompositionLocalConsumerModifierNode) -> InputFocus,
    ) : Modifier.Node(), FocusEventModifierNode, CompositionLocalConsumerModifierNode, FocusedInput {
        private var inputFocus: InputFocus? = null

        override fun onFocusEvent(focusState: FocusState) {
            updateRegistration(if (focusState.isFocused) findInputFocus(this) else null)
        }

        override fun onDetach() = updateRegistration(null)

        fun updateLookup(findInputFocus: (CompositionLocalConsumerModifierNode) -> InputFocus) {
            this.findInputFocus = findInputFocus
            if (inputFocus != null) updateRegistration(findInputFocus(this))
        }

        override fun clearFocus() {
            currentValueOf(LocalFocusManager).clearFocus(force = true)
        }

        private fun updateRegistration(inputFocus: InputFocus?) {
            if (this.inputFocus === inputFocus) return
            this.inputFocus?.unregister(this)
            inputFocus?.register(this)
            this.inputFocus = inputFocus
        }
    }
}

/** Tracks this text input in the supplied registry while it has focus. */
internal fun Modifier.trackInputFocus(
    findInputFocus: (CompositionLocalConsumerModifierNode) -> InputFocus,
): Modifier = then(InputFocusElement(findInputFocus))

private data class InputFocusElement(
    val findInputFocus: (CompositionLocalConsumerModifierNode) -> InputFocus,
) : ModifierNodeElement<InputFocus.Input>() {
    override fun create() = InputFocus.Input(findInputFocus)

    override fun update(node: InputFocus.Input) = node.updateLookup(findInputFocus)

    override fun InspectorInfo.inspectableProperties() {
        name = "trackInputFocus"
    }
}
