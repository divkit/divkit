package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.placeCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
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
) {
    private var lastSyncedSource = source.value
    private var hasPendingSource = !config.accepts(lastSyncedSource)
    private var lastSyncedText = if (hasPendingSource) "" else config.storedText(lastSyncedSource)

    val field = TextFieldState(lastSyncedText, TextRange(lastSyncedText.length))

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
