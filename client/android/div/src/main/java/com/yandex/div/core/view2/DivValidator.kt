package com.yandex.div.core.view2

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.internal.core.DivVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject

@Mockable
@DivScope
internal class DivValidator @Inject constructor() : DivVisitor<Boolean>() {

    fun validate(div: Div, resolver: ExpressionResolver) = visit(div, resolver)

    override fun defaultVisit(data: Div, resolver: ExpressionResolver) = true
}
