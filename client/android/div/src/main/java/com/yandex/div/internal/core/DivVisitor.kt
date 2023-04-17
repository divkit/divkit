package com.yandex.div.internal.core

import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPager
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText

abstract class DivVisitor<T> {

    protected fun visit(div: Div, resolver: ExpressionResolver): T {
        return when (div) {
            is Div.Text -> visit(div.value, resolver)
            is Div.Image -> visit(div.value, resolver)
            is Div.GifImage -> visit(div.value, resolver)
            is Div.Separator -> visit(div.value, resolver)
            is Div.Container -> visit(div.value, resolver)
            is Div.Grid -> visit(div.value, resolver)
            is Div.Gallery -> visit(div.value, resolver)
            is Div.Pager -> visit(div.value, resolver)
            is Div.Tabs -> visit(div.value, resolver)
            is Div.State -> visit(div.value, resolver)
            is Div.Custom -> visit(div.value, resolver)
            is Div.Indicator -> visit(div.value, resolver)
            is Div.Slider -> visit(div.value, resolver)
            is Div.Input -> visit(div.value, resolver)
            is Div.Select -> TODO()
            is Div.Video -> TODO()
        }
    }

    protected fun visit(div: DivBase, resolver: ExpressionResolver): T? {
        return when (div) {
            is DivText -> visit(div, resolver)
            is DivImage -> visit(div, resolver)
            is DivGifImage -> visit(div, resolver)
            is DivSeparator -> visit(div, resolver)
            is DivContainer -> visit(div, resolver)
            is DivGrid -> visit(div, resolver)
            is DivGallery -> visit(div, resolver)
            is DivPager -> visit(div, resolver)
            is DivTabs -> visit(div, resolver)
            is DivState -> visit(div, resolver)
            is DivCustom -> visit(div, resolver)
            is DivIndicator -> visit(div, resolver)
            is DivSlider -> visit(div, resolver)
            is DivInput -> visit(div, resolver)
            else -> {
                Assert.fail("Unsupported div type: ${div.javaClass.simpleName}")
                null
            }
        }
    }

    protected abstract fun visit(data: DivText, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivImage, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivGifImage, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivSeparator, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivContainer, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivGrid, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivGallery, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivPager, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivTabs, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivState, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivCustom, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivIndicator, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivSlider, resolver: ExpressionResolver): T

    protected abstract fun visit(data: DivInput, resolver: ExpressionResolver): T
}
