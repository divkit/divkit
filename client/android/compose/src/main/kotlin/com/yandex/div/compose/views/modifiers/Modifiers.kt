package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.apply(
    div: Div,
    applyMargins: Boolean = true,
): Modifier {
    val data = div.value()

    var modifier = this

    if (applyMargins) {
        data.margins?.let {
            modifier = modifier.padding(it)
        }
    }

    modifier = modifier
        .width(data.width, data.alignmentHorizontal?.observedValue())
        .height(data.height, data.alignmentVertical?.observedValue())
        .actions(div)


    val alphaValue = if (data.visibility.observedValue() == DivVisibility.VISIBLE) {
        data.alpha.observedValue().toFloat()
    } else {
        0f
    }
    if (alphaValue < 1f) {
        modifier = modifier.alpha(alphaValue)
    }

    data.border?.let {
        modifier = modifier.divBorderClip(it)
    }

    data.background?.let {
        modifier = modifier.backgrounds(it)
    }

    data.border?.let {
        modifier = modifier.divBorderStroke(it)
    }

    data.id?.let {
        modifier = modifier.testTag(it)
    }

    return modifier
}

@Composable
internal fun Modifier.applyPaddings(div: Div): Modifier {
    val paddings = div.value().paddings ?: return this
    return padding(paddings)
}

@Composable
private fun Modifier.padding(value: DivEdgeInsets): Modifier {
    return padding(
        start = (value.start ?: value.left).observedValue().toDp(),
        end = (value.end ?: value.right).observedValue().toDp(),
        top = value.top.observedValue().toDp(),
        bottom = value.bottom.observedValue().toDp(),
    )
}

@Composable
internal fun Modifier.verticalPaddings(value: DivEdgeInsets): Modifier {
    return padding(
        top = value.top.observedValue().toDp(),
        bottom = value.bottom.observedValue().toDp(),
    )
}

@Composable
internal fun Modifier.horizontalPaddings(value: DivEdgeInsets): Modifier {
    return padding(
        start = (value.start ?: value.left).observedValue().toDp(),
        end = (value.end ?: value.right).observedValue().toDp(),
    )
}
