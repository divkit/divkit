package com.yandex.div.core.util

import android.net.Uri
import com.yandex.div.core.asExpression
import com.yandex.div.core.mockExpressionResolver
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.test.data.container
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivGallery
import com.yandex.div2.DivImage
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivWalkTreeTest {

    private val resolver = mockExpressionResolver()
    private val path = DivStatePath.fromState(0)

    @Test
    fun `walking single node hierarchy`() {
        val rootDiv = text(text = "lorem ipsum")

        val divWalk = rootDiv.walk(resolver, path)
            .map { item -> item.div.type }

        assertEquals(listOf("text"), divWalk.toList())
    }

    @Test
    fun `walking multiple node hierarchy`() {
        val rootDiv = container(
            items = listOf(
                text(text = "lorem ipsum"),
                divImage()
            )
        )

        val divWalk = rootDiv.walk(resolver, path)
            .map { item -> item.div.type }

        assertEquals(listOf("container", "text", "image"), divWalk.toList())
    }

    @Test
    fun `walking uses depth-first order`() {
        val rootDiv = container(
            items = listOf(
                container(
                    items = listOf(
                        text(text = "lorem ipsum"),
                        divImage()
                    )
                ),
                text(text = "lorem ipsum")
            )
        )

        val divWalk = rootDiv.walk(resolver, path)
            .map { item -> item.div.type }

        assertEquals(listOf("container", "container", "text", "image", "text"), divWalk.toList())
    }

    @Test
    fun `onEnter excludes subtree from walking`() {
        val rootDiv = container(
            items = listOf(
                divGallery(
                    listOf(
                        text(text = "lorem ipsum"),
                        divImage()
                    )
                ),
                text(text = "lorem ipsum")
            )
        )

        val divWalk = rootDiv.walk(resolver, path)
            .onEnter { div -> div !is Div.Gallery }
            .map { item -> item.div.type }

        assertEquals(listOf("container", "text"), divWalk.toList())
    }

    @Test
    fun `maxDepth limits walk depth`() {
        val rootDiv = container(
            items = listOf(
                text(text = "lorem ipsum"),
                container(
                    items = listOf(
                        text(text = "lorem ipsum"),
                        divImage()
                    )
                )
            )
        )

        val divWalk = rootDiv.walk(resolver, path)
            .maxDepth(1)
            .map { item -> item.div.type }

        assertEquals(listOf("container", "text", "container"), divWalk.toList())
    }

    @Test
    fun `onEnter() called only for branch nodes`() {
        val rootDiv = container(
            items = listOf(
                text(text = "lorem ipsum"),
                divGallery(
                    listOf(
                        text(text = "lorem ipsum"),
                        divImage()
                    )
                )
            )
        )

        val enteredDivs = mutableListOf<String>()
        rootDiv.walk(resolver, path)
            .onEnter { div ->
                enteredDivs += div.type
                true
            }
            .toList()

        assertEquals(listOf("container", "gallery"), enteredDivs)
    }

    @Test
    fun `onLeave() called only for branch nodes`() {
        val rootDiv = container(
            items = listOf(
                text(text = "lorem ipsum"),
                divGallery(
                    listOf(
                        text(text = "lorem ipsum"),
                        divImage()
                    )
                )
            )
        )

        val leftDivs = mutableListOf<String>()
        rootDiv.walk(resolver, path)
            .onLeave { div -> leftDivs += div.type }
            .toList()

        assertEquals(listOf("gallery", "container"), leftDivs)
    }

    private fun divImage() = Div.Image(DivImage(imageUrl = Uri.parse("https://none").asExpression()))

    private fun divGallery(items: List<Div>): Div {
        return Div.Gallery(DivGallery(items = items))
    }
}
