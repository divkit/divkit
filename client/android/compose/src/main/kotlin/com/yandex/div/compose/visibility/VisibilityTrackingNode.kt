package com.yandex.div.compose.visibility

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.UnplacedAwareModifierNode
import androidx.compose.ui.platform.InspectorInfo
import com.yandex.div.core.annotations.InternalApi

@Composable
@ReadOnlyComposable
@InternalApi
fun Modifier.onDivVisibilityChanged(
    minFractionVisible: Float,
    onVisibilityChanged: (Boolean) -> Unit,
): Modifier = this then VisibilityTrackingElement(
    host = LocalVisibilityHost.current,
    minFractionVisible = minFractionVisible,
    onVisibilityChanged = onVisibilityChanged,
)

private class VisibilityTrackingElement(
    private val host: VisibilityHost?,
    private val minFractionVisible: Float,
    private val onVisibilityChanged: (Boolean) -> Unit,
) : ModifierNodeElement<VisibilityTrackingNode>() {

    override fun create(): VisibilityTrackingNode {
        return VisibilityTrackingNode(
            host = host,
            minFractionVisible = minFractionVisible,
            onVisibilityChanged = onVisibilityChanged,
        )
    }

    override fun update(node: VisibilityTrackingNode) {
        node.host = host
        node.minFractionVisible = minFractionVisible
        node.onVisibilityChanged = onVisibilityChanged
        node.onPropertiesUpdated()
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "onDivVisibilityChanged"
        properties["minFractionVisible"] = minFractionVisible
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as VisibilityTrackingElement

        if (host != other.host) return false
        if (minFractionVisible != other.minFractionVisible) return false
        if (onVisibilityChanged !== other.onVisibilityChanged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = host.hashCode()
        result = 31 * result + minFractionVisible.hashCode()
        result = 31 * result + onVisibilityChanged.hashCode()
        return result
    }
}

private class VisibilityTrackingNode(
    host: VisibilityHost?,
    var minFractionVisible: Float,
    var onVisibilityChanged: (Boolean) -> Unit,
) : Modifier.Node(),
    GlobalPositionAwareModifierNode,
    UnplacedAwareModifierNode {

    var host: VisibilityHost? = host
        set(value) {
            if (field === value) return
            if (isAttached) field?.removeCallback(invalidateCallback)
            field = value
            if (isAttached) value?.addCallback(invalidateCallback)
        }

    private var isVisible = false
    private var lastCoordinates: LayoutCoordinates? = null

    private val invalidateCallback = object : VisibilityHost.Callback {
        override fun checkVisibility() = this@VisibilityTrackingNode.checkVisibility()
        override fun onContentUnbound() = this@VisibilityTrackingNode.handleContentUnbound()
    }

    override fun onAttach() {
        host?.addCallback(invalidateCallback)
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        lastCoordinates = coordinates
        checkVisibility()
    }

    override fun onUnplaced() {
        lastCoordinates = null
        checkVisibility()
    }

    override fun onDetach() {
        host?.removeCallback(invalidateCallback)
        lastCoordinates = null
        checkVisibility()
        super.onDetach()
    }

    fun onPropertiesUpdated() = checkVisibility()

    private fun handleContentUnbound() {
        if (!isVisible) return
        isVisible = false
        onVisibilityChanged(false)
    }

    private fun checkVisibility() {
        val currentFraction = calculateVisibilityFraction(lastCoordinates)
        val aboveThreshold = currentFraction >= minFractionVisible && currentFraction > 0f
        if (aboveThreshold == isVisible) return
        isVisible = aboveThreshold
        onVisibilityChanged(aboveThreshold)
    }

    private fun calculateVisibilityFraction(coordinates: LayoutCoordinates?): Float {
        if (coordinates == null || !coordinates.isAttached) {
            return 0f
        }

        val size = coordinates.size
        val area = size.width.toFloat() * size.height.toFloat()
        if (area <= 0f) {
            return 0f
        }

        val parentBounds = getParentBounds(coordinates) ?: return 0f

        val visibleBounds = coordinates.boundsInWindow().intersect(parentBounds)
        val visibleArea = if (visibleBounds.isEmpty) {
            0f
        } else {
            visibleBounds.width * visibleBounds.height
        }
        return (visibleArea / area).coerceIn(0f, 1f)
    }

    private fun getParentBounds(coordinates: LayoutCoordinates): Rect? {
        var currentParent = coordinates.parentLayoutCoordinates
        val host = host
        var clipBounds: Rect? = when {
            host != null -> host.getVisibleRectInWindow()
            else -> currentParent?.boundsInWindow()
        } ?: return null
        while (currentParent != null && currentParent.isAttached) {
            clipBounds = clipBounds?.intersect(currentParent.boundsInWindow())
            currentParent = currentParent.parentLayoutCoordinates
        }
        return clipBounds
    }
}
