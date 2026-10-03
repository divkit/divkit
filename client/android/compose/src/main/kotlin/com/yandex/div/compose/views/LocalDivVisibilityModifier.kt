package com.yandex.div.compose.views

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
        sideEffect {
            if (!isAttached) return@sideEffect
            traverseDescendants(FocusVisibilityElement) {
                (it as FocusVisibilityNode).invalidateFocusProperties()
                TraverseDescendantsAction.ContinueTraversal
            }
        }
    }
}
