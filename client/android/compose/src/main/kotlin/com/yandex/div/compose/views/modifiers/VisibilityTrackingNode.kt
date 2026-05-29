package com.yandex.div.compose.views.modifiers

import android.view.View
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.UnplacedAwareModifierNode
import androidx.compose.ui.platform.InspectorInfo
import com.yandex.div.compose.host.CheckVisibilityCallback
import com.yandex.div.compose.DivViewHost

internal fun Modifier.onDivVisibilityChanged(
    minFractionVisible: Float = 1f,
    divViewHost: DivViewHost?,
    onVisibilityChanged: (Boolean) -> Unit,
): Modifier = this then VisibilityTrackingElement(
    minFractionVisible = minFractionVisible,
    divViewHost = divViewHost,
    onVisibilityChanged = onVisibilityChanged,
)

private class VisibilityTrackingElement(
    private val minFractionVisible: Float,
    private val divViewHost: DivViewHost?,
    private val onVisibilityChanged: (Boolean) -> Unit,
) : ModifierNodeElement<VisibilityTrackingNode>() {

    override fun create(): VisibilityTrackingNode = VisibilityTrackingNode(
        minFractionVisible = minFractionVisible,
        divViewHost = divViewHost,
        onVisibilityChanged = onVisibilityChanged,
    )

    override fun update(node: VisibilityTrackingNode) {
        node.minFractionVisible = minFractionVisible
        node.divViewHost = divViewHost
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

        if (minFractionVisible != other.minFractionVisible) return false
        if (divViewHost != other.divViewHost) return false
        if (onVisibilityChanged !== other.onVisibilityChanged) return false

        return true
    }

    override fun hashCode(): Int {
        var result = minFractionVisible.hashCode()
        result = 31 * result + (divViewHost?.hashCode() ?: 0)
        result = 31 * result + onVisibilityChanged.hashCode()
        return result
    }
}

private class VisibilityTrackingNode(
    var minFractionVisible: Float,
    var divViewHost: DivViewHost?,
    var onVisibilityChanged: (Boolean) -> Unit,
) : Modifier.Node(),
    GlobalPositionAwareModifierNode,
    UnplacedAwareModifierNode {

    private var isVisible = false
    private var lastCoordinates: LayoutCoordinates? = null

    private val invalidateCallback = CheckVisibilityCallback(::checkVisibility)

    override fun onAttach() {
        divViewHost?.addCallback(invalidateCallback)
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
        divViewHost?.removeCallback(invalidateCallback)
        lastCoordinates = null
        checkVisibility()
        super.onDetach()
    }

    fun onPropertiesUpdated() = checkVisibility()

    private fun checkVisibility() {
        val currentFraction = calculateVisibilityFraction(lastCoordinates)
        val aboveThreshold = currentFraction >= minFractionVisible && currentFraction > 0f
        if (aboveThreshold == isVisible) return
        isVisible = aboveThreshold
        onVisibilityChanged(aboveThreshold)
    }

    private fun calculateVisibilityFraction(coordinates: LayoutCoordinates?): Float {
        if (coordinates == null || !coordinates.isAttached) return 0f

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
        val divViewHost = divViewHost
        var clipBounds: Rect? = when {
            divViewHost != null -> divViewHost.composeView.getHostVisibleRectInWindow()
            else -> currentParent?.boundsInWindow()
        } ?: return null
        while (currentParent != null && currentParent.isAttached) {
            clipBounds = clipBounds?.intersect(currentParent.boundsInWindow())
            currentParent = currentParent.parentLayoutCoordinates
        }
        return clipBounds
    }

    private fun View.getHostVisibleRectInWindow(): Rect? {
        val globalRect = android.graphics.Rect()
        if (!getGlobalVisibleRect(globalRect)) return null

        return Rect(
            left = globalRect.left.toFloat(),
            top = globalRect.top.toFloat(),
            right = globalRect.right.toFloat(),
            bottom = globalRect.bottom.toFloat(),
        )
    }
}
