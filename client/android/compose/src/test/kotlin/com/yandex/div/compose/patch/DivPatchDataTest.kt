package com.yandex.div.compose.patch

import com.yandex.div.test.data.container
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * A patch copies the owner of an item builder as a new model, and the copy keeps building the
 * same items.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class DivPatchDataTest(private val case: Case) {

    @Test
    fun `copy for a patch keeps the item builder`() {
        val builder = itemBuilder(
            data = jsonArray(jsonObject()),
            prototypes = listOf(prototype(div = text(text = "item"))),
        )

        val copy = case.owner(builder).copyForPatch()

        assertSame(builder, case.itemBuilder(copy))
    }

    class Case(
        private val name: String,
        val owner: (DivCollectionItemBuilder) -> Div,
        val itemBuilder: (Div) -> DivCollectionItemBuilder?,
    ) {
        override fun toString() = name
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun cases(): List<Case> = cases

        // The runner calls the provider once per case, so the list is built once.
        private val cases: List<Case> = listOf(
            Case(
                name = "container",
                owner = { container(itemBuilder = it) },
                itemBuilder = { (it as Div.Container).value.itemBuilder },
            ),
            Case(
                name = "gallery",
                owner = { gallery(itemBuilder = it) },
                itemBuilder = { (it as Div.Gallery).value.itemBuilder },
            ),
            Case(
                name = "pager",
                owner = { pager(itemBuilder = it) },
                itemBuilder = { (it as Div.Pager).value.itemBuilder },
            ),
        )
    }
}
