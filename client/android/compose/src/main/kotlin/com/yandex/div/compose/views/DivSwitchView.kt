package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.variables.mutableStateFromVariable
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

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // The element is sized by its own layout properties, so the switch must not add the
        // Material minimum touch target padding around its track.
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides 0.dp) {
            Switch(
                checked = checkedState.value,
                onCheckedChange = { checkedState.value = it },
                enabled = isEnabled,
                colors = colors,
            )
        }
    }
}

private const val TRACK_CHECKED_ALPHA_FACTOR = 0.3f
