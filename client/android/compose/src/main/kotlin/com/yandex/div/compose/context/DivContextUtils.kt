package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalContext
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    @ReadOnlyComposable
    get() = LocalContext.current as DivContext

internal val expressionResolver: ExpressionResolver
    @Composable
    @ReadOnlyComposable
    get() = LocalComponent.current.expressionResolver

internal val animationsEnabled: Boolean
    @Composable
    @ReadOnlyComposable
    get() = divContext.component.animationConfiguration.isEnabled
