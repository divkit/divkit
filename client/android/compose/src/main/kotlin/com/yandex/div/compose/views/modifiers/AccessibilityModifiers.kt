package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivAccessibility.Type
import com.yandex.div2.DivBase
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivInput
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText

@Composable
internal fun Modifier.accessibility(data: DivBase): Modifier {
    val accessibility = data.accessibility ?: return this

    val mode = accessibility.mode.observedValue()
    if (mode == Mode.EXCLUDE) {
        return clearAndSetSemantics { }
    }

    val type = if (accessibility.type != Type.AUTO) {
        accessibility.type
    } else {
        data.defaultAccessibilityType
    }
    val role = if (accessibility.type != Type.AUTO) type.role else data.defaultRole

    val isMerge = mode == Mode.MERGE

    if (!accessibility.hasSemantics(type, role, isMerge)) {
        return this
    }

    val contentDescription = accessibility.observeContentDescription()
    val stateDescription = accessibility.stateDescription?.observedValue()
    val isChecked = if (type.isCheckable) accessibility.isChecked?.observedValue() else null
    val properties: (SemanticsPropertyReceiver.() -> Unit) = {
        contentDescription?.let { this.contentDescription = it }
        stateDescription?.let { this.stateDescription = it }
        isChecked?.let { toggleableState = if (it) ToggleableState.On else ToggleableState.Off }
        role?.let { this.role = it }
        if (type.isHeader) {
            heading()
        }
    }
    return when {
        !isMerge -> semantics(properties = properties)
        data.keepsControlSemanticsOnMerge -> semantics(mergeDescendants = true, properties = properties)
        else -> clearAndSetSemantics(properties = properties)
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

private fun DivAccessibility.hasSemantics(type: Type, role: Role?, isMerge: Boolean): Boolean {
    return description != null
            || hint != null
            || stateDescription != null
            || (isChecked != null && type.isCheckable)
            || role != null
            || type.isHeader
            || isMerge
}

/** Controls without nested elements: merging must not clear the control's own semantics. */
private val DivBase.keepsControlSemanticsOnMerge: Boolean
    get() = this is DivSwitch

private val DivBase.defaultRole: Role?
    get() = when (this) {
        is DivSwitch -> Role.Switch
        else -> defaultAccessibilityType.role
    }

private val DivBase.defaultAccessibilityType: Type
    get() {
        return when (this) {
            is DivInput -> Type.EDIT_TEXT
            is DivText -> Type.TEXT
            is DivTabs -> Type.TAB_BAR
            is DivSelect -> Type.SELECT
            is DivImage, is DivGifImage -> Type.IMAGE
            else -> Type.NONE
        }
    }

private val Type.role: Role?
    get() = when (this) {
        Type.BUTTON -> Role.Button
        Type.CHECKBOX -> Role.Checkbox
        Type.IMAGE -> Role.Image
        Type.RADIO -> Role.RadioButton
        Type.SELECT -> Role.DropdownList
        Type.TAB_BAR -> Role.Tab
        else -> null
    }

private val Type.isHeader: Boolean
    get() = this == Type.HEADER

private val Type.isCheckable: Boolean
    get() = when (this) {
        Type.CHECKBOX, Type.RADIO -> true
        else -> false
    }
