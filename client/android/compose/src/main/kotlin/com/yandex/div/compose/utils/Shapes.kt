package com.yandex.div.compose.utils

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivCornersRadius

@Composable
internal fun observedRoundedCornerShape(
    cornerRadius: Expression<Long>?,
    cornersRadius: DivCornersRadius?
): Shape? {
    val singleRadius = cornerRadius?.observedValue()?.toDp()
    return when {
        cornersRadius != null -> {
            val default = singleRadius ?: 0.dp
            RoundedCornerShape(
                topStart = cornersRadius.topLeft?.observedValue()?.toDp() ?: default,
                topEnd = cornersRadius.topRight?.observedValue()?.toDp() ?: default,
                bottomStart = cornersRadius.bottomLeft?.observedValue()?.toDp() ?: default,
                bottomEnd = cornersRadius.bottomRight?.observedValue()?.toDp() ?: default,
            )
        }
        singleRadius != null -> RoundedCornerShape(singleRadius)
        else -> null
    }
}
