package com.yandex.div.core.view2.divs

import android.net.Uri
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivAction
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify

internal fun assertActionApplied(divView: Div2View, target: View, actionUri: Uri) {
    val actionCaptor = argumentCaptor<List<DivAction>>()
    verify(divView.div2Component.actionBinder).bindDivActions(
        divView = eq(divView),
        target = eq(target),
        actions = actionCaptor.capture(),
        longTapActions = anyOrNull(),
        doubleTapActions = anyOrNull(),
        actionAnimation = any(),
    )

    val action = actionCaptor.firstValue.filterIsInstance<DivAction.Default>().find {
            action -> action.value.url?.evaluate(divView.expressionResolver) == actionUri
    }
    assertNotNull(action)
    assertTrue(target.hasOnClickListeners())
}
