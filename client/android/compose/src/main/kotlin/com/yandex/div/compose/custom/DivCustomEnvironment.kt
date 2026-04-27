package com.yandex.div.compose.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivCustom

/**
 * Context for composing a `div-custom` element.
 *
 * Passed to [DivCustomViewFactory.Content] and provides access to the current
 * [DivCustom] data, the [ExpressionResolver] that should be used to resolve
 * expressions inside the custom element and helpers for rendering `items`
 * declared on the `div-custom`.
 */
@ExperimentalApi
class DivCustomEnvironment internal constructor(
    val data: DivCustom,
    val expressionResolver: ExpressionResolver,
    val modifier: Modifier,
    val items: @Composable () -> Unit,
    val item: @Composable (index: Int, modifier: Modifier) -> Unit,
)
