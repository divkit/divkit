package com.yandex.div.evaluable.function

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UriCodecTest {

    @Test
    fun `encode keeps unreserved characters`() {
        assertEquals("AZaz09-_.!~*'()", UriCodec.encode("AZaz09-_.!~*'()"))
    }

    @Test
    fun `encode escapes reserved characters`() {
        assertEquals("%3B%2C%2F%3F%3A%40%26%3D%2B%24%23%25", UriCodec.encode(";,/?:@&=+$#%"))
    }

    @Test
    fun `encode escapes space instead of replacing it with plus`() {
        assertEquals("a%20b", UriCodec.encode("a b"))
    }

    @Test
    fun `encode escapes UTF-8 bytes of non-ASCII characters`() {
        assertEquals("%D0%B6%F0%9F%98%80", UriCodec.encode("ж😀"))
    }

    @Test
    fun `decode replaces escapes between unescaped characters`() {
        assertEquals("a b", UriCodec.decode("a%20b"))
    }

    @Test
    fun `decode accepts lowercase hex digits`() {
        assertEquals("/", UriCodec.decode("%2f"))
    }

    @Test
    fun `decode joins consecutive escapes into one UTF-8 character`() {
        assertEquals("ж", UriCodec.decode("%D0%B6"))
    }

    @Test
    fun `decode replaces plus with space`() {
        assertEquals("a b", UriCodec.decode("a+b"))
    }

    @Test
    fun `decode replaces malformed UTF-8 bytes with replacement character`() {
        assertEquals("\uFFFD", UriCodec.decode("%FF"))
    }

    @Test
    fun `decode accepts plus sign in escape`() {
        assertEquals("\r", UriCodec.decode("%+D"))
    }

    @Test
    fun `decode fails when trailing escape is incomplete`() {
        val exception = assertFailsWith<IllegalArgumentException> { UriCodec.decode("a%4") }

        assertEquals("URLDecoder: Incomplete trailing escape (%) pattern", exception.message)
    }

    @Test
    fun `decode fails when escape contains non-hex characters`() {
        val exception = assertFailsWith<IllegalArgumentException> { UriCodec.decode("%zz") }

        assertEquals(
            "URLDecoder: Illegal hex characters in escape (%) pattern - For input string: \"zz\"",
            exception.message
        )
    }

    @Test
    fun `decode fails when escape is negative`() {
        val exception = assertFailsWith<IllegalArgumentException> { UriCodec.decode("%-1") }

        assertEquals("URLDecoder: Illegal hex characters in escape (%) pattern - negative value", exception.message)
    }
}
