package com.yandex.div.core.expression.local

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase

internal interface RuntimeStore {

    val rootRuntime: ExpressionsRuntime

    fun showWarningIfNeeded(child: DivBase) = Unit

    /**
     * Returns runtime if it have been stored before, otherwise creates new runtime using
     * @param parentResolver
     */
    fun getOrCreateRuntime(
        path: DivStatePath,
        div: Div,
        parentResolver: ExpressionResolver,
        divView: Div2View,
    ): ExpressionsRuntime

    fun getRuntimeWithOrNull(resolver: ExpressionResolver): ExpressionsRuntime?

    fun resolveRuntimeWith(
        divView: DivViewFacade?,
        path: DivStatePath,
        div: Div,
        resolver: ExpressionResolver,
        parentResolver: ExpressionResolver,
    ): ExpressionsRuntime?

    fun cleanupRuntimes(divView: DivViewFacade)

    fun updateSubscriptions()

    fun clearBindings(divView: DivViewFacade)

    fun onDetachedFromWindow(divView: DivViewFacade)

    fun traverseFrom(runtime: ExpressionsRuntime, path: DivStatePath, callback: (ExpressionsRuntime) -> Unit)

    fun getUniquePathsAndRuntimes(): Map<String, ExpressionsRuntime>

    fun getOrPutItemBuilderResolver(
        path: String,
        parentResolver: ExpressionResolver,
        createResolver: () -> ExpressionResolver
    ): ExpressionResolver

    companion object {
        val EMPTY = object : RuntimeStore {

            override val rootRuntime: ExpressionsRuntime get() = throwException()

            override fun getOrCreateRuntime(
                path: DivStatePath,
                div: Div,
                parentResolver: ExpressionResolver,
                divView: Div2View,
            ) = throw IllegalStateException()

            override fun getRuntimeWithOrNull(resolver: ExpressionResolver) = throwException()

            override fun resolveRuntimeWith(
                divView: DivViewFacade?,
                path: DivStatePath,
                div: Div,
                resolver: ExpressionResolver,
                parentResolver: ExpressionResolver
            ) = throwException()

            override fun cleanupRuntimes(divView: DivViewFacade) = Unit

            override fun updateSubscriptions() = Unit

            override fun clearBindings(divView: DivViewFacade) = Unit

            override fun onDetachedFromWindow(divView: DivViewFacade) = Unit

            override fun traverseFrom(
                runtime: ExpressionsRuntime,
                path: DivStatePath,
                callback: (ExpressionsRuntime) -> Unit
            ) = Unit

            override fun getUniquePathsAndRuntimes(): Map<String, ExpressionsRuntime> = throwException()

            override fun getOrPutItemBuilderResolver(
                path: String,
                parentResolver: ExpressionResolver,
                createResolver: () -> ExpressionResolver
            ) = throwException()

            private fun throwException(): Nothing =
                throw IllegalStateException("Trying to use RuntimeStore before initializing.")
        }
    }
}
