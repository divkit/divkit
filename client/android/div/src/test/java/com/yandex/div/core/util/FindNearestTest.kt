package com.yandex.div.core.util

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.mockExpressionResolver
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.test.data.container
import com.yandex.div.test.data.indicator
import com.yandex.div.test.data.pager
import com.yandex.div2.Div
import com.yandex.div2.DivPager
import kotlin.test.Test
import kotlin.test.assertSame
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FindNearestTest {
    private val resolver = mockExpressionResolver()
    private val indicator = indicator()
    private val near = pager()
    private val far = pager()

    @Test
    fun `nearest pager is selected before the indicator`() {
        val root = container(items = listOf(container(items = listOf(near, indicator)), far))

        assertSame(near.value, nearestPager(root))
    }

    @Test
    fun `nearest pager is selected after the indicator`() {
        val root = container(items = listOf(far, container(items = listOf(indicator, near))))

        assertSame(near.value, nearestPager(root))
    }

    @Test
    fun `equal distances select the first pager in tree order`() {
        val root = container(items = listOf(near, indicator, far))

        assertSame(near.value, nearestPager(root))
    }

    private fun nearestPager(root: Div): DivPager? = findNearest<DivPager>(
        root, resolver, DivStatePath.fromState(0), indicator.value
    ) { true }
}
