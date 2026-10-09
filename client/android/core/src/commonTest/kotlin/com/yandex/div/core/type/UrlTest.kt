package com.yandex.div.core.type

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class UrlTest {

    @Test
    fun `create keeps valid url string`() {
        val urlString = "https://divkit.tech/docs?lang=en#intro"

        val url = Url.create(urlString)

        assertEquals("https://divkit.tech/docs?lang=en#intro", url.toString())
    }

    @Test
    fun `create accepts file url`() {
        val urlString = "file:///android_asset/image.png"

        val url = Url.create(urlString)

        assertEquals("file:///android_asset/image.png", url.toString())
    }

    @Test
    fun `create accepts custom scheme`() {
        val urlString = "div-action://set_state?state_id=0"

        val url = Url.create(urlString)

        assertEquals("div-action://set_state?state_id=0", url.toString())
    }

    @Test
    fun `create throws when scheme is missing`() {
        val urlString = "divkit.tech/docs"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when string is empty`() {
        val urlString = ""

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when string contains illegal character`() {
        val urlString = "https://divkit.tech/my docs"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when percent escape is malformed`() {
        val urlString = "https://divkit.tech/%zz"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when scheme is empty`() {
        val urlString = "://divkit.tech"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create accepts url without authority`() {
        val urlString = "mailto:info@divkit.tech"

        val url = Url.create(urlString)

        assertEquals("mailto:info@divkit.tech", url.toString())
    }

    @Test
    fun `create accepts scheme with allowed special characters`() {
        val urlString = "Android-App+x.y-z:payload"

        val url = Url.create(urlString)

        assertEquals("Android-App+x.y-z:payload", url.toString())
    }

    @Test
    fun `create accepts uppercase scheme requiring host`() {
        val urlString = "HTTPS://divkit.tech/docs"

        val url = Url.create(urlString)

        assertEquals("HTTPS://divkit.tech/docs", url.toString())
    }

    @Test
    fun `create accepts user info and port`() {
        val urlString = "https://user:pass@divkit.tech:8080/docs"

        val url = Url.create(urlString)

        assertEquals("https://user:pass@divkit.tech:8080/docs", url.toString())
    }

    @Test
    fun `create accepts empty port`() {
        val urlString = "http://divkit.tech:/"

        val url = Url.create(urlString)

        assertEquals("http://divkit.tech:/", url.toString())
    }

    @Test
    fun `create accepts maximum port`() {
        val urlString = "http://divkit.tech:65535/"

        val url = Url.create(urlString)

        assertEquals("http://divkit.tech:65535/", url.toString())
    }

    @Test
    fun `create accepts ipv6 address with port`() {
        val urlString = "http://[::1]:8080/"

        val url = Url.create(urlString)

        assertEquals("http://[::1]:8080/", url.toString())
    }

    @Test
    fun `create accepts full ipv6 address`() {
        val urlString = "http://[2001:db8:0:0:0:0:2:1]/"

        val url = Url.create(urlString)

        assertEquals("http://[2001:db8:0:0:0:0:2:1]/", url.toString())
    }

    @Test
    fun `create accepts ipv6 address with trailing compression`() {
        val urlString = "http://[1:2:3:4:5:6:7::]/"

        val url = Url.create(urlString)

        assertEquals("http://[1:2:3:4:5:6:7::]/", url.toString())
    }

    @Test
    fun `create accepts ipv6 address with embedded ipv4 address`() {
        val urlString = "http://[::ffff:192.168.0.1]/"

        val url = Url.create(urlString)

        assertEquals("http://[::ffff:192.168.0.1]/", url.toString())
    }

    @Test
    fun `create accepts future ip address`() {
        val urlString = "http://[v1.fe80::a+en1]/"

        val url = Url.create(urlString)

        assertEquals("http://[v1.fe80::a+en1]/", url.toString())
    }

    @Test
    fun `create accepts percent encoded characters`() {
        val urlString = "https://divkit.tech/my%20docs"

        val url = Url.create(urlString)

        assertEquals("https://divkit.tech/my%20docs", url.toString())
    }

    @Test
    fun `create accepts sub delimiters in path`() {
        val urlString = "https://divkit.tech/a;b=c,d!\$&'()*+"

        val url = Url.create(urlString)

        assertEquals("https://divkit.tech/a;b=c,d!\$&'()*+", url.toString())
    }

    @Test
    fun `create accepts slash and question mark in query`() {
        val urlString = "https://divkit.tech/?q=a/b?c"

        val url = Url.create(urlString)

        assertEquals("https://divkit.tech/?q=a/b?c", url.toString())
    }

    @Test
    fun `create accepts non ascii characters`() {
        val urlString = "https://яндекс.рф/путь?q=тест#раздел"

        val url = Url.create(urlString)

        assertEquals("https://яндекс.рф/путь?q=тест#раздел", url.toString())
    }

    @Test
    fun `create throws when url is network path reference`() {
        val urlString = "//divkit.tech/docs"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when scheme starts with digit`() {
        val urlString = "1http://divkit.tech"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when scheme contains illegal character`() {
        val urlString = "ht_tp://divkit.tech"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when scheme contains non ascii character`() {
        val urlString = "схема://divkit.tech"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when percent escape is truncated at end`() {
        val urlString = "https://divkit.tech/%2"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when percent escape is truncated before query`() {
        val urlString = "https://divkit.tech/%2?q"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when fragment contains hash`() {
        val urlString = "https://divkit.tech/#a#b"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when port is not a number`() {
        val urlString = "https://divkit.tech:80a/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when port is out of range`() {
        val urlString = "http://divkit.tech:65536/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when port overflows int`() {
        val urlString = "http://divkit.tech:99999999999999999999/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when host is missing for scheme requiring host`() {
        val urlString = "http://"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when host is missing before port`() {
        val urlString = "https://:8080/docs"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when authority is missing for scheme requiring host`() {
        val urlString = "http:divkit.tech"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when user info contains illegal character`() {
        val urlString = "https://us@er@divkit.tech/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ip literal is not closed`() {
        val urlString = "http://[::1/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ip literal is followed by illegal character`() {
        val urlString = "http://[::1]x/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 group is not hexadecimal`() {
        val urlString = "http://[zz::1]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 group is too long`() {
        val urlString = "http://[12345::1]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 address has too few groups`() {
        val urlString = "http://[1:2:3:4:5:6:7]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 address has too many groups`() {
        val urlString = "http://[1:2:3:4:5:6:7:8:9]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when compressed ipv6 address has eight groups`() {
        val urlString = "http://[1:2:3:4:5:6:7:8::]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 address is compressed twice`() {
        val urlString = "http://[1::2::3]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when ipv6 address starts with single colon`() {
        val urlString = "http://[:1:2:3:4:5:6:7]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when embedded ipv4 octet is out of range`() {
        val urlString = "http://[::ffff:256.0.0.1]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when embedded ipv4 octet has leading zero`() {
        val urlString = "http://[::ffff:01.0.0.1]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when future ip address has no version`() {
        val urlString = "http://[v.addr]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when host contains brackets outside of ip literal`() {
        val urlString = "http://a[b]/"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains brackets`() {
        val urlString = "https://divkit.tech/a[0]"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains unsafe ascii character`() {
        val urlString = "https://divkit.tech/a<b"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains backslash`() {
        val urlString = "https://divkit.tech/a\\b"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains ascii control character`() {
        val urlString = "https://divkit.tech/a\nb"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains non ascii control character`() {
        val urlString = "https://divkit.tech/a\u0080b"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }

    @Test
    fun `create throws when path contains non ascii whitespace`() {
        val urlString = "https://divkit.tech/a\u00A0b"

        assertFailsWith<IllegalArgumentException> { Url.create(urlString) }
    }
}
