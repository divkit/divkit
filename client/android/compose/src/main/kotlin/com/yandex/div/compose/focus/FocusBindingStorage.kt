package com.yandex.div.compose.focus

import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.SoftwareKeyboardController
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivViewScope
import javax.inject.Inject

@DivViewScope
internal class FocusBindingStorage @Inject constructor(
    private val reporter: DivReporter,
) {
    class Binding {
        var hasFocus: Boolean = false
        var focusManager: FocusManager? = null
        var keyboardController: SoftwareKeyboardController? = null
    }

    /** An element with an `id`, as the actions that address an element find it. */
    open class Target(val focusRequester: FocusRequester)

    /** An input: the element and the [text] it shows. */
    class InputTarget(focusRequester: FocusRequester, val text: InputText) : Target(focusRequester)

    /** The text an input shows: the formatted text of a masked input. */
    interface InputText {
        /**
         * The length of the shown text, after showing the current value of the variable of the input:
         * the View renderer keeps its field in step with the variable synchronously.
         */
        fun length(): Int

        /**
         * Focuses the input, opening its keyboard, and selects the characters between [start] and
         * [end] of the shown text; a reversed range is allowed, an end behind the text is its end.
         */
        fun setCursorPosition(start: Int, end: Int)
    }

    private val bindings = mutableSetOf<Binding>()
    private val targets = mutableMapOf<String, MutableList<Target>>()
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

    fun register(id: String, target: Target) {
        targets.getOrPut(id) { mutableListOf() }.add(target)
    }

    fun unregister(id: String, target: Target) {
        val registered = targets[id] ?: return
        registered.remove(target)
        if (registered.isEmpty()) targets.remove(id)
    }

    /**
     * The single element with the [id], as the View renderer finds a view by its tag: a missing or
     * an ambiguous id is reported and gives null.
     */
    fun findTarget(id: String): Target? = single(id, targets[id].orEmpty())

    /**
     * The focus requester of the single element with the [id], or null: a missing or an ambiguous
     * id is not reported here, as `next_focus_ids` resolves it on every focus move.
     */
    fun getFocusRequester(id: String): FocusRequester? = targets[id]?.singleOrNull()?.focusRequester

    /** The single input with the [id]; an element of another type with the id does not count. */
    fun findInput(id: String): InputTarget? {
        return single(id, targets[id].orEmpty().filterIsInstance<InputTarget>())
    }

    private fun <T : Target> single(id: String, found: List<T>): T? {
        return when (found.size) {
            1 -> found.single()
            0 -> null.also { reporter.reportError("Element with id '$id' not found") }
            else -> null.also { reporter.reportError("Element with id '$id' is ambiguous") }
        }
    }
}
