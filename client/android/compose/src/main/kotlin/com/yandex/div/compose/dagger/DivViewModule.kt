package com.yandex.div.compose.dagger

import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module
import com.yandex.yatagan.Provides
import javax.inject.Named

@Module
internal interface DivViewModule {

    @Binds
    fun bindExpressionResolver(impl: DivComposeExpressionResolver): ExpressionResolver

    companion object {

        @DivViewScope
        @Named(Names.CARD_VARIABLES)
        @Provides
        fun provideVariableController(
            @Named(Names.HOST_VARIABLES) variableController: DivVariableController
        ): DivVariableController {
            return DivVariableController(variableController)
        }
    }
}
