package com.yandex.div.core.view2.divs.widgets

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBorder

internal interface DivBorderSupports {

    val border: DivBorder?

    fun getDivBorderDrawer(): DivBorderDrawer?

    fun setBorder(border: DivBorder?, resolver: ExpressionResolver)
}
