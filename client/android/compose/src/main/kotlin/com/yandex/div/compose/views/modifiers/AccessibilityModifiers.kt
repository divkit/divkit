package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivBase
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivInput
import com.yandex.div2.DivSelect
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText

@Composable
internal fun Modifier.accessibility(divBase: DivBase): Modifier {
    val accessibility = divBase.accessibility ?: return this

    val mode = accessibility.mode.observedValue()
    if (mode == DivAccessibility.Mode.EXCLUDE) {
        return clearAndSetSemantics { }
    }

    val explicitType = accessibility.type
    val type = if (explicitType != DivAccessibility.Type.AUTO) explicitType else divBase.toDivAccessibilityType()
    val isMerge = mode == DivAccessibility.Mode.MERGE

    if (!accessibility.hasSemantics(type, isMerge)) {
        return this
    }

    val contentDesc = accessibility.observeContentDescription()
    val stateDesc = accessibility.stateDescription?.observedValue()
    val isChecked = if (type.isCheckable) accessibility.isChecked?.observedValue() else null

    return semantics(mergeDescendants = isMerge) {
        contentDesc?.let { contentDescription = it }
        stateDesc?.let { stateDescription = it }
        isChecked?.let { toggleableState = if (it) ToggleableState.On else ToggleableState.Off }
        type.composeRole?.let { role = it }
        if (type.isHeading) heading()
    }
}

@Composable
private fun DivAccessibility.observeContentDescription(): String? {
    val description = description?.observedValue()
    val hint = hint?.observedValue()
    return when {
        description == null -> hint
        hint == null -> description
        description == hint -> description
        else -> "$description\n$hint"
    }
}

private fun DivAccessibility.hasSemantics(
    type: DivAccessibility.Type,
    isMerge: Boolean,
): Boolean {
    return description != null
            || hint != null
            || stateDescription != null
            || (isChecked != null && type.isCheckable)
            || type.composeRole != null
            || type.isHeading
            || isMerge
}

private fun DivBase.toDivAccessibilityType(): DivAccessibility.Type = when (this) {
    is DivInput -> DivAccessibility.Type.EDIT_TEXT
    is DivText -> DivAccessibility.Type.TEXT
    is DivTabs -> DivAccessibility.Type.TAB_BAR
    is DivSelect -> DivAccessibility.Type.SELECT
    is DivImage, is DivGifImage -> DivAccessibility.Type.IMAGE
    else -> DivAccessibility.Type.NONE
}

private val DivAccessibility.Type.composeRole: Role?
    get() = when (this) {
        DivAccessibility.Type.BUTTON -> Role.Button
        DivAccessibility.Type.IMAGE -> Role.Image
        DivAccessibility.Type.CHECKBOX -> Role.Checkbox
        DivAccessibility.Type.RADIO -> Role.RadioButton
        DivAccessibility.Type.TAB_BAR -> Role.Tab
        DivAccessibility.Type.SELECT -> Role.DropdownList
        else -> null
    }

private val DivAccessibility.Type.isHeading: Boolean
    get() = this == DivAccessibility.Type.HEADER

private val DivAccessibility.Type.isCheckable: Boolean
    get() = this == DivAccessibility.Type.CHECKBOX
            || this == DivAccessibility.Type.RADIO
