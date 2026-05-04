package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.observedColorValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.variables.mutableStateFromVariable
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
            checkedTrackColor = onColor.copy(alpha = TRACK_CHECKED_ALPHA),
            checkedBorderColor = onColor.copy(alpha = TRACK_CHECKED_ALPHA),
        )
    } else {
        SwitchDefaults.colors()
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Switch(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it },
            enabled = isEnabled,
            colors = colors,
        )
    }
}

private const val TRACK_CHECKED_ALPHA = 0.3f
