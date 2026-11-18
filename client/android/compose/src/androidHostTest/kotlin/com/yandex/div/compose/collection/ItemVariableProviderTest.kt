package com.yandex.div.compose.collection

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.rootVariableProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ItemVariableProviderTest {
    private val controller = DivVariableController()
    private val provider = ItemVariableProvider(
        rootVariableProvider(controller),
        mapOf("first" to 1L, "second" to 2L),
    )

    @Test
    fun `constants shadow controller variables with the same name`() {
        controller.declare(Variable.IntegerVariable("first", 10L))

        assertEquals(1L, provider.get("first"))
    }

    @Test
    fun `subscription is notified only when a value differs from the one it last saw`() {
        // `first` starts at 1; rows update it before and after subscribing, then notify once.
        listOf(
            Row("update before subscribing", before = listOf(3L), after = emptyList()),
            Row("revert after subscribing", before = listOf(3L), after = listOf(1L), notified = 1L),
            Row("update reverted before notifying", before = emptyList(), after = listOf(3L, 1L)),
        ).forEach { row ->
            val item = ItemVariableProvider(rootVariableProvider(), mapOf("first" to 1L))
            row.before.forEach { item.update(mapOf("first" to it)) }
            val updates = mutableListOf<Any?>()
            item.subscribe(listOf("first")) { updates.add(item.get("first")) }
            row.after.forEach { item.update(mapOf("first" to it)) }

            item.notifyChanges()

            assertEquals<List<Any?>>(listOfNotNull(row.notified), updates, row.name)
        }
    }

    @Test
    fun `updates are readable at once and notify once with the final values`() {
        val updates = mutableListOf<Any?>()
        provider.subscribe(listOf("first")) { updates.add(provider.get("first")) }

        provider.update(mapOf("first" to 3L, "second" to 2L))

        assertEquals(3L, provider.get("first"))
        assertEquals(emptyList(), updates)

        provider.update(mapOf("first" to 4L, "second" to 2L))
        provider.notifyChanges()
        provider.notifyChanges()

        assertEquals(listOf<Any?>(4L), updates)
    }

    @Test
    fun `observers see a single complete update of item values`() {
        val updates = mutableListOf<Pair<Any?, Any?>>()
        provider.subscribe(listOf("first", "second")) {
            updates.add(provider.get("first") to provider.get("second"))
        }

        provider.update(mapOf("first" to 3L, "second" to 4L))
        provider.notifyChanges()

        assertEquals(listOf<Pair<Any?, Any?>>(3L to 4L), updates)
    }

    @Test
    fun `closed subscription ignores subsequent updates`() {
        val updates = mutableListOf<Any?>()
        val subscription = checkNotNull(
            provider.subscribe(listOf("first")) { updates.add(provider.get("first")) }
        )
        subscription.close()

        provider.update(mapOf("first" to 3L, "second" to 4L))
        provider.notifyChanges()

        assertEquals(emptyList(), updates)
    }

    @Test
    fun `shadowed parent values do not notify child subscriptions`() {
        val child = ItemVariableProvider(provider, mapOf("first" to 10L))
        val updates = mutableListOf<Any?>()
        child.subscribe(listOf("first")) { updates.add(child.get("first")) }

        provider.update(mapOf("first" to 3L, "second" to 2L))
        provider.notifyChanges()

        assertEquals(emptyList(), updates)
    }
}

private class Row(
    val name: String,
    val before: List<Long>,
    val after: List<Long>,
    val notified: Long? = null,
)
