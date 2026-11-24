package com.yandex.div.compose.observer

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.observer.events.DivPagerPageChangedEvent
import com.yandex.div.compose.observer.events.DivScrollDirection
import com.yandex.div.compose.views.pager.PagerPageListener
import com.yandex.div.render.common.pager.PagerItemWindow

/**
 * Returns the observation of the pager's selected page, or null when it is not observed.
 * The observation outlives updates to the page list of the same pager.
 */
@Composable
internal fun rememberPagerPageObservation(pagerId: String?, listState: LazyListState): PagerPageObservation? {
    val observer = activeComponentObserver ?: return null
    val component = LocalDivViewContext.current.component
    return remember(component, listState) { PagerPageObservation(observer, component.data.logId, pagerId) }
}

/**
 * Reports changes of the selected page. The first settled page is the baseline.
 */
internal class PagerPageObservation(
    private val observer: DivComponentObserver,
    private val cardId: String,
    private val pagerId: String?,
) : PagerPageListener {
    private var itemWindow: PagerItemWindow? = null
    private var rawPage: Int = 0

    override fun onPageSettled(itemWindow: PagerItemWindow, rawPage: Int) {
        val previousWindow = this.itemWindow
        val previousRawPage = this.rawPage
        this.itemWindow = itemWindow
        this.rawPage = rawPage
        if (previousWindow == null) return

        val page = itemWindow.realIndex(rawPage)
        val previousPage = previousWindow.realIndex(previousRawPage)
        if (page == previousPage) return

        // Raw indices describe scroll direction only within the same cyclic window.
        val isForward = if (itemWindow == previousWindow) {
            rawPage > previousRawPage
        } else {
            page > previousPage
        }
        val direction = if (isForward) DivScrollDirection.FORWARD else DivScrollDirection.BACKWARD
        observer.onPagerPageChanged(DivPagerPageChangedEvent(cardId, pagerId, page, direction))
    }
}
