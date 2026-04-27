package com.yandex.div.compose.extensions

import androidx.compose.ui.Modifier
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivExtension

/**
 * Context for applying a `div-extension` to the element.
 *
 * @see DivExtensionHandler
 */
@ExperimentalApi
class DivExtensionEnvironment internal constructor(
    val extension: DivExtension,
    val expressionResolver: ExpressionResolver,
    val modifier: Modifier
)
