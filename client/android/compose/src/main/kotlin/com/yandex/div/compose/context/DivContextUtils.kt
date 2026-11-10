package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    @ReadOnlyComposable
    get() = LocalDivContext.current

internal val expressionResolver: ExpressionResolver
    @Composable
    @ReadOnlyComposable
    get() = LocalComponent.current.expressionResolver

internal val animationsEnabled: Boolean
    @Composable
    @ReadOnlyComposable
    get() = divContext.component.animationConfiguration.isEnabled
