package com.yandex.div.compose.pager

import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.grid
import com.yandex.div.test.data.indicator
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.state
import com.yandex.div.test.data.tabItem
import com.yandex.div.test.data.tabs
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertSame

class NearestPagerTest {
    private val indicator = indicator()
    private val near = pager()
    private val far = pager()

    @Test
    fun `nearest pager is found before indicator without ids`() {
        val root = container(items = listOf(container(items = listOf(near, indicator)), far))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `nearest pager is found after indicator without ids`() {
        val root = container(items = listOf(far, container(items = listOf(indicator, near))))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `first pager wins a distance tie before indicator`() {
        val root = container(items = listOf(near, far, indicator))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `first pager wins a distance tie after indicator`() {
        val root = container(items = listOf(indicator, near, far))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `first pager wins a distance tie around indicator`() {
        val root = container(items = listOf(near, indicator, far))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `pager id filters out a closer pager`() {
        val indicator = indicator(pagerId = "target")
        val target = pager(id = "target")
        val root = container(items = listOf(target, container(items = listOf(near, indicator))))

        assertSame(target.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `nearest pager is selected among duplicate ids`() {
        val indicator = indicator(pagerId = "target")
        val target = pager(id = "target")
        val other = pager(id = "target")
        val root = container(items = listOf(other, container(items = listOf(indicator, target))))

        assertSame(target.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `containing pager is nearer than a sibling pager`() {
        val parent = pager(items = listOf(indicator))
        val root = container(items = listOf(parent, far))

        assertSame(parent.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `lookup visits div states`() {
        val root = state(
            states = listOf(
                state(stateId = "first", div = far),
                state(stateId = "second", div = container(items = listOf(indicator, near))),
            )
        )

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `lookup visits gallery items`() {
        val root = container(items = listOf(far, gallery(items = listOf(indicator, near))))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `lookup visits custom items`() {
        val root = container(items = listOf(far, custom(
            type = "children",
            items = listOf(indicator, near),
        )))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `lookup visits grid items`() {
        val root = container(items = listOf(grid(
            columnCount = constant(1L),
            items = listOf(near, indicator),
        ), far))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `lookup visits tab contents`() {
        val root = tabs(items = listOf(
            tabItem(far, "First"),
            tabItem(container(items = listOf(indicator, near)), "Second"),
        ))

        assertSame(near.value, findNearestPager(root, indicator.value))
    }

    @Test
    fun `missing pager id does not fall back to another pager`() {
        val indicator = indicator(pagerId = "missing")
        val root = container(items = listOf(near, indicator))

        assertNull(findNearestPager(root, indicator.value))
    }

    @Test
    fun `missing indicator does not select a pager`() {
        assertNull(findNearestPager(near, indicator.value))
    }

    @Test
    fun `missing pager leaves indicator unbound`() {
        assertNull(findNearestPager(indicator, indicator.value))
    }
}
