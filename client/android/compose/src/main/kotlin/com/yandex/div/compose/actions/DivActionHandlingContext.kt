package com.yandex.div.compose.actions

import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.compose.dagger.Names
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivSightAction
import javax.inject.Inject
import javax.inject.Named

/**
 * Context passed to action handlers. Provides access to DivKit components
 * scoped to the element that triggered the action.
 */
@DivLocalScope
class DivActionHandlingContext @Inject internal constructor(
    @param:Named(Names.CARD_ID) val cardId: String,
    val expressionResolver: ExpressionResolver
) {
    /**
     * How many times each limited sight action of this scope has been logged.
     */
    internal val sightActionCounts = mutableMapOf<DivSightAction, Int>()
}
