package com.yandex.div.compose.views.indicator

import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedColorValue
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div.compose.expressions.observedIntValue
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.utils.toPx
import com.yandex.div2.DivDefaultIndicatorItemPlacement
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivIndicatorItemPlacement

internal data class IndicatorStyle(
    val activeShape: ShapeParams,
    val inactiveShape: ShapeParams,
    val minimumShape: ShapeParams,
    val spaceBetweenCenters: Float,
    val itemSpacing: Float,
    val maxVisibleItems: Int,
    val isStretch: Boolean,
    val animation: DivIndicator.Animation,
)

@Composable
internal fun DivIndicator.observeIndicatorStyle(): IndicatorStyle {
    val activeItemColor = activeItemColor.observedColorValue()
    val inactiveItemColor = inactiveItemColor.observedColorValue()
    val activeItemSize = activeItemSize.observedFloatValue()
    val minimumItemSize = minimumItemSize.observedFloatValue()

    val inactiveShape = observeInactiveShape(activeItemSize, minimumItemSize, inactiveItemColor)
    val activeShape = activeShape?.toShapeParams(activeItemColor)
        ?: inactiveShape.copy(color = activeItemColor).scale(activeItemSize)
    val minimumShape = inactiveMinimumShape?.toShapeParams(inactiveItemColor)
        ?: inactiveShape.scale(minimumItemSize)

    val placement = itemsPlacement
        ?: DivIndicatorItemPlacement.Default(DivDefaultIndicatorItemPlacement(spaceBetweenCenters))

    return when (placement) {
        is DivIndicatorItemPlacement.Default -> IndicatorStyle(
            activeShape = activeShape,
            inactiveShape = inactiveShape,
            minimumShape = minimumShape,
            spaceBetweenCenters = placement.value.spaceBetweenCenters.observedValue().toPx(),
            itemSpacing = 0f,
            maxVisibleItems = Int.MAX_VALUE,
            isStretch = false,
            animation = animation.observedValue(),
        )
        is DivIndicatorItemPlacement.Stretch -> IndicatorStyle(
            activeShape = activeShape,
            inactiveShape = inactiveShape,
            minimumShape = minimumShape,
            spaceBetweenCenters = 0f,
            itemSpacing = placement.value.itemSpacing.observedValue().toPx(),
            maxVisibleItems = placement.value.maxVisibleItems.observedIntValue(),
            isStretch = true,
            animation = animation.observedValue(),
        )
    }
}
