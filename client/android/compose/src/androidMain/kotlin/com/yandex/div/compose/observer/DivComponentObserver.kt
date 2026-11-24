package com.yandex.div.compose.observer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.defaultComponentObserver
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.observer.events.DivGalleryScrollEvent
import com.yandex.div.compose.observer.events.DivPagerPageChangedEvent
import com.yandex.div.compose.observer.events.DivTabChangedEvent
import com.yandex.div.compose.observer.events.DivTabTitlesScrolledEvent
import com.yandex.div.compose.views.tabs.LocalIsMeasurementPass

/**
 * Observes scroll and selection events from [DivView] on the UI thread.
 * Override only the events your application consumes, and install the observer via
 * [DivConfiguration.Builder.componentObserver]. For example:
 *
 *     divConfiguration {
 *         componentObserver = object : DivComponentObserver {
 *             override fun onTabChanged(event: DivTabChangedEvent) {
 *                 saveSelectedTab(event.cardId, event.selectedTab)
 *             }
 *         }
 *     }
 */
interface DivComponentObserver {
    /**
     * Reports the first significant movement in a gesture (including its fling) or scroll action.
     * The accumulated absolute distance must exceed 5% of the viewport along the scroll axis.
     * Further movement in the same gesture or action does not repeat this event.
     * Use [onGalleryScrollCompleted] to observe completed movements, including shorter scrolls.
     */
    fun onGallerySignificantScroll(event: DivGalleryScrollEvent): Unit = Unit

    /**
     * Called after a gesture (including its fling) or action moves the gallery and finishes.
     * A short movement can finish without an [onGallerySignificantScroll] notification.
     */
    fun onGalleryScrollCompleted(event: DivGalleryScrollEvent): Unit = Unit

    /**
     * Called after the selected pager page changes and scrolling settles, whether caused by a
     * gesture, a scroll action or an update to the page list. Initial selection and restoration
     * do not emit this event.
     */
    fun onPagerPageChanged(event: DivPagerPageChangedEvent): Unit = Unit

    /**
     * Called after the selected tab changes and scrolling settles. This includes swipes, title
     * taps, scroll actions and updates to selected_tab. Initial selection does not emit this event.
     */
    fun onTabChanged(event: DivTabChangedEvent): Unit = Unit

    /**
     * Called once when the user scrolls the tab title strip. This does not require a tab change.
     */
    fun onTabTitlesScrolled(event: DivTabTitlesScrolledEvent): Unit = Unit
}

/**
 * The configured observer, or null when none is installed or the content is only measured.
 */
internal val activeComponentObserver: DivComponentObserver?
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsMeasurementPass.current) {
        null
    } else {
        LocalComponent.current.componentObserver.takeUnless { it === defaultComponentObserver }
    }
