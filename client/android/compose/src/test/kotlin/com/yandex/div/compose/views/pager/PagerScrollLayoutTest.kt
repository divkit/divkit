package com.yandex.div.compose.views.pager

import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.lazy.LazyListState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.pager.PagerItemWindow
import kotlin.test.Test
import kotlin.test.assertEquals
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PagerScrollLayoutTest {

    @Test
    fun `current page is the initial page before the first layout`() {
        // A centred page 2 of 100 px in a 300 px viewport starts the list at item 1.
        val listState = LazyListState(firstVisibleItemIndex = 1)
        val layout = PagerScrollLayout(
            listState = listState,
            itemWindow = PagerItemWindow(5),
            initialPage = 2,
            snapPosition = SnapPosition.Center,
            pageSize = { null },
            selectedActionsHandler = {},
        )

        assertEquals(2, layout.currentPage)
    }
}
