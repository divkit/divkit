package com.yandex.div.compose.extensions.pinchtozoom

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div2.Div

/**
 * A [DivExtensionHandler] that adds pinch-to-zoom gesture support to `image` elements.
 */
class PinchToZoomExtensionHandler(
    private val configuration: PinchToZoomConfiguration = PinchToZoomConfiguration()
) : DivExtensionHandler {

    @Composable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (Modifier) -> Unit
    ) {
        if (environment.data !is Div.Image) {
            environment.reportError("Extension pinch-to-zoom can be applied for images only.")
            content(modifier)
            return
        }

        val state = remember { mutableStateOf<PinchToZoomState?>(null) }
        val coordinates = remember { mutableStateOf<LayoutCoordinates?>(null) }

        content(
            modifier
                .onGloballyPositioned { coordinates.value = it }
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        var zooming = false

                        while (true) {
                            val event = awaitPointerEvent()
                            val pointers = event.changes.filter { it.pressed }

                            when {
                                !zooming && pointers.size >= 2 -> {
                                    val bounds = coordinates.value?.boundsInWindow() ?: break
                                    val centroid = event.calculateCentroid()
                                    state.value = PinchToZoomState(
                                        bounds = bounds,
                                        pivot = centroid - Offset(bounds.left, bounds.top),
                                        dimColor = configuration.dimColor,
                                        animationsEnabled = environment.animationsEnabled
                                    )
                                    zooming = true
                                    event.changes.forEach { it.consume() }
                                }

                                zooming -> {
                                    state.value = state.value?.updated(
                                        zoom = event.calculateZoom(),
                                        pan = event.calculatePan()
                                    )
                                    event.changes.forEach { it.consume() }
                                    if (pointers.isEmpty()) {
                                        state.value = state.value?.finishing()
                                        break
                                    }
                                }

                                pointers.isEmpty() -> break
                            }
                        }
                    }
                }
        )
        ZoomHost(state, content)
    }
}
