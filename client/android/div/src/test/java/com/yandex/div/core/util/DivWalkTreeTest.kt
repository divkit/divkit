package com.yandex.div.core.util

import android.net.Uri
import com.yandex.div.core.asExpression
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivImage
import com.yandex.div2.DivText
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivWalkTreeTest {

    @Test
    fun `walking single node hierarchy`() {
        val rootDiv = divText("lorem ipsum")

        val divWalk = rootDiv.walk()
            .map { div -> div.type }

        assertEquals(listOf("text"), divWalk.toList())
    }

    @Test
    fun `walking multiple node hierarchy`() {
        val rootDiv = divContainer(
            listOf(
                divText("lorem ipsum"),
                divImage("https://none")
            )
        )

        val divWalk = rootDiv.walk()
            .map { div -> div.type }

        assertEquals(listOf("container", "text", "image"), divWalk.toList())
    }

    @Test
    fun `walking uses depth-first order`() {
        val rootDiv = divContainer(
            listOf(
                divContainer(
                    listOf(
                        divText("lorem ipsum"),
                        divImage("https://none")
                    )
                ),
                divText("lorem ipsum")
            )
        )

        val divWalk = rootDiv.walk()
            .map { div -> div.type }

        assertEquals(listOf("container", "container", "text", "image", "text"), divWalk.toList())
    }

    @Test
    fun `onEnter excludes subtree from walking`() {
        val rootDiv = divContainer(
            listOf(
                divGallery(
                    listOf(
                        divText("lorem ipsum"),
                        divImage("https://none")
                    )
                ),
                divText("lorem ipsum")
            )
        )

        val divWalk = rootDiv.walk()
            .onEnter { div -> div !is Div.Gallery }
            .map { div -> div.type }

        assertEquals(listOf("container", "text"), divWalk.toList())
    }

    @Test
    fun `maxDepth limits walk depth`() {
        val rootDiv = divContainer(
            listOf(
                divText("lorem ipsum"),
                divContainer(
                    listOf(
                        divText("lorem ipsum"),
                        divImage("https://none")
                    )
                )
            )
        )

        val divWalk = rootDiv.walk()
            .maxDepth(1)
            .map { div -> div.type }

        assertEquals(listOf("container", "text", "container"), divWalk.toList())
    }

    @Test
    fun `onEnter() called only for branch nodes`() {
        val rootDiv = divContainer(
            listOf(
                divText("lorem ipsum"),
                divGallery(
                    listOf(
                        divText("lorem ipsum"),
                        divImage("https://none")
                    )
                )
            )
        )

        val enteredDivs = mutableListOf<String>()
        rootDiv.walk()
            .onEnter { div ->
                enteredDivs += div.type
                true
            }
            .toList()

        assertEquals(listOf("container", "gallery"), enteredDivs)
    }

    @Test
    fun `onLeave() called only for branch nodes`() {
        val rootDiv = divContainer(
            listOf(
                divText("lorem ipsum"),
                divGallery(
                    listOf(
                        divText("lorem ipsum"),
                        divImage("https://none")
                    )
                )
            )
        )

        val leftDivs = mutableListOf<String>()
        rootDiv.walk()
            .onLeave { div -> leftDivs += div.type }
            .toList()

        assertEquals(listOf("gallery", "container"), leftDivs)
    }

    private fun divText(text: String): Div {
        return Div.Text(DivText(text = text.asExpression()))
    }

    private fun divImage(imageUrl: String): Div {
        return Div.Image(DivImage(imageUrl = Uri.parse(imageUrl).asExpression()))
    }

    private fun divContainer(items: List<Div>): Div {
        return Div.Container(DivContainer(items = items))
    }

    private fun divGallery(items: List<Div>): Div {
        return Div.Gallery(DivGallery(items = items))
    }
}
