package com.yandex.div.core.view2.divs

import android.os.Looper
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.divs.tabs.DivTabsBinder
import com.yandex.div.core.view2.divs.widgets.DivTabsLayout
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.toBlock
import com.yandex.div.internal.viewpool.PseudoViewPool
import com.yandex.div.internal.widget.tabs.TabTextStyleProvider
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import org.junit.runner.RunWith
import org.mockito.kotlin.clearInvocations
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.Shadows.shadowOf
import kotlin.test.Test

@RunWith(AndroidJUnit4::class)
class DivTabsBinderTest : DivBinderTest() {

    private val tracker = mock<DivVisibilityActionTracker>()
    private val underTest = DivTabsBinder(
        baseBinder = baseBinder,
        viewCreator = viewCreator,
        divBinder = { mock() },
        viewPool = PseudoViewPool(),
        textStyleProvider = TabTextStyleProvider(mock()),
        actionPerformer = actionPerformer,
        div2Logger = mock(),
        imageLoader = imageLoader,
        visibilityActionTracker = tracker,
        context = context,
        animationsEnabledController = mock(),
    )

    @Test
    fun `adapter replacement tracks the selected tab after posted display`() {
        whenever(dataComponent.tabsStateCache).thenReturn(mock())
        whenever(dataComponent.runtimeVisitor).thenReturn(mock())
        val firstTab = text(text = "first")
        val secondTab = text(text = "second")
        val div = tabs(
            items = listOf(tabItem(firstTab, "first"), tabItem(secondTab, "second")),
            selectedTab = constant(1L),
        ) as Div.Tabs
        val view = DivTabsLayout(context)
        underTest.bindView(view, div.toBlock(resolver, rootPath()) as DivBlock.Tabs, divView)
        shadowOf(Looper.getMainLooper()).idle()
        clearInvocations(tracker)
        val replacement = Div.Tabs(div.value.copy(dynamicHeight = constant(true)))

        underTest.bindView(view, replacement.toBlock(resolver, rootPath()) as DivBlock.Tabs, divView)
        shadowOf(Looper.getMainLooper()).idle()

        verify(tracker).startTrackingViewsHierarchy(view, secondTab, resolver, divView)
        verifyNoMoreInteractions(tracker)
    }
}
