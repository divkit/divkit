package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.yandex.div.compose.views.observedValue
import com.yandex.div.compose.views.container.wrap.ContainerWrapHorizontalView
import com.yandex.div.compose.views.container.wrap.ContainerWrapVerticalView
import com.yandex.div2.DivContainer

@Composable
internal fun DivContainerView(
    modifier: Modifier,
    data: DivContainer
) {
    val orientation = data.orientation.observedValue()
    val clipToBounds = data.clipToBounds.observedValue()
    val layoutMode = data.layoutMode.observedValue()

    val modifier = if (clipToBounds) modifier.clipToBounds() else modifier

    when (orientation) {
        DivContainer.Orientation.HORIZONTAL -> when (layoutMode) {
            DivContainer.LayoutMode.WRAP -> ContainerWrapHorizontalView(
                modifier = modifier,
                data = data,
            )
            else -> ContainerHorizontalView(
                modifier = modifier,
                data = data,
            )
        }

        DivContainer.Orientation.VERTICAL -> when (layoutMode) {
            DivContainer.LayoutMode.WRAP -> ContainerWrapVerticalView(
                modifier = modifier,
                data = data,
            )
            else -> ContainerVerticalView(
                modifier = modifier,
                data = data,
            )
        }

        DivContainer.Orientation.OVERLAP -> ContainerOverlapView(
            modifier = modifier,
            data = data,
        )
    }
}
