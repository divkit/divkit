package com.yandex.div.core

import android.net.Uri
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.disableAssertions
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivActionHandlerTest {

    private val divView = mock<DivViewFacade>()
    private val underTest = DivActionHandler()

    @Test
    fun `uri with numeric state is handled`() {
        val result = underTest.handleUri("div-action://set_state?state_id=1".uri, divView)

        verify(divView).switchToState("1".path, true)
        Assert.assertTrue(result)
    }

    @Test
    fun `uri with string state is handled`() {
        val result = underTest.handleUri("div-action://set_state?state_id=1/foo/bar&temporary=true".uri, divView)

        verify(divView).switchToState("1/foo/bar".path, true)
        Assert.assertTrue(result)
    }

    @Test
    fun `uri with escaped parameter is handled`() {
        val result = underTest.handleUri("div-action://set_state?state_id=1%2Ffoo%2Fbar%2Flol%2Fkek".uri, divView)

        verify(divView).switchToState("1/foo/bar/lol/kek".path, true)
        Assert.assertTrue(result)
    }

    @Test
    fun `uri without path is not handled`() {
        val result = underTest.handleUri("div-action://?state_id=1".uri, divView)

        verifyNoMoreInteractions(divView)
        Assert.assertFalse(result)
    }

    @Test
    fun `uri with invalid path is not handled`() {
        disableAssertions {
            Assert.assertFalse(underTest.handleUri("div-action://set_state?state_id=1.5/foo/bar/lol/kek".uri, divView))
            Assert.assertFalse(underTest.handleUri("div-action://set_state?state_id=1/foo/bar/lol".uri, divView))
        }
    }

    private val String.uri: Uri get() = Uri.parse(this)
    private val String.path: DivStatePath get() = DivStatePath.parse(this)
}
