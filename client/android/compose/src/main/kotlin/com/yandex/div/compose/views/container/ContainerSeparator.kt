package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toColor
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivShape

@Composable
internal fun ContainerSeparator(
    separator: DivContainer.Separator,
    modifier: Modifier = Modifier,
) {
    val style = separator.style as? DivDrawable.Shape ?: return
    val shapeDrawable = style.value
    val color = shapeDrawable.color.observedValue().toColor()
    val margins = separator.margins

    val modifier = modifier.padding(
        start = margins.startMarginDp(),
        end = margins.endMarginDp(),
        top = margins.topMarginDp(),
        bottom = margins.bottomMarginDp(),
    )

    when (val shape = shapeDrawable.shape) {
        is DivShape.RoundedRectangle -> {
            val rect = shape.value
            val cornerRadiusDp = rect.cornerRadius.value.observedValue().toDp()
            Spacer(
                modifier = modifier
                    .width(rect.itemWidth.value.observedValue().toDp())
                    .height(rect.itemHeight.value.observedValue().toDp())
                    .drawBehind {
                        drawRoundRect(
                            color = color,
                            cornerRadius = CornerRadius(cornerRadiusDp.toPx()),
                        )
                    }
            )
        }

        is DivShape.Circle -> {
            val circle = shape.value
            val radius = circle.radius.value.observedValue().toDp()
            val diameter = radius * 2
            Spacer(
                modifier = modifier
                    .size(diameter)
                    .drawBehind { drawCircle(color = color) }
            )
        }
    }
}

@Composable
private fun DivEdgeInsets?.startMarginDp(): Dp =
    this?.run { (start ?: left).observedValue().toDp() } ?: 0.dp

@Composable
private fun DivEdgeInsets?.endMarginDp(): Dp =
    this?.run { (end ?: right).observedValue().toDp() } ?: 0.dp

@Composable
private fun DivEdgeInsets?.topMarginDp(): Dp =
    this?.top.observedValue(0L).toDp()

@Composable
private fun DivEdgeInsets?.bottomMarginDp(): Dp =
    this?.bottom.observedValue(0L).toDp()
