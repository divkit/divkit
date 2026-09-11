package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AdjustScrollToItemTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `backward target keeps lanes assigned from the first item`() {
        lateinit var gridState: LazyStaggeredGridState
        var targetIndex by mutableIntStateOf(8)
        composeRule.setContent {
            gridState = rememberLazyStaggeredGridState(initialFirstVisibleItemIndex = 8)
            AdjustScrollToItem(
                gridState = gridState,
                targetIndex = targetIndex,
                totalItemsCount = 10,
                isHorizontal = false,
                desiredOffset = { _, _ -> 0 },
            )
            TestStaggeredGrid(gridState = gridState, itemsCount = 10)
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle { targetIndex = 1 }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(
                with(composeRule.density) { 100.dp.roundToPx() },
                gridState.layoutInfo.visibleItemsInfo.single { it.index == 1 }.offset.x,
            )
        }
    }

    @Test
    fun `target is aligned when the updated grid layout becomes available`() {
        lateinit var gridState: LazyStaggeredGridState
        var itemsCount by mutableIntStateOf(6)
        var layoutReady by mutableStateOf(true)
        composeRule.setContent {
            gridState = rememberLazyStaggeredGridState()
            AdjustScrollToItem(
                gridState = gridState,
                targetIndex = 12.coerceAtMost(itemsCount - 1),
                totalItemsCount = itemsCount,
                isHorizontal = false,
                desiredOffset = { _, _ -> 0 },
            )
            if (layoutReady) {
                TestStaggeredGrid(gridState = gridState, itemsCount = itemsCount)
            }
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            itemsCount = 20
            layoutReady = false
        }
        composeRule.waitForIdle()
        composeRule.runOnIdle { layoutReady = true }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(12, gridState.firstVisibleItemIndex)
            assertEquals(0, gridState.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun `invalid target keeps staggered grid position`() {
        lateinit var gridState: LazyStaggeredGridState
        composeRule.setContent {
            gridState = rememberLazyStaggeredGridState(
                initialFirstVisibleItemIndex = 2,
                initialFirstVisibleItemScrollOffset = 15,
            )
            AdjustScrollToItem(
                gridState = gridState,
                targetIndex = 8,
                totalItemsCount = 6,
                isHorizontal = false,
                desiredOffset = { _, _ -> 0 },
            )
            TestStaggeredGrid(gridState = gridState, itemsCount = 6)
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(2, gridState.firstVisibleItemIndex)
            assertEquals(15, gridState.firstVisibleItemScrollOffset)
        }
    }

    @Test
    fun `unreachable backward target restores position when viewport is empty`() {
        lateinit var gridState: LazyStaggeredGridState
        composeRule.setContent {
            gridState = rememberLazyStaggeredGridState(
                initialFirstVisibleItemIndex = 8,
                initialFirstVisibleItemScrollOffset = 15,
            )
            AdjustScrollToItem(
                gridState = gridState,
                targetIndex = 3,
                totalItemsCount = 10,
                isHorizontal = false,
                desiredOffset = { _, _ -> 0 },
            )
            TestStaggeredGrid(gridState = gridState, itemsCount = 10, height = 0.dp)
        }
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertEquals(8, gridState.firstVisibleItemIndex)
            assertEquals(15, gridState.firstVisibleItemScrollOffset)
        }
    }

    @Composable
    private fun TestStaggeredGrid(
        gridState: LazyStaggeredGridState,
        itemsCount: Int,
        height: Dp = 100.dp,
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.size(width = 200.dp, height = height),
            state = gridState,
        ) {
            items(itemsCount) { index ->
                Box(
                    Modifier
                        .height(100.dp)
                        .testTag("item$index")
                )
            }
        }
    }
}
