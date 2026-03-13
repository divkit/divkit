package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val reporter: DivReporter
    @Composable
    get() = divContext.component.reporter

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = LocalDivViewContext.current.expressionResolver
