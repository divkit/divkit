package com.yandex.div.compose.views.transition

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.IntSize
import com.yandex.div.compose.context.DivViewContext
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivVisibility

internal class StateChangeTransitionScope(
    val enterScope: AnimatedVisibilityScope,
    val exitScope: AnimatedVisibilityScope,
    val enteringIds: Set<String>,
    val exitingIds: Set<String>,
    val commonIds: Set<String> = emptySet(),
    val transitionState: StateChangeTransitionState = StateChangeTransitionState(),
    val scene: StateChangeTransitionScene = StateChangeTransitionScene(),
)

internal class StateChangeTransitionState {
    private val currentValues = mutableMapOf<String, StateChangeVisualState>()
    private val exitValues = mutableMapOf<String, StateChangeVisualState>()
    private val currentLayouts = mutableMapOf<String, StateChangeLayoutState>()
    private val exitLayouts = mutableMapOf<String, StateChangeLayoutState>()
    private val renderedIdCounts = mutableMapOf<String, Int>()
    private val overlayLayers = mutableStateListOf<StateChangeOverlay>()
    private val overlayIds = mutableListOf<String>()
    private val overlayIndices = mutableMapOf<String, Int>()
    val snapshots = StateChangeSnapshotCache()

    fun updateCurrent(id: String, values: StateChangeVisualState) {
        currentValues[id] = values
    }

    fun captureExitValues(ids: Set<String>) {
        ids.forEach { id ->
            currentValues[id]?.let { exitValues[id] = it }
            (currentLayouts[id] ?: snapshots.layout(id))?.let { exitLayouts[id] = it }
        }
    }

    fun getExitValues(id: String): StateChangeVisualState? = exitValues[id]

    fun updateCurrentLayout(id: String, layout: StateChangeLayoutState) {
        currentLayouts[id] = layout
    }

    fun getExitLayout(id: String): StateChangeLayoutState? = exitLayouts[id]

    fun registerRenderedId(id: String) {
        renderedIdCounts[id] = (renderedIdCounts[id] ?: 0) + 1
    }

    fun unregisterRenderedId(id: String) {
        val remainingCount = (renderedIdCounts[id] ?: 0) - 1
        if (remainingCount > 0) {
            renderedIdCounts[id] = remainingCount
        } else {
            renderedIdCounts -= id
        }
    }

    fun snapshotRenderedIds(): Set<String> = renderedIdCounts.keys.toSet()

    val renderedIds: Set<String> get() = renderedIdCounts.keys

    fun registerOverlay(id: String, layer: GraphicsLayer, visualState: StateChangeVisualState) {
        val overlay = StateChangeOverlay(layer, visualState)
        val index = overlayIndices[id]
        if (index == null) {
            overlayIndices[id] = overlayLayers.size
            overlayIds += id
            overlayLayers += overlay
        } else {
            overlayLayers[index] = overlay
        }
    }

    fun unregisterOverlays(ids: Set<String>) {
        if (ids.isEmpty()) return

        for (index in overlayIds.lastIndex downTo 0) {
            if (overlayIds[index] in ids) {
                overlayIds.removeAt(index)
                overlayLayers.removeAt(index)
            }
        }
        overlayIndices.clear()
        overlayIds.forEachIndexed { index, id -> overlayIndices[id] = index }
        snapshots.remove(ids)
    }

    fun overlayLayers(): List<StateChangeOverlay> = overlayLayers
}

internal data class StateChangeOverlay(
    val layer: GraphicsLayer,
    val visualState: StateChangeVisualState,
)

internal class StateChangeTransitionScene {
    var coordinates: LayoutCoordinates? = null
        private set
    var size by mutableStateOf(IntSize.Zero)
        private set
    var positionInRoot = Offset.Zero
        private set

    fun update(coordinates: LayoutCoordinates) {
        this.coordinates = coordinates
        size = coordinates.size
        positionInRoot = coordinates.positionInRoot()
    }
}

@Immutable
internal data class StateChangeTransitionContext(
    val scope: StateChangeTransitionScope? = null,
    val contentActive: Boolean = true,
    val contentVisible: Boolean = true,
    val overlayCaptured: Boolean = false,
    val snapshotParentId: String? = null,
)

internal val LocalStateChangeTransitionContext =
    compositionLocalOf { StateChangeTransitionContext() }

internal fun Div?.containsStateChangeTransitions(): Boolean {
    val div = this ?: return false
    with(div.value()) {
        if (transitionIn != null || transitionOut != null) return true
    }
    return when (div) {
        is Div.Container -> div.value.items.orEmpty().any { it.containsStateChangeTransitions() }
        is Div.Grid -> div.value.items.orEmpty().any { it.containsStateChangeTransitions() }
        is Div.Custom,
        is Div.Gallery,
        is Div.GifImage,
        is Div.Image,
        is Div.Indicator,
        is Div.Input,
        is Div.Pager,
        is Div.Select,
        is Div.Separator,
        is Div.Slider,
        is Div.State,
        is Div.Switch,
        is Div.Tabs,
        is Div.Text,
        is Div.Video -> false
    }
}

@Immutable
internal data class StateChangeVisualState(
    val alpha: Float,
    val scale: Float = 1f,
    val pivotX: Float = 0.5f,
    val pivotY: Float = 0.5f,
    val translationX: Float = 0f,
    val translationY: Float = 0f,
)

internal data class SlideGeometry(
    val sceneSize: IntSize,
    val bounds: ElementBounds,
)

internal data class StateChangeLayoutState(
    val sceneSize: IntSize,
    val bounds: ElementBounds,
    val positionInRoot: Offset,
) {
    val slideGeometry = SlideGeometry(sceneSize, bounds)
}

internal data class ElementBounds(
    val left: Float,
    val top: Float,
    val width: Float,
    val height: Float,
) {
    constructor(position: Offset, size: IntSize) : this(
        left = position.x,
        top = position.y,
        width = size.width.toFloat(),
        height = size.height.toFloat(),
    )

    val right: Float get() = left + width
    val bottom: Float get() = top + height

    companion object {
        val Zero = ElementBounds(0f, 0f, 0f, 0f)
    }
}

internal fun Div?.visibleStateTransitionIds(
    divViewContext: DivViewContext,
    parentComponent: DivLocalComponent,
): Set<String> = buildSet {
    this@visibleStateTransitionIds?.collectVisibleStateTransitionIds(
        destination = this,
        divViewContext = divViewContext,
        parentComponent = { parentComponent },
    )
}

private fun Div.collectVisibleStateTransitionIds(
    destination: MutableSet<String>,
    divViewContext: DivViewContext,
    parentComponent: () -> DivLocalComponent,
) {
    val data = value()
    val localComponent = data.stateTransitionComponentProvider(divViewContext, parentComponent)
    val visibility = when (val expression = data.visibility) {
        is Expression.ConstantExpression -> expression.value
        else -> expression.evaluate(localComponent().expressionResolver)
    }
    if (visibility != DivVisibility.VISIBLE) return

    data.id?.let(destination::add)
    if (this is Div.State) return

    when (this) {
        is Div.Container -> value.items.orEmpty().forEach {
            it.collectVisibleStateTransitionIds(destination, divViewContext, localComponent)
        }
        is Div.Gallery -> value.items.orEmpty().forEach {
            it.collectVisibleStateTransitionIds(destination, divViewContext, localComponent)
        }
        is Div.Grid -> value.items.orEmpty().forEach {
            it.collectVisibleStateTransitionIds(destination, divViewContext, localComponent)
        }
        is Div.Pager -> value.items.orEmpty().forEach {
            it.collectVisibleStateTransitionIds(destination, divViewContext, localComponent)
        }
        is Div.Tabs -> value.items.forEach {
            it.div.collectVisibleStateTransitionIds(destination, divViewContext, localComponent)
        }
        is Div.State,
        is Div.Custom,
        is Div.GifImage,
        is Div.Image,
        is Div.Indicator,
        is Div.Input,
        is Div.Select,
        is Div.Separator,
        is Div.Slider,
        is Div.Switch,
        is Div.Text,
        is Div.Video -> Unit
    }
}

private fun DivBase.stateTransitionComponentProvider(
    divViewContext: DivViewContext,
    parentComponent: () -> DivLocalComponent,
): () -> DivLocalComponent {
    if (functions.isNullOrEmpty() && variables.isNullOrEmpty() && variableTriggers.isNullOrEmpty()) {
        return parentComponent
    }

    var component: DivLocalComponent? = null
    return {
        component ?: divViewContext.getLocalComponent(this, parentComponent()).also { component = it }
    }
}

internal fun Modifier.disableRetainedStateInteractions(): Modifier {
    return clearAndSetSemantics { }
        .focusProperties { canFocus = false }
}
