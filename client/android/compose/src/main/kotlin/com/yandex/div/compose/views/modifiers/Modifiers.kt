package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.yandex.div.compose.views.evaluate
import com.yandex.div.compose.views.toDp
import com.yandex.div2.DivBase
import com.yandex.div2.DivEdgeInsets

@Composable
internal fun Modifier.apply(data: DivBase): Modifier {
    var modifier = this
        .width(data.width)
        .height(data.height)

    data.margins?.let {
        modifier = modifier.padding(it)
    }

    data.background?.let {
        modifier = modifier.backgrounds(it)
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
        start = (value.start ?: value.left).evaluate().toDp(),
        end = (value.end ?: value.right).evaluate().toDp(),
        top = value.top.evaluate().toDp(),
        bottom = value.bottom.evaluate().toDp()
    )
}
