package com.yandex.div.core.tooltip

import android.os.Handler
import android.os.Looper
import android.view.View
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.DivTooltipRestrictor
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div2.DivTooltip
import javax.inject.Inject

@DivScope
internal class ActiveTooltipFactory @Inject constructor(
    private val tooltipRestrictor: DivTooltipRestrictor,
    private val divPreloader: DivPreloader,
    private val viewFactory: DivTooltipViewFactory,
) {

    private val mainThreadHandler = Handler(Looper.getMainLooper())

    fun create(
        divTooltip: DivTooltip,
        anchor: View,
        divView: Div2View,
        multiple: Boolean,
        scopeId: String?,
        onFinished: (ActiveTooltipComponent) -> Unit,
    ): ActiveTooltipComponent? {
        val anchorBlock = anchor.divBlock ?: return null
        if (!tooltipRestrictor.canShowTooltip(divView, anchor, divTooltip, multiple, scopeId)) {
            return null
        }

        val data = TooltipData(
            id = divTooltip.id,
            scopeId = scopeId,
            divTooltip = divTooltip,
            anchor = anchor,
            anchorBlock = anchorBlock,
            divView = divView,
        )
        return ActiveTooltipComponent(
            data = data,
            tooltipRestrictor = tooltipRestrictor,
            divPreloader = divPreloader,
            viewFactory = viewFactory,
            mainThreadHandler = mainThreadHandler,
            onFinished = onFinished,
        )
    }
}
