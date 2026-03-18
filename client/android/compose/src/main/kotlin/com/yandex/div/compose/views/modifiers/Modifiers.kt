package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.yandex.div.compose.views.observedValue
import com.yandex.div.compose.views.toDp
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets

@Composable
internal fun Modifier.apply(div: Div): Modifier {
    val data = div.value()

    var modifier = this

    data.margins?.let {
        modifier = modifier.padding(it)
    }

    modifier = modifier
        .width(data.width, data.alignmentHorizontal?.observedValue())
        .height(data.height, data.alignmentVertical?.observedValue())
        .actions(div)

    data.border?.let {
        modifier = modifier.divBorderClip(it)
    }

    data.background?.let {
        modifier = modifier.backgrounds(it)
    }

    data.border?.let {
        modifier = modifier.divBorderStroke(it)
    }

    data.paddings?.let {
        modifier = modifier.padding(it)
    }

    data.id?.let {
        modifier = modifier.testTag(it)
    }

    return modifier
}

@Composable
private fun Modifier.padding(value: DivEdgeInsets): Modifier {
    return padding(
        start = (value.start ?: value.left).observedValue().toDp(),
        end = (value.end ?: value.right).observedValue().toDp(),
        top = value.top.observedValue().toDp(),
        bottom = value.bottom.observedValue().toDp()
    )
}
