package com.yandex.div.core.tooltip

import com.yandex.div.core.annotations.PublicApi
import com.yandex.div.core.dagger.DivScope
import javax.inject.Inject

/**
 * Used to help handling all tooltips inside [com.yandex.div.core.Div2Context].
 */
@DivScope
@PublicApi
class DivTooltipManager @Inject internal constructor(
    private val tooltipController: DivTooltipController,
) {
    /**
     * Captures all currently shown tooltips.
     */
    fun captureShownTooltips(): List<TooltipMetadata> {
        return tooltipController.captureCurrentTooltips().map {
            TooltipMetadata(
                id = it.id,
                initiator = it.bindingContext.divView,
            )
        }
    }

    /**
     * @return `true` if at least one tooltip was cancelled, `false` otherwise.
     */
    fun cancelAllTooltips(): Boolean {
        return tooltipController.cancelAllTooltips()
    }
}
