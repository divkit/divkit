package com.yandex.div.compose.dagger

import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.yatagan.Binds
import com.yandex.yatagan.Module

@Module
internal interface DivLocalModule {

    @Binds
    fun bindExpressionResolver(impl: DivComposeExpressionResolver): ExpressionResolver
}
