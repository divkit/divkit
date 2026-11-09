package com.yandex.div.compose.state

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.test.data.action
import com.yandex.div.test.data.state
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivStateStorageTest {
    private val storage = DivStateStorage()
    private val rootPath = DivStatePath.fromState(0L)

    @Test
    fun `setActiveState invokes a bound variable setter`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }

        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals(listOf("main"), received)
    }

    @Test
    fun `setActiveState does not touch unbound paths`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }

        storage.setActiveState(DivStatePath.parse("0/footer/compact"))

        assertEquals(emptyList(), received)
    }

    @Test
    fun `unbindStateIdVariable stops sync`() {
        val received = mutableListOf<String>()
        storage.bindStateIdVariable(rootPath, "menu") { received.add(it) }
        storage.unbindStateIdVariable(rootPath, "menu")

        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals(emptyList(), received)
    }

    @Test
    fun `getActiveState returns the stored state id`() {
        storage.setActiveState(DivStatePath.parse("0/menu/main"))

        assertEquals("main", storage.getActiveState(rootPath, "menu"))
    }

    @Test
    fun `swipe dismissal is isolated for different composition keys`() {
        val state = state(
            stateId = "main",
            swipeOutActions = listOf(action(url = "divkit-test://swipe")),
        )
        val first = storage.getSwipeOutState(1L, state)
        first?.value = true

        val second = storage.getSwipeOutState(2L, state)

        assertEquals(false, second?.value)
    }

    @Test
    fun `clear resets swipe dismissal`() {
        val state = state(
            stateId = "main",
            swipeOutActions = listOf(action(url = "divkit-test://swipe")),
        )
        storage.getSwipeOutState(1L, state)?.value = true

        storage.clear()

        assertEquals(false, storage.getSwipeOutState(1L, state)?.value)
    }

    @Test
    fun `clear wipes states and bindings`() {
        storage.setActiveState(DivStatePath.parse("0/menu/main"))
        storage.bindStateIdVariable(rootPath, "menu") { /* */ }

        storage.clear()

        assertNull(storage.getActiveState(rootPath, "menu"))
        storage.setActiveState(DivStatePath.parse("0/menu/details"))
        assertEquals("details", storage.getActiveState(rootPath, "menu"))
    }
}
