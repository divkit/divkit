package com.yandex.div.core.view2.divs

import android.content.Context
import android.view.ContextThemeWrapper
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivConfiguration
import com.yandex.div.core.DivViewConfig
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivValidator
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.internal.viewpool.PseudoViewPool
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.json.expressions.ExpressionResolver
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy

internal fun context() = getApplicationContext<Context>()

internal fun divView(
    logId: String = "",
    divTag: String = ""
): Div2View {
    val context = context()
    val div2Context = Div2Context(
        ContextThemeWrapper(context, context.theme),
        DivConfiguration.Builder(mock()).build(),
        lifecycleOwner = null
    )

    val actionBinder = mock<DivActionBinder> {
        on { bindDivActions(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull(),
            anyOrNull(), any(), any()) }.thenCallRealMethod()
    }

    val component = mock<Div2Component>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
        on { this.actionBinder } doReturn actionBinder
        on { this.div2Logger } doReturn mock()
    }

    return mock {
        on { this.context } doReturn div2Context
        on { this.resources } doReturn context.resources
        on { this.div2Component } doReturn component
        on { this.logId } doReturn logId
        on { this.divTag } doReturn DivDataTag(divTag)
        on { this.config } doReturn DivViewConfig.DEFAULT
        on { this.expressionResolver } doReturn ExpressionResolver.EMPTY
        on { handleUri(any()) }.thenCallRealMethod()
    }
}

internal fun viewCreator(): DivViewCreator {
    val validator = mock<DivValidator> {
        on { validate(any(), any()) } doReturn true
    }

    return spy(DivViewCreator(context(), PseudoViewPool(), validator, ViewPreCreationProfile(), mock()))
}

internal fun rootPath() = DivStatePath.parse("0")

internal fun defaultLayoutParams(): ViewGroup.LayoutParams {
    return FrameLayout.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT
    )
}
