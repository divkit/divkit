package com.yandex.divkit.demo.div

import com.yandex.div.compose.observer.DivComponentObserver
import com.yandex.div.compose.observer.events.DivGalleryScrollEvent
import com.yandex.div.compose.observer.events.DivPagerPageChangedEvent
import com.yandex.div.compose.observer.events.DivScrollDirection
import com.yandex.div.compose.observer.events.DivTabChangedEvent
import com.yandex.div.compose.observer.events.DivTabTitlesScrolledEvent
import com.yandex.div.internal.KLog
import com.yandex.divkit.regression.ScenarioLogDelegate

internal class DemoComponentObserver(
    private val logDelegate: ScenarioLogDelegate = ScenarioLogDelegate.Stub,
) : DivComponentObserver {
    override fun onGallerySignificantScroll(event: DivGalleryScrollEvent) {
        log("logGalleryScroll(cardId = ${event.cardId})")
    }

    override fun onGalleryScrollCompleted(event: DivGalleryScrollEvent) {
        log(
            "logGalleryCompleteScroll(cardId = ${event.cardId}, firstVisibleItem = ${event.firstVisibleItem}, " +
                "lastVisibleItem = ${event.lastVisibleItem}, scrollDirection = ${event.scrollDirection.logName()})"
        )
    }

    override fun onPagerPageChanged(event: DivPagerPageChangedEvent) {
        log(
            "logPagerChangePage(cardId = ${event.cardId}, currentPageIndex = ${event.currentPageIndex}), " +
                "scrollDirection = ${event.scrollDirection.logName()})"
        )
    }

    override fun onTabChanged(event: DivTabChangedEvent) {
        log("logTabPageChanged(cardId = ${event.cardId}, selectedTab = ${event.selectedTab})")
    }

    override fun onTabTitlesScrolled(event: DivTabTitlesScrolledEvent) {
        log("logTabTitlesScroll(cardId = ${event.cardId})")
    }

    private fun log(message: String) {
        KLog.d("DemoComponentObserver") { message }
        logDelegate.println(message)
    }
}

private fun DivScrollDirection.logName(): String {
    return when (this) {
        DivScrollDirection.FORWARD -> "next"
        DivScrollDirection.BACKWARD -> "back"
    }
}
