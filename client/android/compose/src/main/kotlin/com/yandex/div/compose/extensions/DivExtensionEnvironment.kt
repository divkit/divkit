package com.yandex.div.compose.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.yandex.div.compose.DivReporter
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivExtension

/**
 * Context for applying a `div-extension` to the element.
 *
 * @see DivExtensionHandler
 */
@ExperimentalApi
@SuppressLint("ComposableNaming")
class DivExtensionEnvironment internal constructor(
    val data: Div,
    val extension: DivExtension,
    val expressionResolver: ExpressionResolver,
    val reporter: DivReporter
) {
    @Composable
    fun reportError(message: String) {
        LaunchedEffect(message) {
            reporter.reportError(message)
        }
    }
}
