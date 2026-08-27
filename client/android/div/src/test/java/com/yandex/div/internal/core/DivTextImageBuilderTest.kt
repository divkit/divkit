package com.yandex.div.internal.core

import android.net.Uri
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.evaluable.EvaluationContext
import com.yandex.div.evaluable.Evaluator
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.booleanExpression
import com.yandex.div2.DivText
import org.json.JSONArray
import org.json.JSONObject
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DivTextImageBuilderTest {

    private val itemResolvers = mutableMapOf<String, ExpressionResolver>()
    private val runtimeStore = mock<RuntimeStore> {
        on { getOrPutItemBuilderResolver(any(), any()) } doAnswer {
            val itemPath = it.getArgument<String>(0)
            val createResolver = it.getArgument<() -> ExpressionResolver>(1)
            itemResolvers.getOrPut(itemPath, createResolver)
        }
    }
    private val evaluator = Evaluator(EvaluationContext(
        variableProvider = { null },
        storedValueProvider = mock(),
        functionProvider = mock(),
        warningSender = { _, _ -> },
    ))
    private val resolver = ExpressionResolverImpl(runtimeStore, mock(), evaluator, mock())
    private val path = DivStatePath.fromState(0)

    @Test
    fun `builder creates one result for every data element`() {
        val text = text(imageBuilder = builder(3, prototype(image(start = 7L))))

        val results = requireNotNull(text.buildImages(resolver, path))

        assertEquals(listOf(7L, 7L, 7L), results.starts())
    }

    @Test
    fun `builder selects prototype using item index`() {
        val text = text(
            imageBuilder = builder(
                3,
                prototype(image(start = 10L), indexIs(0)),
                prototype(image(start = 20L), indexIs(1)),
                prototype(image(start = 30L), indexIs(2)),
            ),
        )

        val results = requireNotNull(text.buildImages(resolver, path))

        assertEquals(listOf(10L, 20L, 30L), results.starts())
    }

    @Test
    fun `builder omits elements without a matching prototype`() {
        val text = text(imageBuilder = builder(2, prototype(image(start = 10L), indexIs(0))))

        val results = requireNotNull(text.buildImages(resolver, path))

        assertEquals(listOf(10L), results.starts())
    }

    @Test
    fun `builder results take priority over static images`() {
        val text = text(
            images = listOf(image(start = 1L)),
            imageBuilder = builder(1, prototype(image(start = 7L))),
        )

        val results = requireNotNull(text.buildImages(resolver, path))

        assertEquals(listOf(7L), results.starts())
    }

    @Test
    fun `static images keep image and root resolver when builder is absent`() {
        val firstImage = image(start = 1L)
        val secondImage = image(start = 2L)

        val results = requireNotNull(
            text(images = listOf(firstImage, secondImage)).buildImages(resolver, path),
        )

        assertEquals(
            listOf(firstImage to resolver, secondImage to resolver),
            results.map { it.image to it.resolver },
        )
    }

    @Test
    fun `text images are absent when builder and static images are absent`() {
        assertNull(text().buildImages(resolver, path))
    }

    @Test
    fun `ellipsis builder uses item selectors`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            imageBuilder = builder(
                2,
                prototype(image(start = 10L), indexIs(0)),
                prototype(image(start = 20L), indexIs(1)),
            ),
        )

        val results = requireNotNull(ellipsis.buildImages(resolver, path))

        assertEquals(listOf(10L, 20L), results.starts())
    }

    @Test
    fun `ellipsis builder results take priority over static images`() {
        val ellipsis = DivText.Ellipsis(
            text = Expression.constant("..."),
            images = listOf(image(start = 1L)),
            imageBuilder = builder(1, prototype(image(start = 7L))),
        )

        val results = requireNotNull(ellipsis.buildImages(resolver, path))

        assertEquals(listOf(7L), results.starts())
    }

    private fun text(
        images: List<DivText.Image>? = null,
        imageBuilder: DivText.ImageBuilder? = null,
    ) = DivText(
        text = Expression.constant("text"),
        images = images,
        imageBuilder = imageBuilder,
    )

    private fun builder(
        dataSize: Int,
        vararg prototypes: DivText.ImageBuilder.Prototype,
    ) = DivText.ImageBuilder(
        data = Expression.constant(JSONArray().apply { repeat(dataSize) { put(JSONObject()) } }),
        prototypes = prototypes.toList(),
    )

    private fun prototype(
        image: DivText.Image,
        selector: Expression<Boolean> = Expression.constant(true),
    ) = DivText.ImageBuilder.Prototype(image = image, selector = selector)

    private fun image(start: Long) = DivText.Image(
        start = Expression.constant(start),
        url = Expression.constant(Uri.parse("https://divkit.tech/image-$start.png")),
    )

    private fun indexIs(expectedIndex: Long) = booleanExpression("@{index == $expectedIndex}")

    private fun List<DivTextImageResult>.starts(): List<Long> {
        return map { it.image.start.evaluate(it.resolver) }
    }
}
