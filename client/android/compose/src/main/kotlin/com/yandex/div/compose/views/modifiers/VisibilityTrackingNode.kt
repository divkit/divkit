package com.yandex.div.compose.views.modifiers

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.LayoutAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.UnplacedAwareModifierNode
import androidx.compose.ui.platform.InspectorInfo

internal fun Modifier.onDivVisibilityChanged(
    minFractionVisible: Float = 1f,
    onVisibilityChanged: (Boolean) -> Unit
): Modifier = this then VisibilityTrackingElement(
    minFractionVisible = minFractionVisible,
    onVisibilityChanged = onVisibilityChanged
)

private data class VisibilityTrackingElement(
    private val minFractionVisible: Float,
    private val onVisibilityChanged: (Boolean) -> Unit
) : ModifierNodeElement<VisibilityTrackingNode>() {

    override fun create(): VisibilityTrackingNode = VisibilityTrackingNode(
        minFractionVisible = minFractionVisible,
        onVisibilityChanged = onVisibilityChanged
    )

    override fun update(node: VisibilityTrackingNode) {
        node.minFractionVisible = minFractionVisible
        node.onVisibilityChanged = onVisibilityChanged
        node.onPropertiesUpdated()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "onDivVisibilityChanged"
        properties["minFractionVisible"] = minFractionVisible
    }
}

private class VisibilityTrackingNode(
    var minFractionVisible: Float,
    var onVisibilityChanged: (Boolean) -> Unit
) : Modifier.Node(),
    LayoutAwareModifierNode,
    UnplacedAwareModifierNode {

    private var isVisible = false
    private var currentFraction = 0f

    override fun onPlaced(coordinates: LayoutCoordinates) {
        updateFraction(coordinates)
    }

    override fun onUnplaced() {
        currentFraction = 0f
        notifyInvisible()
    }

    override fun onDetach() {
        notifyInvisible()
        super.onDetach()
    }

    fun onPropertiesUpdated() = handleVisibilityChange()

    private fun notifyInvisible() {
        if (isVisible) {
            isVisible = false
            onVisibilityChanged(false)
        }
    }

    private fun updateFraction(coordinates: LayoutCoordinates) {
        val newFraction = calculateVisibilityFraction(coordinates)
        if (newFraction != currentFraction) {
            currentFraction = newFraction
            handleVisibilityChange()
        }
    }

    private fun handleVisibilityChange() {
        val aboveThreshold = currentFraction >= minFractionVisible
        if (aboveThreshold == isVisible) return
        isVisible = aboveThreshold
        onVisibilityChanged(aboveThreshold)
    }

    private fun calculateVisibilityFraction(coordinates: LayoutCoordinates): Float {
        if (!coordinates.isAttached) return 0f

        val size = coordinates.size
        val rectArea = size.width.toFloat() * size.height.toFloat()
        if (rectArea <= 0f) return 0f

        val parentBounds = getParentBounds(coordinates) ?: return 0f

        val elementBounds = coordinates.boundsInWindow()
        val visibleBounds = elementBounds.intersect(parentBounds)
        val visibleArea = if (visibleBounds.isEmpty) 0f else visibleBounds.width * visibleBounds.height

        return (visibleArea / rectArea).coerceIn(0f, 1f)
    }

    private fun getParentBounds(coordinates: LayoutCoordinates): Rect? {
        var currentParent = coordinates.parentLayoutCoordinates
        var clipBounds: Rect? = null
        while (currentParent != null && currentParent.isAttached) {
            clipBounds = clipBounds?.intersect(currentParent.boundsInWindow()) ?: currentParent.boundsInWindow()
            currentParent = currentParent.parentLayoutCoordinates
        }
        return clipBounds
    }
}
