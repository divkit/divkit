package com.yandex.div.compose.actions

import com.yandex.div.compose.dagger.DivLocalScope
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.json.expressions.ExpressionResolver
import javax.inject.Inject

/**
 * Context passed to action handlers. Provides access to DivKit components
 * scoped to the element that triggered the action.
 */
@DivLocalScope
@ExperimentalApi
class DivActionHandlingContext @Inject internal constructor(
    val expressionResolver: ExpressionResolver
)
