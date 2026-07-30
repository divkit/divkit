package com.yandex.div.core.view2.divs

import android.net.Uri
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

internal fun assertActionApplied(target: View, actionUri: Uri, resolver: ExpressionResolver, divView: Div2View) {
    val actionCaptor = argumentCaptor<List<DivAction>>()
    verify(divView.div2Component.actionBinder).bindDivActions(
        target = eq(target),
        actions = actionCaptor.capture(),
        longTapActions = anyOrNull(),
        doubleTapActions = anyOrNull(),
        hoverStartActions = anyOrNull(),
        hoverEndActions = anyOrNull(),
        pressStartActions = anyOrNull(),
        pressEndActions = anyOrNull(),
        actionAnimation = any(),
        captureFocusOnAction = any(),
        resolver = eq(resolver),
        divView = eq(divView),
    )

    val action = actionCaptor.firstValue.find {
            action -> action.url?.evaluate(resolver) == actionUri
    }
    assertNotNull(action)
    assertTrue(target.hasOnClickListeners())
}
