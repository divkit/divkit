package com.yandex.div.internal.core

import android.net.Uri
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
class DivTextImageBuilderTest {

    private val store = mock<RuntimeStoreImpl> {
        on { getOrPutItemBuilderResolver(any(), any()) } doAnswer {
            createResolver(it.arguments[0] as String)
        }
    }
    private val resolver = createResolver("")

    private fun createResolver(path: String): ExpressionResolverImpl =
        ExpressionResolverImpl(path, store, mock(), mock(), mock())

    @Test
    fun `builder produces an image per data element`() {
        val text = createText(
            imageBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject(), JSONObject()),
                prototypes = listOf(prototype(image()))
            )
        )

        val images = text.buildImages(resolver)

        assertEquals(3, images?.size)
    }

    @Test
    fun `builder takes priority over static images`() {
        val text = createText(
            images = listOf(image(start = 1L), image(start = 2L)),
            imageBuilder = builder(
                data = jsonArray(JSONObject()),
                prototypes = listOf(prototype(image(start = 7L)))
            )
        )

        val images = text.buildImages(resolver)

        assertEquals(1, images?.size)
        assertEquals(7L, images?.first()?.image?.start?.evaluate(resolver))
    }

    @Test
    fun `static images are used when no builder is set`() {
        val text = createText(images = listOf(image(), image()))

        val images = text.buildImages(resolver)

        assertEquals(2, images?.size)
        images?.forEach { assertSame(resolver, it.resolver) }
    }

    @Test
    fun `selector chooses prototype per element`() {
        val text = createText(
            imageBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(
                    prototype(image(start = 10L), selectorForIndex(0)),
                    prototype(image(start = 20L), selectorForIndex(1)),
                )
            )
        )

        val images = text.buildImages(resolver)

        assertEquals(2, images?.size)
        assertEquals(10L, images?.get(0)?.image?.start?.evaluate(resolver))
        assertEquals(20L, images?.get(1)?.image?.start?.evaluate(resolver))
    }

    @Test
    fun `element with no matching prototype is skipped`() {
        val text = createText(
            imageBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(prototype(image(), selectorForIndex(0)))
            )
        )

        val images = text.buildImages(resolver)

        assertEquals(1, images?.size)
    }

    @Test
    fun `null when neither images nor builder are set`() {
        val images = createText().buildImages(resolver)

        assertNull(images)
    }

    @Test
    fun `ellipsis builder produces an image per data element`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            imageBuilder = builder(
                data = jsonArray(JSONObject(), JSONObject()),
                prototypes = listOf(prototype(image()))
            )
        )

        val images = ellipsis.buildImages(resolver)

        assertEquals(2, images?.size)
    }

    @Test
    fun `ellipsis builder takes priority over static images`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            images = listOf(image(start = 1L)),
            imageBuilder = builder(
                data = jsonArray(JSONObject()),
                prototypes = listOf(prototype(image(start = 7L)))
            )
        )

        val images = ellipsis.buildImages(resolver)

        assertEquals(1, images?.size)
        assertEquals(7L, images?.first()?.image?.start?.evaluate(resolver))
    }

    private fun createText(
        images: List<DivText.Image>? = null,
        imageBuilder: DivText.ImageBuilder? = null,
    ) = DivText(
        text = Expression.constant("text"),
        images = images,
        imageBuilder = imageBuilder,
    )

    private fun builder(
        data: org.json.JSONArray,
        prototypes: List<DivText.ImageBuilder.Prototype>,
    ) = DivText.ImageBuilder(
        data = Expression.constant(data),
        prototypes = prototypes,
    )

    private fun prototype(
        image: DivText.Image,
        selector: Expression<Boolean> = Expression.constant(true),
    ) = DivText.ImageBuilder.Prototype(image = image, selector = selector)

    private fun image(start: Long = 0L) = DivText.Image(
        start = Expression.constant(start),
        url = Expression.constant(Uri.parse("https://divkit.tech/image.png")),
    )

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
