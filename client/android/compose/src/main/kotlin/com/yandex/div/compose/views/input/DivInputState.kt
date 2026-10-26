package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.foundation.text.input.selectAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.TextRange
import com.yandex.div.compose.focus.FocusBindingStorage
import com.yandex.div.compose.variables.mutableStateFromVariable
import com.yandex.div2.DivInput

@Composable
internal fun DivInput.rememberInputState(config: InputConfig): DivInputState {
    val rawTextVariable = mask?.value()?.rawTextVariable
    val source = mutableStateFromVariable(rawTextVariable ?: textVariable, defaultValue = "")
    val formatted = if (rawTextVariable == null) null else mutableStateFromVariable(textVariable, defaultValue = "")
    val state = remember(source, formatted) { DivInputState(source, formatted, config) }
    val sourceText = source.value
    SideEffect { state.syncSource(sourceText, config) }
    LaunchedEffect(state) { state.sync() }
    return state
}

/**
 * The text of an input, bound to its variable: `text_variable`, or `raw_text_variable` of a masked
 * input, whose `text_variable` then receives the [formatted] text.
 */
internal class DivInputState(
    private val source: MutableState<String>,
    private val formatted: MutableState<String>?,
    private var config: InputConfig,
) : FocusBindingStorage.InputText {
    private var lastSyncedSource = source.value
    private var hasPendingSource = !config.accepts(lastSyncedSource)
    private var lastSyncedText = if (hasPendingSource) "" else config.storedText(lastSyncedSource)

    val field = TextFieldState(lastSyncedText, TextRange(lastSyncedText.length))
    val focusRequester = FocusRequester()
    private var hasFocus = false
    private var actionSelection: TextRange? = null

    override fun length(): Int {
        showSource()
        return shownText().length
    }

    /** Positions of the shown text address the raw characters of a masked input between them. */
    override fun setCursorPosition(start: Int, end: Int) {
        focusRequester.requestFocus()
        // An `on_focus` action may have written the variable: the selection addresses the text the
        // input shows afterwards, as in the View renderer, where the variable reaches the field at once.
        showSource()
        val mask = config.mask
        val raw = field.text.toString()
        val range = if (mask == null) {
            TextRange(start.coerceAtMost(raw.length), end.coerceAtMost(raw.length))
        } else {
            TextRange(mask.rawOffset(raw, start), mask.rawOffset(raw, end))
        }
        // The focus gained here is handled after this call, so the selection must outlive it, as in
        // the View renderer, where `select_all_on_focus` runs inside the focus request.
        actionSelection = if (hasFocus) range else null
        field.edit { selection = range }
    }

    /** Shows the current value of the variable at once, instead of after the next composition. */
    private fun showSource() = syncSource(source.value, config)

    /** The text the field shows: the formatted text of a masked input, formatted by [config]. */
    fun shownText(config: InputConfig = this.config): String {
        return config.mask?.format(field.text.toString()) ?: field.text.toString()
    }

    /**
     * Applies `select_all_on_focus` to a gained focus, unless the input still shows the selection
     * [setCursorPosition] gave it together with that focus.
     */
    fun onFocusChanged(isFocused: Boolean, selectAllOnFocus: Boolean) {
        val kept = actionSelection
        actionSelection = null
        if (isFocused && selectAllOnFocus && field.selection != kept) field.edit { selectAll() }
    }

    /** Follows the focus of the field as it changes; a lost focus was not the one [setCursorPosition] kept. */
    fun trackFocus(hasFocus: Boolean) {
        this.hasFocus = hasFocus
        if (!hasFocus) actionSelection = null
    }

    /**
     * Shows the [text] of the variable when it was changed outside the field, or shows it through a
     * changed mask: the variable is kept as written. A value the filters reject is kept until a filter
     * change accepts it.
     */
    fun syncSource(text: String, config: InputConfig) {
        val maskChanged = config.mask !== this.config.mask
        this.config = config
        if (source.value != text) return
        val sourceChanged = text != lastSyncedSource || maskChanged
        if (sourceChanged || hasPendingSource && config.accepts(text)) {
            lastSyncedSource = text
            hasPendingSource = !config.accepts(text)
            if (!hasPendingSource) show(config.storedText(text))
        }
        // The collector below does not run for an unchanged text, which a new mask formats differently.
        if (maskChanged) writeFormatted(field.text.toString())
    }

    private fun show(text: String) {
        lastSyncedText = text
        if (!field.text.contentEquals(text)) {
            field.edit {
                replace(0, length, text)
                placeCursorAtEnd()
            }
        }
    }

    /** Writes the edits of the field to the variables. */
    suspend fun sync() {
        snapshotFlow { field.text.toString() }.collect { text ->
            if (text != lastSyncedText && source.value == lastSyncedSource) {
                lastSyncedText = text
                writeSource(config.mask?.toVariableValue(text) ?: text)
            }
            writeFormatted(text)
        }
    }

    private fun writeSource(value: String) {
        lastSyncedSource = value
        hasPendingSource = false
        source.value = value
        // An observer of the variable may have replaced the written value at once.
        if (source.value != value) syncSource(source.value, config)
    }

    private fun writeFormatted(text: String) {
        val mask = config.mask
        if (formatted == null || mask == null) return
        val formattedText = mask.format(text)
        if (formatted.value != formattedText) formatted.value = formattedText
    }
}
