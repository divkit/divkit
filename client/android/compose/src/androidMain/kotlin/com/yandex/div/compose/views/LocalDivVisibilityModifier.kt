package com.yandex.div.compose.views

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusProperties
import androidx.compose.ui.focus.FocusPropertiesModifierNode
import androidx.compose.ui.focus.invalidateFocusProperties
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.modifier.modifierLocalProvider
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.TraversableNode.Companion.TraverseDescendantsAction
import androidx.compose.ui.node.findNearestAncestor
import androidx.compose.ui.node.traverseDescendants
import androidx.compose.ui.platform.InspectorInfo
import com.yandex.div2.DivVisibility

internal val LocalDivVisibilityModifier = modifierLocalOf { true }

private val invisibleDivVisibilityModifier =
    Modifier.modifierLocalProvider(LocalDivVisibilityModifier) { false }.then(HiddenVisibilityElement)

internal fun Modifier.divVisibility(visibility: DivVisibility): Modifier {
    return if (visibility == DivVisibility.VISIBLE) this else then(invisibleDivVisibilityModifier)
}

internal fun Modifier.inheritFocusVisibility(): Modifier = then(FocusVisibilityElement)

internal class DivVisibilityState {
    private var observer: Modifier.Node? = null
    var invalidation by mutableIntStateOf(0)
        private set

    fun currentVisibility(): Boolean? = observer?.takeIf { it.isAttached }?.let {
        it.findNearestAncestor(HiddenVisibilityElement) == null
    }

    fun attach(observer: Modifier.Node) {
        this.observer = observer
        invalidate()
    }

    fun detach() {
        observer = null
        invalidate()
    }

    fun invalidate() {
        invalidation++
    }
}

internal fun Modifier.observeDivVisibility(state: DivVisibilityState): Modifier =
    then(DivVisibilityObserverElement(state))

private data class DivVisibilityObserverElement(
    val state: DivVisibilityState,
) : ModifierNodeElement<DivVisibilityObserverNode>() {
    override fun create() = DivVisibilityObserverNode(state)

    override fun update(node: DivVisibilityObserverNode) {
        node.update(state)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "observeDivVisibility"
    }
}

private class DivVisibilityObserverNode(
    private var state: DivVisibilityState,
) : Modifier.Node(), TraversableNode {
    override val traverseKey = DivVisibilityState::class

    override fun onAttach() = state.attach(this)

    override fun onDetach() = state.detach()

    fun update(state: DivVisibilityState) {
        if (this.state === state) return
        if (isAttached) this.state.detach()
        this.state = state
        if (isAttached) state.attach(this)
    }

    fun invalidate() = state.invalidate()
}

private data object FocusVisibilityElement : ModifierNodeElement<FocusVisibilityNode>() {
    override fun create() = FocusVisibilityNode()

    override fun update(node: FocusVisibilityNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "inheritFocusVisibility"
    }
}

private class FocusVisibilityNode : Modifier.Node(),
    FocusPropertiesModifierNode,
    TraversableNode {

    override val traverseKey = FocusVisibilityElement

    override fun applyFocusProperties(focusProperties: FocusProperties) {
        if (findNearestAncestor(HiddenVisibilityElement) != null) focusProperties.canFocus = false
    }
}

private data object HiddenVisibilityElement : ModifierNodeElement<HiddenVisibilityNode>() {
    override fun create() = HiddenVisibilityNode()

    override fun update(node: HiddenVisibilityNode) = Unit

    override fun InspectorInfo.inspectableProperties() {
        name = "hiddenVisibility"
    }
}

private class HiddenVisibilityNode : Modifier.Node(), TraversableNode {
    override val traverseKey = HiddenVisibilityElement

    override fun onAttach() {
        invalidateVisibilityObservers()
        sideEffect {
            if (!isAttached) return@sideEffect
            traverseDescendants(FocusVisibilityElement) {
                (it as FocusVisibilityNode).invalidateFocusProperties()
                TraverseDescendantsAction.ContinueTraversal
            }
        }
    }

    override fun onDetach() {
        invalidateVisibilityObservers()
    }

    private fun invalidateVisibilityObservers() {
        traverseDescendants(DivVisibilityState::class) {
            (it as DivVisibilityObserverNode).invalidate()
            TraverseDescendantsAction.ContinueTraversal
        }
    }
}
