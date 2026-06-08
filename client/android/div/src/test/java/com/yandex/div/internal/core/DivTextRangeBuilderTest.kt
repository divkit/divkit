package com.yandex.div.internal.core

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.local.RuntimeStoreImpl
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.DivText
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivTextRangeBuilderTest {

    private val store = mock<RuntimeStoreImpl> {
        on { getOrPutItemBuilderResolver(any(), any()) } doAnswer {
            createResolver(it.arguments[0] as String)
        }
    }
    private val resolver = createResolver("")

    private fun createResolver(path: String): ExpressionResolverImpl =
        ExpressionResolverImpl(path, store, mock(), mock(), mock())

    @Test
    fun `builder produces a range per data element`() {
        val text = createText(
            rangeBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject(), JSONObject()),
                prototypes = listOf(prototype(range()))
            )
        )

        val ranges = text.buildRanges(resolver)

        assertEquals(3, ranges?.size)
    }

    @Test
    fun `builder takes priority over static ranges`() {
        val text = createText(
            ranges = listOf(range(start = 1L), range(start = 2L)),
            rangeBuilder = builder(
                data = jsonArray(JSONObject()),
                prototypes = listOf(prototype(range(start = 7L)))
            )
        )

        val ranges = text.buildRanges(resolver)

        assertEquals(1, ranges?.size)
        assertEquals(7L, ranges?.first()?.range?.start?.evaluate(resolver))
    }

    @Test
    fun `static ranges are used when no builder is set`() {
        val text = createText(ranges = listOf(range(), range()))

        val ranges = text.buildRanges(resolver)

        assertEquals(2, ranges?.size)
        ranges?.forEach { assertSame(resolver, it.resolver) }
    }

    @Test
    fun `selector chooses prototype per element`() {
        val text = createText(
            rangeBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(
                    prototype(range(start = 10L), selectorForIndex(0)),
                    prototype(range(start = 20L), selectorForIndex(1)),
                )
            )
        )

        val ranges = text.buildRanges(resolver)

        assertEquals(2, ranges?.size)
        assertEquals(10L, ranges?.get(0)?.range?.start?.evaluate(resolver))
        assertEquals(20L, ranges?.get(1)?.range?.start?.evaluate(resolver))
    }

    @Test
    fun `element with no matching prototype is skipped`() {
        val text = createText(
            rangeBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(prototype(range(), selectorForIndex(0)))
            )
        )

        val ranges = text.buildRanges(resolver)

        assertEquals(1, ranges?.size)
    }

    @Test
    fun `null when neither ranges nor builder are set`() {
        val ranges = createText().buildRanges(resolver)

        assertNull(ranges)
    }

    @Test
    fun `ellipsis builder produces a range per data element`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            rangeBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(prototype(range()))
            )
        )

        val ranges = ellipsis.buildRanges(resolver)

        assertEquals(2, ranges?.size)
    }

    @Test
    fun `ellipsis builder takes priority over static ranges`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            ranges = listOf(range(start = 1L)),
            rangeBuilder = builder(
                data = jsonArray(JSONObject()),
                prototypes = listOf(prototype(range(start = 7L)))
            )
        )

        val ranges = ellipsis.buildRanges(resolver)

        assertEquals(1, ranges?.size)
        assertEquals(7L, ranges?.first()?.range?.start?.evaluate(resolver))
    }

    private fun createText(
        ranges: List<DivText.Range>? = null,
        rangeBuilder: DivText.RangeBuilder? = null,
    ) = DivText(
        text = Expression.constant("text"),
        ranges = ranges,
        rangeBuilder = rangeBuilder,
    )

    private fun builder(
        data: org.json.JSONArray,
        prototypes: List<DivText.RangeBuilder.Prototype>,
    ) = DivText.RangeBuilder(
        data = Expression.constant(data),
        prototypes = prototypes,
    )

    private fun prototype(
        range: DivText.Range,
        selector: Expression<Boolean> = Expression.constant(true),
    ) = DivText.RangeBuilder.Prototype(range = range, selector = selector)

    private fun range(start: Long = 0L) = DivText.Range(start = Expression.constant(start))

    private fun jsonArray(vararg elements: Any): org.json.JSONArray = org.json.JSONArray().apply {
        elements.forEach { put(it) }
    }

    private fun selectorForIndex(index: Int): Expression<Boolean> = mock {
        on { evaluate(any()) } doAnswer {
            val r = it.arguments[0] as ExpressionResolverImpl
            r.path.endsWith(":$index")
        }
        on { observe(any(), anyOrNull()) } doReturn mock()
    }
}
