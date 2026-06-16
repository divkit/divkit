package com.yandex.div.compose.views.modifiers

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import com.yandex.div.compose.actions.DivActions
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.tooltips.tooltipAnchors
import com.yandex.div.compose.utils.applyIf
import com.yandex.div.compose.utils.applyIfNotNull
import com.yandex.div.compose.utils.observeHorizontalInsets
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observeVerticalInsets
import com.yandex.div2.Div
import com.yandex.div2.DivAspect
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivVisibility

@Composable
internal fun Modifier.apply(
    div: Div,
    actions: DivActions?,
    applyMargins: Boolean
): Modifier {
    val data = div.value()

    var modifier = this

    if (applyMargins) {
        data.margins?.let {
            modifier = modifier.padding(it)
        }
    }

    val aspect = div.aspectRatioOrNull()

    modifier = modifier
        .width(data.width, data.alignmentHorizontal?.observedValue())
        .applyIfNotNull(aspect) { ratio ->
            aspectRatio(ratio)
        }
        .applyIf(aspect == null) {
            height(data.height, data.alignmentVertical?.observedValue())
        }
        .visibilityActions(data)

    val alphaValue = if (data.visibility.observedValue() == DivVisibility.VISIBLE) {
        data.alpha.observedFloatValue()
    } else {
        0f
    }
    data.transform?.let {
        modifier = modifier.transform(it)
    }

    data.border?.let {
        modifier = modifier.borderShadow(it, alphaValue)
    }

    if (alphaValue < 1f) {
        modifier = modifier.alpha(alphaValue)
    }

    data.border?.let {
        modifier = modifier.borderClip(it)
    }

    // The actions must be applied AFTER the transformations and the border clipping in order
    // to have correct touch and animation area.
    // The actions must be applied BEFORE the background so that the action animation is applied
    // to the background.
    actions?.let {
        modifier = modifier.actions(it)
    }

    data.background?.let {
        modifier = modifier.backgrounds(it)
    }

    data.border?.let {
        modifier = modifier.borderStroke(it)
    }

    data.id?.let {
        modifier = modifier.testTag(it)
    }

    return modifier
        .accessibility(data)
        .tooltipAnchors(data.tooltips)
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

@Composable
private fun Div.aspectRatioOrNull(): Float? {
    val ratio = aspectOrNull()?.ratio?.observedFloatValue() ?: return null
    return if (ratio > 0f) ratio else null
}

private fun Div.aspectOrNull(): DivAspect? = when (this) {
    is Div.Container -> value.aspect
    is Div.Image -> value.aspect
    is Div.GifImage -> value.aspect
    is Div.Video -> value.aspect
    else -> null
}
