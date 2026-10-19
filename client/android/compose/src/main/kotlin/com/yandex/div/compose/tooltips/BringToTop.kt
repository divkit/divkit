package com.yandex.div.compose.tooltips

import android.view.View
import android.view.ViewTreeObserver
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionOnScreen
import androidx.compose.ui.platform.LocalView
import com.yandex.div.compose.context.LocalDivViewContext

internal val LocalTooltipContent = staticCompositionLocalOf { false }

@Composable
internal fun Modifier.tooltipBringToTop(id: String?): Modifier {
    if (id == null || LocalTooltipContent.current) return this
    val storage = LocalDivViewContext.current.component.tooltipStateStorage
    if (!storage.isBringToTopTarget(id)) return this

    val layer = rememberGraphicsLayer()
    val view = LocalView.current
    val entry = remember(id, layer, view) { BringToTopEntry(id, layer, view) }
    DisposableEffect(entry) {
        storage.register(entry)
        onDispose { storage.unregister(entry) }
    }
    return onGloballyPositioned { entry.coordinates = it }
        .drawWithContent {
            layer.record { this@drawWithContent.drawContent() }
            drawLayer(layer)
            entry.isDrawn = true
        }
}

@Composable
internal fun BringToTop(id: String?, modifier: Modifier) {
    if (id == null) return
    val storage = LocalDivViewContext.current.component.tooltipStateStorage
    val entry = storage.getBringToTopEntry(id) ?: return
    var popupPosition by remember { mutableStateOf<Offset?>(null) }
    val sourceToScreen = remember { Matrix() }
    DisposableEffect(entry) {
        val observer = entry.view.viewTreeObserver
        val listener = ViewTreeObserver.OnDrawListener {
            // A nested layer can change without recording the outer source layer again.
            if (entry.isDrawn) entry.isDrawn = true
        }
        observer.addOnDrawListener(listener)
        onDispose { if (observer.isAlive) observer.removeOnDrawListener(listener) }
    }
    Spacer(
        modifier.onGloballyPositioned { popupPosition = it.positionOnScreen() }
            .drawBehind {
                val source = entry.coordinates ?: return@drawBehind
                val popup = popupPosition ?: return@drawBehind
                if (source.isAttached && entry.isDrawn && !entry.layer.isReleased) {
                    sourceToScreen.reset()
                    source.transformToScreen(sourceToScreen)
                    withTransform({
                        translate(left = -popup.x, top = -popup.y)
                        transform(sourceToScreen)
                    }) {
                        drawLayer(entry.layer)
                    }
                }
            }
    )
}

internal class BringToTopEntry(val id: String, val layer: GraphicsLayer, val view: View) {
    var coordinates by mutableStateOf<LayoutCoordinates?>(null)

    // The replica is in another window, so recording also invalidates its drawing.
    var isDrawn by mutableStateOf(false, neverEqualPolicy())
}
