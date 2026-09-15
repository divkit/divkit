package com.yandex.div.compose.views.input

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.TextRange
import com.yandex.div.compose.variables.mutableStateFromVariable
import com.yandex.div2.DivInput

@Composable
internal fun DivInput.rememberLengthLimitedInputState(maxLength: Int): LengthLimitedInputState {
    val source = mutableStateFromVariable(textVariable, defaultValue = "")
    val sourceText = source.value
    val filter = filters.orEmpty().observeFilter()
    val currentFilter = rememberUpdatedState(filter)
    val state = remember(source) { LengthLimitedInputState(source, maxLength, currentFilter) }
    SideEffect { state.syncSource(sourceText, maxLength, filter) }
    LaunchedEffect(state) { state.sync() }
    return state
}

internal class LengthLimitedInputState(
    private val source: MutableState<String>,
    maxLength: Int,
    private val filter: State<(String) -> Boolean>,
) {
    private var lastSyncedSource = source.value
    private var hasPendingSource = !filter.value(lastSyncedSource)
    private var lastSyncedDisplay = if (!hasPendingSource) {
        lastSyncedSource.limitLength(maxLength)
    } else {
        ""
    }

    val field = TextFieldState(lastSyncedDisplay, TextRange(lastSyncedDisplay.length))

    @Composable
    fun rememberInputTransformation(maxLength: Int): InputTransformation {
        return remember(this, maxLength) {
            MaxLengthInputTransformation(maxLength).then(InputTransformation {
                if (!asCharSequence().contentEquals(originalText) && !filter.value(toString())) {
                    revertAllChanges()
                }
            })
        }
    }

    suspend fun sync() {
        snapshotFlow { field.text.toString() }
            .collect { displayText ->
                if (displayText != lastSyncedDisplay && source.value == lastSyncedSource) {
                    lastSyncedDisplay = displayText
                    lastSyncedSource = displayText
                    hasPendingSource = false
                    source.value = displayText
                }
            }
    }

    fun syncSource(text: String, maxLength: Int, predicate: (String) -> Boolean) {
        if (source.value != text) return
        if (text == lastSyncedSource && !(hasPendingSource && predicate(text))) return
        lastSyncedSource = text
        hasPendingSource = !predicate(text)
        if (!hasPendingSource) {
            lastSyncedDisplay = text.limitLength(maxLength)
            if (!field.text.contentEquals(lastSyncedDisplay)) {
                field.edit {
                    replace(0, length, lastSyncedDisplay)
                    selection = TextRange(length)
                }
            }
        }
    }
}
