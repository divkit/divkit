package com.yandex.div.backdrop.model

import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BackdropEffectTest {

    @Test
    fun `scope defaults to card when the param is omitted`() {
        val effect = BackdropEffect.deserialize(JSONObject())

        assertEquals(BackdropScope.CARD, effect.scope)
    }

    @Test
    fun `scope is parsed from the backdrop_scope param`() {
        val effect = BackdropEffect.deserialize(JSONObject("""{"backdrop_scope": "window"}"""))

        assertEquals(BackdropScope.WINDOW, effect.scope)
    }

    @Test
    fun `card scope is parsed from the backdrop_scope param`() {
        val effect = BackdropEffect.deserialize(JSONObject("""{"backdrop_scope": "card"}"""))

        assertEquals(BackdropScope.CARD, effect.scope)
    }

    @Test
    fun `scope defaults to card when the param is null`() {
        val effect = BackdropEffect.deserialize(JSONObject("""{"backdrop_scope": null}"""))

        assertEquals(BackdropScope.CARD, effect.scope)
    }

    @Test(expected = JSONException::class)
    fun `unknown scope is rejected`() {
        BackdropEffect.deserialize(JSONObject("""{"backdrop_scope": "screen"}"""))
    }

    @Test
    fun `scope of a wrong type falls back to card`() {
        val effect = BackdropEffect.deserialize(JSONObject("""{"backdrop_scope": 1}"""))

        assertEquals(BackdropScope.CARD, effect.scope)
    }
}
