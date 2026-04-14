package com.yandex.div.compose.internal

import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.context.DivViewContextStorage
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import javax.inject.Inject

/**
 * Provides access to the features not intended to be used in production environment.
 *
 * @see com.yandex.div.compose.DivContext.debugFeatures
 */
@DivContextScope
@InternalApi
class DivDebugFeatures @Inject internal constructor(
    private val actionHandler: DivActionHandler,
    private val viewContextStorage: DivViewContextStorage
) {

    /**
     * Returns [ExpressionResolver] associated with the given [DivData].
     */
    fun getExpressionResolver(data: DivData): ExpressionResolver? {
        return viewContextStorage.get(data)?.rootLocalComponent?.expressionResolver
    }

    /**
     * Performs action in the context of [com.yandex.div.compose.DivView] associated with
     * the given [DivData].
     */
    fun performAction(data: DivData, action: DivAction) {
        viewContextStorage.get(data)?.let {
            actionHandler.handle(
                context = it.rootLocalComponent.actionHandlingContext,
                action = action,
                source = DivActionSource.EXTERNAL
            )
        }
    }
}
