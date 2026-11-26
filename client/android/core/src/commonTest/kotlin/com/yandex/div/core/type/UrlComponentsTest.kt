package com.yandex.div.core.type

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class UrlComponentsTest {

    @Test
    fun `scheme is part before first colon`() {
        val url = Url.create("div-action://set_state?state_id=0")

        assertEquals("div-action", url.scheme)
    }

    @Test
    fun `authority ends before query`() {
        val url = Url.create("div-action://set_state?state_id=0")

        assertEquals("set_state", url.authority)
    }

    @Test
    fun `authority ends before path`() {
        val url = Url.create("https://divkit.tech/docs")

        assertEquals("divkit.tech", url.authority)
    }

    @Test
    fun `authority ends before fragment`() {
        val url = Url.create("https://divkit.tech#intro")

        assertEquals("divkit.tech", url.authority)
    }

    @Test
    fun `authority is percent decoded`() {
        val url = Url.create("custom://set%20state")

        assertEquals("set state", url.authority)
    }

    @Test
    fun `authority is null without double slash`() {
        val url = Url.create("mailto:user@divkit.tech")

        assertNull(url.authority)
    }

    @Test
    fun `query parameter returns value for matching name`() {
        val url = Url.create("div-action://set_variable?name=counter&value=1&value=2")

        assertEquals("counter", url.getQueryParameter("name"))
    }

    @Test
    fun `query parameter returns first value when name is repeated`() {
        val url = Url.create("div-action://set_variable?name=counter&value=1&value=2")

        assertEquals("1", url.getQueryParameter("value"))
    }

    @Test
    fun `query parameter is null when name is missing`() {
        val url = Url.create("div-action://set_variable?name=counter")

        assertNull(url.getQueryParameter("value"))
    }

    @Test
    fun `query parameter is null when query is missing`() {
        val url = Url.create("div-action://set_variable")

        assertNull(url.getQueryParameter("name"))
    }

    @Test
    fun `query parameter is empty when equals sign is missing`() {
        val url = Url.create("div-action://set_state?temporary&state_id=")

        assertEquals("", url.getQueryParameter("temporary"))
    }

    @Test
    fun `query parameter is empty when value after equals sign is empty`() {
        val url = Url.create("div-action://set_state?temporary&state_id=")

        assertEquals("", url.getQueryParameter("state_id"))
    }

    @Test
    fun `query parameter value is percent decoded`() {
        val url = Url.create("div-action://set_variable?value=a%26b%3Dc%20d")

        assertEquals("a&b=c d", url.getQueryParameter("value"))
    }

    @Test
    fun `query parameter decodes plus as space`() {
        val url = Url.create("div-action://set_variable?value=hello+world")

        assertEquals("hello world", url.getQueryParameter("value"))
    }

    @Test
    fun `query parameter decodes utf-8 escapes`() {
        val url = Url.create("div-action://set_variable?value=%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82")

        assertEquals("привет", url.getQueryParameter("value"))
    }

    @Test
    fun `query parameter name is percent encoded before matching`() {
        val url = Url.create("div-action://set_variable?state%20id=1")

        assertEquals("1", url.getQueryParameter("state id"))
    }

    @Test
    fun `query parameter name with a space does not match a raw plus`() {
        val url = Url.create("div-action://set_variable?a+b=1")

        assertNull(url.getQueryParameter("a b"))
    }

    @Test
    fun `query parameter name does not match an escaped underscore`() {
        val url = Url.create("div-action://set_variable?state%5Fid=1")

        assertNull(url.getQueryParameter("state_id"))
    }

    @Test
    fun `query parameter name does not match a raw comma`() {
        val url = Url.create("div-action://set_variable?a,b=1")

        assertNull(url.getQueryParameter("a,b"))
    }

    @Test
    fun `query parameter name does not match a lowercase percent escape`() {
        val url = Url.create("div-action://set_variable?a%2cb=1")

        assertNull(url.getQueryParameter("a,b"))
    }

    @Test
    fun `query parameter name matches an escaped comma`() {
        val url = Url.create("div-action://set_variable?a,b=1&a%2Cb=2")

        assertEquals("2", url.getQueryParameter("a,b"))
    }

    @Test
    fun `query parameter name matches an escaped plus`() {
        val url = Url.create("div-action://set_variable?a%2Bb=1")

        assertEquals("1", url.getQueryParameter("a+b"))
    }

    @Test
    fun `query parameter name matches utf-8 escapes`() {
        val url = Url.create("div-action://set_variable?%D1%8F=1")

        assertEquals("1", url.getQueryParameter("я"))
    }

    @Test
    fun `query parameter name matches allowed punctuation without escapes`() {
        val url = Url.create("div-action://set_variable?-_.!~'()*=1")

        assertEquals("1", url.getQueryParameter("-_.!~'()*"))
    }

    @Test
    fun `query ends before fragment`() {
        val url = Url.create("https://divkit.tech/docs?lang=en#section?lang=ru")

        assertEquals("en", url.getQueryParameter("lang"))
    }

    @Test
    fun `question mark in fragment does not start query`() {
        val url = Url.create("https://divkit.tech/docs#section?lang=ru")

        assertNull(url.getQueryParameter("lang"))
    }

    @Test
    fun `boolean query parameter is false for lowercase false`() {
        val url = Url.create("div-action://show_tooltip?a=false&b=FALSE&c=0")

        assertFalse(url.getBooleanQueryParameter("a", defaultValue = true))
    }

    @Test
    fun `boolean query parameter is false for uppercase false`() {
        val url = Url.create("div-action://show_tooltip?a=false&b=FALSE&c=0")

        assertFalse(url.getBooleanQueryParameter("b", defaultValue = true))
    }

    @Test
    fun `boolean query parameter is false for zero`() {
        val url = Url.create("div-action://show_tooltip?a=false&b=FALSE&c=0")

        assertFalse(url.getBooleanQueryParameter("c", defaultValue = true))
    }

    @Test
    fun `boolean query parameter is true for true`() {
        val url = Url.create("div-action://show_tooltip?a=true&b=1&c=&d=yes")

        assertTrue(url.getBooleanQueryParameter("a", defaultValue = false))
    }

    @Test
    fun `boolean query parameter is true for one`() {
        val url = Url.create("div-action://show_tooltip?a=true&b=1&c=&d=yes")

        assertTrue(url.getBooleanQueryParameter("b", defaultValue = false))
    }

    @Test
    fun `boolean query parameter is true for empty value`() {
        val url = Url.create("div-action://show_tooltip?a=true&b=1&c=&d=yes")

        assertTrue(url.getBooleanQueryParameter("c", defaultValue = false))
    }

    @Test
    fun `boolean query parameter is true for other nonempty values`() {
        val url = Url.create("div-action://show_tooltip?a=true&b=1&c=&d=yes")

        assertTrue(url.getBooleanQueryParameter("d", defaultValue = false))
    }

    @Test
    fun `boolean query parameter is true when missing and default is true`() {
        val url = Url.create("div-action://show_tooltip?id=tooltip")

        assertTrue(url.getBooleanQueryParameter("multiple", defaultValue = true))
    }

    @Test
    fun `boolean query parameter is false when missing and default is false`() {
        val url = Url.create("div-action://show_tooltip?id=tooltip")

        assertFalse(url.getBooleanQueryParameter("multiple", defaultValue = false))
    }
}
