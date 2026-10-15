package com.yandex.div.core.state

import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.view2.Div2View

internal object DivStatePathResolver {

    @JvmStatic
    @Throws(PathFormatException::class)
    fun resolve(view: DivViewFacade, stateId: String): DivStatePath {
        if (stateId.split('/').size % 2 != 0 || view !is Div2View) {
            return DivStatePath.parse(stateId)
        }
        return view.div2Component.tooltipController.resolveStatePath(view, stateId)
    }
}
