package com.yandex.div.compose.focus

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

/**
 * Focused text inputs of all views in a [DivContext].
 *
 * A select opened by touch calls [clear], which removes the focus from the input, hiding the keyboard and
 * dispatching its blur actions, as `InputFocusTracker` does in the View renderer. Unlike
 * [FocusManager.clearFocus] of the select window, this keeps the focus of other elements and also reaches an
 * input in another `ComposeView`.
 */
@DivContextScope
internal class InputFocus @Inject constructor() {
    private val focusedInputs = mutableSetOf<Input>()

    fun clear() {
        // An input leaves the set as soon as it loses focus.
        focusedInputs.toList().forEach { it.clearFocus() }
    }

    /**
     * A text input, kept in [focusedInputs] while it has focus. It clears the focus through the
     * [FocusManager] of its own window.
     */
    internal class Input : Modifier.Node(), FocusEventModifierNode, CompositionLocalConsumerModifierNode {
        private var inputFocus: InputFocus? = null

        override fun onFocusEvent(focusState: FocusState) {
            if (!focusState.isFocused) return forget()

            val inputFocus = (currentValueOf(LocalContext) as DivContext).component.inputFocus
            inputFocus.focusedInputs += this
            this.inputFocus = inputFocus
        }

        override fun onDetach() = forget()

        fun clearFocus() {
            currentValueOf(LocalFocusManager).clearFocus(force = true)
        }

        private fun forget() {
            inputFocus?.focusedInputs?.remove(this)
            inputFocus = null
        }
    }
}

/**
 * Adds the text input that this modifier is applied to into the [InputFocus] of its context while it has focus.
 */
internal fun Modifier.trackInputFocus(): Modifier = then(InputFocusElement)

private data object InputFocusElement : ModifierNodeElement<InputFocus.Input>() {
    override fun create() = InputFocus.Input()

    override fun update(node: InputFocus.Input) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "trackInputFocus"
    }
}
