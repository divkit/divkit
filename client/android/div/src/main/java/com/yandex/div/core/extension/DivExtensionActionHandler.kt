package com.yandex.div.core.extension

import android.view.View
import com.yandex.div.core.expression.evaluation.DictEvaluator
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver

/**
 * Handles extension actions addressed to a Div instance by its runtime [DivStatePath].
 *
 * The path identifies a concrete instance, including its state and repeated-collection context.
 * An action can be handled before the corresponding [View] is created or bound. Action state is
 * therefore owned by the path, while subscriptions created by [onViewBind] belong to a particular
 * View and must be released by [onViewUnbind].
 */
interface DivExtensionActionHandler {
    /**
     * Subscribes [view] to action state associated with [path] and updates its presentation.
     */
    fun onViewBind(divView: Div2View, path: DivStatePath, view: View) = Unit

    /**
     * Releases subscriptions and other View-owned resources created for [view] at [path].
     */
    fun onViewUnbind(divView: Div2View, path: DivStatePath, view: View) = Unit

    /**
     * Handles an action for [path], independently of whether its View is currently bound.
     *
     * [payload] contains the action data, and [resolver] provides the expression-evaluation
     * context. Implementations may update action or card state here and return `true` when the
     * action was handled.
     */
    fun handleAction(
        divView: Div2View,
        path: DivStatePath,
        payload: DictEvaluator?,
        resolver: ExpressionResolver,
    ): Boolean
}
