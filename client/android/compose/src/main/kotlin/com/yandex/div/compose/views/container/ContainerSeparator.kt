package com.yandex.div.compose.views.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.modifiers.padding
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape

@Composable
internal fun ContainerSeparator(
    separator: DivContainer.Separator,
    modifier: Modifier = Modifier
) {
    when (val style = separator.style) {
        is DivDrawable.Shape -> {
            val color = style.value.color.observedColorValue()
            val modifier = modifier.padding(separator.margins)
            when (val shape = style.value.shape) {
                is DivShape.RoundedRectangle -> {
                    val rect = shape.value
                    Spacer(
                        modifier = modifier
                            .width(rect.itemWidth.observedValue())
                            .height(rect.itemHeight.observedValue())
                            .background(
                                color = color,
                                shape = RoundedCornerShape(rect.cornerRadius.observedValue())
                            )
                    )
                }

                is DivShape.Circle -> {
                    Spacer(
                        modifier = modifier
                            .size(shape.value.radius.observedValue() * 2)
                            .background(color, CircleShape)
                    )
                }
            }
        }
    }
}
