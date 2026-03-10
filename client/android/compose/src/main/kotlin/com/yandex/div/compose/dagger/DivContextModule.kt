package com.yandex.div.compose.dagger

import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.compose.expressions.EvaluatorWarningSender
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.evaluable.function.GeneratedBuiltinFunctionProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides

@Module
internal interface DivContextModule {

    @Binds
    fun bindExpressionResolver(impl: DivComposeExpressionResolver): ExpressionResolver

    companion object {

        @DivContextScope
        @Provides
        fun providerEvaluator(
            warningSender: EvaluatorWarningSender
        ): Evaluator {
            val evaluationContext = EvaluationContext(
                variableProvider = { _ -> null },
                storedValueProvider = { _ -> null },
                functionProvider = GeneratedBuiltinFunctionProvider,
                warningSender = warningSender
            )
            return Evaluator(evaluationContext)
        }
    }
}
