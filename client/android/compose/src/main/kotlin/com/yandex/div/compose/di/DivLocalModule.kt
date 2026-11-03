package com.yandex.div.compose.di

import com.yandex.div.compose.expressions.DivComposeExpressionResolver
import com.yandex.div.json.expressions.ExpressionResolver
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.Binds

@BindingContainer
internal interface DivLocalModule {

    @Binds
    fun bindExpressionResolver(impl: DivComposeExpressionResolver): ExpressionResolver
}
