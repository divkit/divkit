package com.yandex.div.compose.views.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import com.yandex.div.compose.views.transition.StateChangeTransitionScene

@Composable
internal fun StateChangeContentLayout(
    modifier: Modifier,
    entries: List<StateChangeContentEntry>,
    scene: StateChangeTransitionScene,
    content: @Composable () -> Unit,
) {
    val overlayModifier = Modifier.drawWithContent {
        drawContent()
        entries.forEach { entry ->
            entry.transitionState.overlayLayers().forEach { drawLayer(it.layer) }
        }
    }
    Layout(
        modifier = modifier
            .onGloballyPositioned {
                scene.update(it)
                entries.filter { entry -> entry.contentActive }.forEach { entry ->
                    entry.transitionState.snapshots.updateLayouts(scene)
                }
            }
            .then(overlayModifier),
        content = content,
    ) { measurables, constraints ->
        val contentConstraints = constraints.copy(minWidth = 0, minHeight = 0)
        val placeables = measurables.mapIndexed { index, measurable ->
            val entry = entries[index]
            val childConstraints = if (entry.contentActive || !entry.hasLayoutSize) {
                contentConstraints
            } else {
                Constraints.fixed(entry.layoutWidth, entry.layoutHeight)
            }
            measurable.measure(childConstraints).also { placeable ->
                if (entry.contentActive) {
                    entry.layoutWidth = placeable.width
                    entry.layoutHeight = placeable.height
                    entry.hasLayoutSize = true
                }
            }
        }
        val activePlaceable = entries
            .indexOfFirst { it.contentActive }
            .takeIf { it >= 0 }
            ?.let(placeables::get)
        layout(
            width = constraints.constrainWidth(activePlaceable?.width ?: 0),
            height = constraints.constrainHeight(activePlaceable?.height ?: 0),
        ) {
            placeables.forEach { it.place(0, 0) }
        }
    }
}
