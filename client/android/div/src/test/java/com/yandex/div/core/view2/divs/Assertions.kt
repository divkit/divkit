package com.yandex.div.core.view2.divs

import android.net.Uri
import android.view.View
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivAction
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

internal fun assertActionApplied(context: BindingContext, target: View, actionUri: Uri) {
    val actionCaptor = argumentCaptor<List<DivAction>>()
    verify(context.divView.div2Component.actionBinder).bindDivActions(
        context = eq(context),
        target = eq(target),
        actions = actionCaptor.capture(),
        longTapActions = anyOrNull(),
        doubleTapActions = anyOrNull(),
        hoverStartActions = anyOrNull(),
        hoverEndActions = anyOrNull(),
        pressStartActions = anyOrNull(),
        pressEndActions = anyOrNull(),
        actionAnimation = any(),
        accessibility = anyOrNull(),
    )

    val action = actionCaptor.firstValue.find {
            action -> action.url?.evaluate(context.expressionResolver) == actionUri
    }
    assertNotNull(action)
    assertTrue(target.hasOnClickListeners())
}
