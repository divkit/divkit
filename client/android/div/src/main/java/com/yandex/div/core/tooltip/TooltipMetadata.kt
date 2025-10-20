package com.yandex.div.core.tooltip

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.view2.Div2View

@PublicApi
class TooltipMetadata(
    /**
     * Id that was used at [com.yandex.div2.DivTooltip.id].
     */
    val id: String,

    /**
     * View which initiated show ot tooltip.
     */
    val initiator: Div2View,
)
