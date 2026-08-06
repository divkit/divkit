package com.yandex.div.core.tooltip

import android.view.View
import androidx.activity.OnBackPressedCallback
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivPreloader
import com.yandex.div.core.util.SafePopupWindow
import com.yandex.div.core.view2.BindingContext
import com.yandex.div2.DivTooltip

internal class TooltipData(
    val id: String,
    val scopeId: String?,
    val bindingContext: BindingContext,
    val divTooltip: DivTooltip,
    val anchor: View,
) {
    var popupWindow: SafePopupWindow? = null
    var onBackPressedCallback: OnBackPressedCallback? = null
    var ticket: DivPreloader.Ticket? = null
    var dismissed = false
    var anchorTrackingDisposable: Disposable? = null

    fun stopAnchorTracking() {
        anchorTrackingDisposable?.close()
        anchorTrackingDisposable = null
    }
}

internal val TooltipData.tooltipContainer get() = popupWindow?.contentView as? DivTooltipContainer
