package com.yandex.div.compose.views.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.container.wrap.ContainerWrapHorizontalView
import com.yandex.div.compose.views.container.wrap.ContainerWrapVerticalView
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContainer.LayoutMode
import com.yandex.div2.DivContainer.Orientation

@Composable
internal fun DivContainerView(
    modifier: Modifier,
    data: DivContainer
) {
    if (data.itemBuilder != null) {
        reportError("div-container.item_builder not supported")
    }

    val modifier = if (data.clipToBounds.observedValue()) {
        modifier.clipToBounds()
    } else {
        modifier
    }

    when (data.orientation.observedValue()) {
        Orientation.HORIZONTAL -> when (data.layoutMode.observedValue()) {
            LayoutMode.WRAP -> ContainerWrapHorizontalView(modifier, data)
            LayoutMode.NO_WRAP -> ContainerHorizontalView(modifier, data)
        }

        Orientation.VERTICAL -> when (data.layoutMode.observedValue()) {
            LayoutMode.WRAP -> ContainerWrapVerticalView(modifier, data)
            LayoutMode.NO_WRAP -> ContainerVerticalView(modifier, data)
        }

        Orientation.OVERLAP -> ContainerOverlapView(modifier, data)
    }
}
