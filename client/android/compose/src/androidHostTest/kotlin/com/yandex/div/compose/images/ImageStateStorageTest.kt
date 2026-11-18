package com.yandex.div.compose.images

import androidx.compose.runtime.SideEffect
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import coil3.request.ImageRequest
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import org.junit.Rule
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class ImageStateStorageTest {

    @get:Rule
    val rule = createComposeRule()

    private val data = (text(text = "Image") as Div.Text).value
    private val storage = ImageStateStorage()

    @Test
    fun `reset by stale owner keeps latest loaded state`() {
        val staleOwner = mock<ImageRequest>()
        val currentOwner = mock<ImageRequest>()
        storage.markLoaded(data, staleOwner)
        storage.markLoaded(data, currentOwner)

        storage.reset(data, staleOwner)

        assertTrue(storage.isLoaded(data))
        storage.reset(data, currentOwner)
        assertFalse(storage.isLoaded(data))
    }

    @Test
    fun `loaded state matches current owner`() {
        val staleOwner = mock<ImageRequest>()
        val currentOwner = mock<ImageRequest>()

        storage.markLoaded(data, staleOwner)
        assertTrue(storage.isLoaded(data, staleOwner))
        assertFalse(storage.isLoaded(data, currentOwner))

        storage.markLoaded(data, currentOwner)
        assertFalse(storage.isLoaded(data, staleOwner))
        assertTrue(storage.isLoaded(data, currentOwner))
    }

    @Test
    fun `loaded state is reactive`() {
        val owner = mock<ImageRequest>()
        var observedLoaded: Boolean? = null
        rule.setContent {
            val isLoaded = storage.isLoaded(data)
            SideEffect {
                observedLoaded = isLoaded
            }
        }

        rule.runOnIdle {
            assertFalse(observedLoaded!!)
            storage.markLoaded(data, owner)
        }
        rule.runOnIdle {
            assertTrue(observedLoaded!!)
            storage.reset(data, owner)
        }
        rule.runOnIdle {
            assertFalse(observedLoaded!!)
        }
    }
}
