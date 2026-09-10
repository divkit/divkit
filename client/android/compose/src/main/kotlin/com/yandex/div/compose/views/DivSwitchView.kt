package com.yandex.div.compose.views

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.variables.mutableStateFromVariable
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.DivSwitch

@Composable
internal fun DivSwitchView(
    modifier: Modifier,
    data: DivSwitch,
) {
    val checkedState = mutableStateFromVariable(data.isOnVariable, defaultValue = false)
    val isEnabled = data.isEnabled.observedValue()
    val onColor = data.onColor?.observedColorValue()

    val colors = if (onColor != null) {
        SwitchDefaults.colors(
            checkedThumbColor = onColor,
            checkedTrackColor = onColor.copy(alpha = onColor.alpha * TRACK_CHECKED_ALPHA_FACTOR),
        )
    } else {
        SwitchDefaults.colors()
    }

    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .toggleable(
                value = checkedState.value,
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled,
                role = Role.Switch,
                onValueChange = { checkedState.value = it },
            )
            .padding(data.paddings),
        contentAlignment = Alignment.Center,
    ) {
        // The whole element is the interactive node, so the switch takes no handler of its own:
        // without it Material adds neither a second toggleable node nor the minimum touch target
        // padding around the track, and the element keeps the size of its layout properties.
        Switch(
            checked = checkedState.value,
            onCheckedChange = null,
            enabled = isEnabled,
            colors = colors,
            interactionSource = interactionSource,
        )
    }
}

private const val TRACK_CHECKED_ALPHA_FACTOR = 0.3f
