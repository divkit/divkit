package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.actions.DivActionHandler
import com.yandex.div.compose.views.LocalDivContext
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val actionHandler: DivActionHandler
    @Composable
    get() = divContext.component.actionHandler

internal val imageLoader: ImageLoader
    @Composable
    get() = divContext.component.imageLoader

internal val reporter: DivReporter
    @Composable
    get() = divContext.component.reporter

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = LocalDivContext.current.expressionResolver
