package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import coil3.ImageLoader
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivFontFamilyProvider
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.json.expressions.ExpressionResolver

internal val divContext: DivContext
    @Composable
    get() = LocalContext.current as DivContext

internal val fontFamilyProvider: DivFontFamilyProvider
    @Composable
    get() = divContext.component.fontFamilyProvider

internal val imageLoader: ImageLoader
    @Composable
    get() = divContext.component.imageLoader

internal val expressionResolver: ExpressionResolver
    @Composable
    get() = LocalComponent.current.expressionResolver
