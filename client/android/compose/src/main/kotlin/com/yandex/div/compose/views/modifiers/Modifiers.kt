package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div.compose.utils.observedFloatValue
import com.yandex.div.compose.utils.observedValue
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
        .visibilityActions(data)

    val alphaValue = if (data.visibility.observedValue() == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }
    if (alphaValue < 1f) {
        modifier = modifier.alpha(alphaValue)
    }

    data.transform?.let {
        modifier = modifier.transform(it)
    }

    data.border?.let {
        modifier = modifier.borderClip(it)
    }

    data.background?.let {
        modifier = modifier.backgrounds(it)
    }

    data.border?.let {
        modifier = modifier.borderStroke(it)
    }

    modifier = modifier.accessibility(data)

    data.id?.let {
        modifier = modifier.testTag(it)
    }

    return modifier
}

@Composable
internal fun Modifier.applyPaddings(data: Div): Modifier {
    return this.padding(data.value().paddings)
}

@Composable
internal fun Modifier.padding(value: DivEdgeInsets?): Modifier {
    val insets = value.observeInsets()
    return padding(
        start = insets.calculateStartPadding(LayoutDirection.Ltr),
        end = insets.calculateEndPadding(LayoutDirection.Ltr),
        top = insets.calculateTopPadding(),
        bottom = insets.calculateBottomPadding(),
    )
}

@Composable
internal fun Modifier.verticalPaddings(value: DivEdgeInsets): Modifier {
    val (top, bottom) = value.observeVerticalInsets()
    return padding(
        top = top,
        bottom = bottom,
    )
}

@Composable
internal fun Modifier.horizontalPaddings(value: DivEdgeInsets): Modifier {
    val (start, end) = value.observeHorizontalInsets()
    return padding(
        start = start,
        end = end,
    )
}
