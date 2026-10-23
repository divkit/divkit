package com.yandex.div.core.tooltip

import com.yandex.div.core.dagger.DivScope
import javax.inject.Inject

/**
 * Used to help handling all tooltips inside [com.yandex.div.core.Div2Context].
 */
@DivScope
class DivTooltipManager @Inject internal constructor(
    private val tooltipController: DivTooltipController,
) {
    /**
     * Captures all currently shown tooltips.
     * Includes open popup windows whose content has not been laid out yet.
     * Tooltips that are still preloading their content are not included.
     */
    fun captureShownTooltips(): List<TooltipMetadata> {
        return tooltipController.captureShownTooltips().map {
            TooltipMetadata(id = it.id, initiator = it.divView)
        }
    }

    /**
     * @return `true` if at least one tooltip was cancelled, `false` otherwise.
     */
    fun cancelAllTooltips(): Boolean {
        return tooltipController.cancelAllTooltips()
    }
}
