package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.CustomAccessibilityAction
import com.yandex.div.compose.actions.AccessibilityMenuScope
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.actions.LocalAccessibilityMenuScope
import com.yandex.div.compose.actions.observedEnabledActions
import com.yandex.div.compose.di.DivLocalComponent
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.di.handleActions
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAccessibilityMenuItem
import com.yandex.div2.DivBase
import com.yandex.div2.DivInput
import com.yandex.div2.DivSlider
import com.yandex.div2.DivSwitch

@Composable
internal fun DivAccessibility.observeMenu(
    data: DivBase,
): List<CustomAccessibilityAction> {
    val items = menu
    if (items.isNullOrEmpty()) {
        return emptyList()
    }
    val scope = LocalAccessibilityMenuScope.current
    if (scope.hidesOwnMenu || mode.observedValue() == DivAccessibility.Mode.EXCLUDE) {
        return emptyList()
    }
    val component = LocalComponent.current
    val enabled = when (data) {
        is DivSlider -> data.isEnabled.observedValue()
        is DivSwitch -> data.isEnabled.observedValue()
        is DivInput -> data.isEnabled.observedValue()
        else -> true
    }
    if (!enabled) {
        return emptyList()
    }
    val availableItems = items.mapNotNull { item ->
        val enabledActions = item.actions.observedEnabledActions()
        if (enabledActions.isEmpty()) {
            null
        } else {
            item.label.observedValue() to item
        }
    }
    if (availableItems.size > MAX_CUSTOM_ACTIONS) {
        component.reporter.reportWarning("Accessibility menu exceeds $MAX_CUSTOM_ACTIONS actions")
    }
    return availableItems.take(MAX_CUSTOM_ACTIONS).withDistinctLabels().map { (label, item) ->
        CustomAccessibilityAction(label) {
            performMenuItem(item, data, component, scope)
        }
    }
}

private fun DivAccessibility.performMenuItem(
    item: DivAccessibilityMenuItem,
    data: DivBase,
    component: DivLocalComponent,
    scope: AccessibilityMenuScope,
): Boolean {
    val resolver = component.expressionResolver
    if (scope.isDisposed || mode.evaluate(resolver) == DivAccessibility.Mode.EXCLUDE) {
        return false
    }
    if (!data.isMenuEnabled(resolver) || item.actions.none { it.isEnabled.evaluate(resolver) }) {
        return false
    }
    component.handleActions(item.actions, DivActionSource.TAP)
    return true
}

private fun DivBase.isMenuEnabled(resolver: ExpressionResolver): Boolean {
    return when (this) {
        is DivSlider -> isEnabled.evaluate(resolver)
        is DivSwitch -> isEnabled.evaluate(resolver)
        is DivInput -> isEnabled.evaluate(resolver)
        else -> true
    }
}

// Compose rejects 32 custom actions while populating an Android accessibility node.
private const val MAX_CUSTOM_ACTIONS = 31

private fun List<Pair<String, DivAccessibilityMenuItem>>.withDistinctLabels():
    List<Pair<String, DivAccessibilityMenuItem>> {
    val originalLabels = map { it.first }.toSet()
    val usedLabels = mutableSetOf<String>()
    return map { (label, item) ->
        var displayedLabel = label
        var suffix = 2
        while (displayedLabel in usedLabels || (displayedLabel != label && displayedLabel in originalLabels)) {
            displayedLabel = "$label (${suffix++})"
        }
        usedLabels.add(displayedLabel)
        displayedLabel to item
    }
}
