package com.yandex.div.compose.collection

import androidx.test.core.app.ApplicationProvider.getApplicationContext
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.dagger.DivLocalComponent
import com.yandex.div.compose.patch.copyForPatch
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.itemBuilder
import com.yandex.div.test.data.jsonArray
import com.yandex.div.test.data.jsonObject
import com.yandex.div.test.data.pager
import com.yandex.div.test.data.prototype
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivCollectionItemBuilder
import org.junit.runner.RunWith
import org.robolectric.ParameterizedRobolectricTestRunner
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

/**
 * The storage keeps the scopes of built items per owner instance, whatever element owns the
 * builder, and releases them together with the scopes of the owners built inside them.
 */
@RunWith(ParameterizedRobolectricTestRunner::class)
class CollectionItemScopeStorageTest(private val case: Case) {
    private val divContext = DivContext(
        getApplicationContext(),
        DivConfiguration(reporter = TestReporter())
    )
    private val viewContext = divContext.getViewContext(data(text(text = "card")))
    private val storage = viewContext.collectionItemScopeStorage
    private val parent = viewContext.rootLocalComponent

    @Test
    fun `an item keeps its scope for the same key`() {
        val builder = leafBuilder()
        val owner = case.owner(builder).value()
        val item = storage.getOrCreateItem(owner, builder)

        assertSame(item, storage.getOrCreateItem(owner, builder))
    }

    @Test
    fun `owners sharing a builder keep separate scopes`() {
        val builder = leafBuilder()
        val item = storage.getOrCreateItem(case.owner(builder).value(), builder)

        assertNotSame(item, storage.getOrCreateItem(case.owner(builder).value(), builder))
    }

    @Test
    fun `retainItems releases the removed items with the scopes of the owners inside them`() {
        val nestedBuilder = leafBuilder()
        val builder = ownerBuilder(case.nested(nestedBuilder))
        val owner = case.owner(builder).value()
        val item = storage.getOrCreateItem(owner, builder)
        val nestedOwner = item.div.value()
        val nestedItem = storage.getOrCreateItem(nestedOwner, nestedBuilder, item.component)

        val released = storage.retainItems(owner, parent, emptyList())

        assertEquals(setOf(nestedOwner, nestedItem.div.value()), released)
        assertNotSame(item, storage.getOrCreateItem(owner, builder))
        assertNotSame(
            nestedItem,
            storage.getOrCreateItem(nestedOwner, nestedBuilder, item.component)
        )
    }

    @Test
    fun `alias keeps the item scopes for a copy of the owner`() {
        val builder = leafBuilder()
        val owner = case.owner(builder)
        val item = storage.getOrCreateItem(owner.value(), builder)
        val copy = owner.copyForPatch().value()

        storage.alias(owner.value(), copy)

        assertSame(item, storage.getOrCreateItem(copy, builder))
    }

    @Test
    fun `retain keeps the scopes of a retained owner and adds the divs it generated`() {
        val nestedBuilder = leafBuilder()
        val builder = ownerBuilder(case.nested(nestedBuilder))
        val owner = case.owner(builder).value()
        val item = storage.getOrCreateItem(owner, builder)
        val nestedOwner = item.div.value()
        val nestedItem = storage.getOrCreateItem(nestedOwner, nestedBuilder, item.component)
        val divs = mutableSetOf(owner)

        storage.retain(divs)

        assertEquals(setOf(owner, nestedOwner, nestedItem.div.value()), divs)
        assertSame(item, storage.getOrCreateItem(owner, builder))
        assertSame(nestedItem, storage.getOrCreateItem(nestedOwner, nestedBuilder, item.component))
    }

    @Test
    fun `retain releases the scopes of an owner that is not retained`() {
        val builder = leafBuilder()
        val owner = case.owner(builder).value()
        val item = storage.getOrCreateItem(owner, builder)

        storage.retain(mutableSetOf())

        assertNotSame(item, storage.getOrCreateItem(owner, builder))
    }

    private fun CollectionItemScopeStorage.getOrCreateItem(
        owner: DivBase,
        builder: DivCollectionItemBuilder,
        parent: DivLocalComponent = this@CollectionItemScopeStorageTest.parent,
    ): CollectionItem = getOrCreateItem(
        owner = owner,
        parent = parent,
        item = SelectedCollectionItem(
            prototype = builder.prototypes.single(),
            id = null,
            elementKey = "{}:0",
            constants = emptyMap(),
        ),
        duplicateIndex = null,
        viewContext = viewContext,
    )

    class Case(
        private val name: String,
        val owner: (DivCollectionItemBuilder) -> Div,
        val nested: (DivCollectionItemBuilder) -> Div,
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
                nested = { gallery(itemBuilder = it) },
            ),
            Case(
                name = "gallery",
                owner = { gallery(itemBuilder = it) },
                nested = { pager(itemBuilder = it) },
            ),
            Case(
                name = "pager",
                owner = { pager(itemBuilder = it) },
                nested = { container(itemBuilder = it) },
            ),
        )

        private fun leafBuilder(): DivCollectionItemBuilder = ownerBuilder(text(text = "item"))

        private fun ownerBuilder(div: Div): DivCollectionItemBuilder = itemBuilder(
            data = jsonArray(jsonObject()),
            prototypes = listOf(prototype(div = div)),
        )
    }
}
