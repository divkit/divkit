package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = LocalComponent.current.expressionResolver
