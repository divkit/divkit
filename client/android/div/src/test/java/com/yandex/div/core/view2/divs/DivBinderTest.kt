package com.yandex.div.core.view2.divs

import androidx.transition.TransitionSet
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivCustomContainerViewAdapter
import com.yandex.div.core.DivCustomViewAdapter
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.extension.DivExtensionController
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.DivValidator
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.DivViewIdProvider
import com.yandex.div.core.view2.animations.DivTransitionHandler
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.internal.viewpool.PseudoViewPool
import com.yandex.div.internal.viewpool.ViewPreCreationProfile
import com.yandex.div.json.expressions.ExpressionResolver
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import org.robolectric.RuntimeEnvironment

open class DivBinderTest {

    internal val actionBinder = mock<DivActionBinder> {
        on { bindDivActions(any(), any(), anyOrNull(), anyOrNull(), anyOrNull(), any(), anyOrNull()) }.thenCallRealMethod()
    }
    private val mockComponent = mock<Div2Component>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
        on { actionBinder } doReturn actionBinder
    }

    private val validator = mock<DivValidator> {
        on { validate(any(), any()) } doReturn true
    }
    internal val imageLoader = mock<DivImageLoader>()

    internal val context = RuntimeEnvironment.application
    private val expressionResolver = mock<ExpressionResolver>()
    private val oldExpressionResolver = mock<ExpressionResolver>()
    internal val divView = mock<Div2View> {
        on { div2Component } doReturn mockComponent
        on { divTag } doReturn DivDataTag("id")
        on { handleUri(any()) }.thenCallRealMethod()
        on { logId } doReturn "id"
        on { config } doReturn mock()
        on { expressionResolver } doReturn expressionResolver
        on { oldExpressionResolver } doReturn oldExpressionResolver
        on { divTransitionHandler } doReturn DivTransitionHandler(mock)
    }
    private val divExtensionController = DivExtensionController(emptyList())

    internal val visitor: ReleaseViewVisitor = spy(
        ReleaseViewVisitor(divView,
            DivCustomViewAdapter.STUB,
            DivCustomContainerViewAdapter.STUB,
            divExtensionController,
        )
    ).apply {
        whenever(divView.releaseViewVisitor) doReturn this
    }

    private val transitionBuilder = mock<DivTransitionBuilder> {
        on { buildTransitions(fromDiv = anyOrNull(), toDiv = anyOrNull(), any(), any()) } doReturn TransitionSet()
    }

    private val viewIdProvider = DivViewIdProvider()

    private val viewComponent = mock<Div2ViewComponent>(defaultAnswer = Mockito.RETURNS_DEEP_STUBS) {
        on { viewIdProvider } doReturn viewIdProvider
        on { releaseViewVisitor } doReturn visitor
        on { transitionBuilder } doReturn transitionBuilder
    }.apply {
        whenever(divView.viewComponent) doReturn this
    }

    internal val viewCreator = spy(DivViewCreator(context(), PseudoViewPool(), validator, ViewPreCreationProfile(), mock()))
    internal val baseBinder = DivBaseBinder(mock(), mock(), mock(), mock())

    companion object {
        internal const val CARD_ID = "id"
    }
}
