package com.yandex.div.core.type

internal object UrlParser {

    fun parse(input: String): String {
        val schemeSeparator = input.indexOfAny(charArrayOf(':', '/', '?', '#'))
        if (schemeSeparator < 0 || input[schemeSeparator] != ':') {
            fail(input, "Expected scheme name", 0)
        }
        checkScheme(input, schemeSeparator)
        val requiresHost = input.substring(0, schemeSeparator).lowercase() in SCHEMES_WITH_HOST
        var index = schemeSeparator + 1

        if (input.startsWith("//", index)) {
            val authorityEnd = input.indexOfAny(charArrayOf('/', '?', '#'), index + 2).orEndOf(input)
            checkAuthority(input, index + 2, authorityEnd, requiresHost)
            index = authorityEnd
        } else if (requiresHost) {
            fail(input, "Expected authority", index)
        }

        val pathEnd = input.indexOfAny(charArrayOf('?', '#'), index).orEndOf(input)
        checkComponent(input, index, pathEnd, "path") { isPathChar(it) || it == '/' }
        index = pathEnd

        if (index < input.length && input[index] == '?') {
            val queryEnd = input.indexOf('#', index + 1).orEndOf(input)
            checkComponent(input, index + 1, queryEnd, "query", ::isQueryChar)
            index = queryEnd
        }

        if (index < input.length) {
            checkComponent(input, index + 1, input.length, "fragment", ::isQueryChar)
        }

        return input
    }

    private fun checkScheme(input: String, end: Int) {
        if (end == 0) {
            fail(input, "Expected scheme name", 0)
        }
        if (!input[0].isAsciiLetter()) {
            fail(input, "Illegal character in scheme name", 0)
        }
        for (i in 1 until end) {
            if (!isSchemeChar(input[i])) {
                fail(input, "Illegal character in scheme name", i)
            }
        }
    }

    private fun checkAuthority(input: String, start: Int, end: Int, requiresHost: Boolean) {
        val hostStart = checkUserInfo(input, start, end)
        val portSeparator = if (hostStart < end && input[hostStart] == '[') {
            checkIpLiteral(input, hostStart, end)
        } else {
            checkHostname(input, hostStart, end)
        }
        if (requiresHost && portSeparator == hostStart) {
            fail(input, "Expected host", hostStart)
        }
        checkPort(input, portSeparator + 1, end)
    }

    private fun checkPort(input: String, start: Int, end: Int) {
        for (i in start until end) {
            if (!input[i].isAsciiDigit()) {
                fail(input, "Illegal character in port number", i)
            }
        }
        if (start < end && (input.substring(start, end).toIntOrNull() ?: Int.MAX_VALUE) > MAX_PORT) {
            fail(input, "Port number is out of range", start)
        }
    }

    /**
     * @return start index of the host.
     */
    private fun checkUserInfo(input: String, start: Int, end: Int): Int {
        val userInfoSeparator = input.lastIndexOf('@', end - 1)
        if (userInfoSeparator < start) {
            return start
        }
        checkComponent(input, start, userInfoSeparator, "user info") {
            isUnreserved(it) || isSubDelim(it) || it == ':'
        }
        return userInfoSeparator + 1
    }

    /**
     * @return index of the port separator or [end] if there is no port.
     */
    private fun checkHostname(input: String, start: Int, end: Int): Int {
        val portSeparator = input.indexOf(':', start).let {
            if (it !in 0..end) end else it
        }
        checkComponent(input, start, portSeparator, "hostname") { isUnreserved(it) || isSubDelim(it) }
        return portSeparator
    }

    /**
     * @return index of the port separator or [end] if there is no port.
     */
    private fun checkIpLiteral(input: String, start: Int, end: Int): Int {
        val ipLiteralEnd = input.indexOf(']', start)
        if (ipLiteralEnd !in 0..<end) {
            fail(input, "Expected closing bracket for IP literal", end)
        }
        if (!IpLiteral.isValid(input.substring(start + 1, ipLiteralEnd))) {
            fail(input, "Malformed IP literal", start + 1)
        }
        val portSeparator = ipLiteralEnd + 1
        if (portSeparator < end && input[portSeparator] != ':') {
            fail(input, "Illegal character in authority", portSeparator)
        }
        return portSeparator
    }

    private inline fun checkComponent(
        input: String,
        start: Int,
        end: Int,
        component: String,
        isAllowed: (Char) -> Boolean
    ) {
        var i = start
        while (i < end) {
            val c = input[i]
            when {
                c == '%' -> {
                    if (i + 2 >= end || !input[i + 1].isHexDigit() || !input[i + 2].isHexDigit()) {
                        fail(input, "Malformed escape pair", i)
                    }
                    i += ESCAPE_LENGTH
                }
                isAllowed(c) || isOther(c) -> i++
                else -> fail(input, "Illegal character in $component", i)
            }
        }
    }

    private fun fail(input: String, reason: String, index: Int): Nothing {
        throw IllegalArgumentException("$reason at index $index: $input")
    }

    private fun Int.orEndOf(value: String): Int = if (this < 0) value.length else this
}

private object IpLiteral {

    private const val IPV6_GROUP_COUNT = 8
    private const val IPV6_GROUP_MAX_LENGTH = 4
    private const val IPV4_GROUP_COUNT = 2
    private const val IPV4_OCTET_COUNT = 4
    private const val IPV4_OCTET_MAX_LENGTH = 3
    private const val IPV4_OCTET_MAX_VALUE = 255

    fun isValid(address: String): Boolean {
        return if (address.startsWith('v') || address.startsWith('V')) {
            isIpvFuture(address)
        } else {
            isIpv6Address(address)
        }
    }

    private fun isIpvFuture(address: String): Boolean {
        val dot = address.indexOf('.')
        if (dot < 2 || dot == address.lastIndex) {
            return false
        }
        val version = address.substring(1, dot)
        val value = address.substring(dot + 1)
        return version.all { it.isHexDigit() } && value.all { isUnreserved(it) || isSubDelim(it) || it == ':' }
    }

    private fun isIpv6Address(address: String): Boolean {
        val compression = address.indexOf("::")
        if (compression < 0) {
            return countGroups(address) == IPV6_GROUP_COUNT
        }
        if (address.indexOf("::", compression + 1) >= 0) {
            return false
        }
        val head = address.substring(0, compression)
        val tail = address.substring(compression + 2)
        val headGroups = if (head.isEmpty()) 0 else countGroups(head, allowIpv4 = false)
        val tailGroups = if (tail.isEmpty()) 0 else countGroups(tail)
        return headGroups >= 0 && tailGroups >= 0 && headGroups + tailGroups < IPV6_GROUP_COUNT
    }

    /**
     * @return number of 16-bit groups in colon separated [part] or -1 if the part is malformed.
     * Trailing IPv4 address is counted as two groups.
     */
    private fun countGroups(part: String, allowIpv4: Boolean = true): Int {
        val pieces = part.split(':')
        var groups = 0
        pieces.forEachIndexed { index, piece ->
            groups += when {
                allowIpv4 && index == pieces.lastIndex && piece.contains('.') -> {
                    if (isIpv4Address(piece)) IPV4_GROUP_COUNT else return -1
                }
                piece.length in 1..IPV6_GROUP_MAX_LENGTH && piece.all { it.isHexDigit() } -> 1
                else -> return -1
            }
        }
        return groups
    }

    private fun isIpv4Address(address: String): Boolean {
        val octets = address.split('.')
        return octets.size == IPV4_OCTET_COUNT && octets.all { octet ->
            octet.length in 1..IPV4_OCTET_MAX_LENGTH &&
                octet.all { it.isAsciiDigit() } &&
                (octet.length == 1 || octet[0] != '0') &&
                octet.toInt() <= IPV4_OCTET_MAX_VALUE
        }
    }
}

private const val ESCAPE_LENGTH = 3
private const val MAX_ASCII_CODE = 0x7F
private const val MAX_PORT = 65535

private val SCHEMES_WITH_HOST = setOf("http", "https", "ws", "wss", "ftp")

private fun isSchemeChar(c: Char): Boolean = c.isAsciiLetter() || c.isAsciiDigit() || c == '+' || c == '-' || c == '.'

private fun isPathChar(c: Char): Boolean = isUnreserved(c) || isSubDelim(c) || c == ':' || c == '@'

private fun isQueryChar(c: Char): Boolean = isPathChar(c) || c == '/' || c == '?'

private fun isUnreserved(c: Char): Boolean {
    return c.isAsciiLetter() || c.isAsciiDigit() || c == '-' || c == '.' || c == '_' || c == '~'
}

private fun isSubDelim(c: Char): Boolean {
    return when (c) {
        '!', '$', '&', '\'', '(', ')', '*', '+', ',', ';', '=' -> true
        else -> false
    }
}

private fun isOther(c: Char): Boolean = c.code > MAX_ASCII_CODE && !c.isISOControl() && !c.isWhitespace()

private fun Char.isAsciiLetter(): Boolean = this in 'a'..'z' || this in 'A'..'Z'

private fun Char.isAsciiDigit(): Boolean = this in '0'..'9'

private fun Char.isHexDigit(): Boolean = isAsciiDigit() || this in 'a'..'f' || this in 'A'..'F'
