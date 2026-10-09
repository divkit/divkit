package com.yandex.div.compose.extensions

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivExtension

/**
 * Context for applying a `div-extension` to the element.
 *
 * [storedValuesStorage] provides access to stored values bound to the current card.
 *
 * @see DivExtensionHandler
 */
@ConsistentCopyVisibility
@Stable
@SuppressLint("ComposableNaming")
data class DivExtensionEnvironment internal constructor(
    val data: Div,
    val extension: DivExtension,
    val expressionResolver: ExpressionResolver,
    val reporter: DivReporter,
    val animationsEnabled: Boolean,
    val storedValuesStorage: DivStoredValuesStorage,
) {
    @Composable
    fun reportError(message: String) {
        LaunchedEffect(message) {
            reporter.reportError(message)
        }
    }
}
