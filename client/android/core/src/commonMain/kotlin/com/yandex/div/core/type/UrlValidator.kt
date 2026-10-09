package com.yandex.div.core.type

/**
 * Validates absolute URLs according to RFC 3986.
 *
 * URL is split into scheme, authority, path, query and fragment components which are validated separately.
 * Scheme is required. Schemes http, https, ws, wss and ftp also require an authority with non-empty host.
 * Port number, if present, must be in range 0..65535.
 * In addition to RFC 3986 characters, non-ASCII characters which are not control or whitespace characters
 * are allowed in all components except scheme, port and IP literal.
 */
internal object UrlValidator {

    fun validate(url: String): Result<Unit> {
        return try {
            UrlParser.parse(url)
            Result.success(Unit)
        } catch (e: IllegalArgumentException) {
            Result.failure(e)
        }
    }
}
