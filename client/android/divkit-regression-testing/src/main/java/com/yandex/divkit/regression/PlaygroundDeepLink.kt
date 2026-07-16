package com.yandex.divkit.regression

import android.net.Uri

/**
 * Shared `playground://` deep-link contract (kept in sync with iOS `AppRouter`).
 *
 * - `playground://test?id=` / `?title=` — open a regression scenario
 * - `playground://json?url=` — open custom Div JSON from a URL
 */
object PlaygroundDeepLink {
    private const val SCHEME = "playground"
    private const val HOST_TEST = "test"
    private const val HOST_JSON = "json"
    private const val PARAM_ID = "id"
    private const val PARAM_TITLE = "title"
    private const val PARAM_URL = "url"

    sealed class TestQuery {
        data class ByCaseId(val caseId: Int) : TestQuery()
        data class ByTitle(val title: String) : TestQuery()
    }

    fun parseTest(uri: Uri): TestQuery? {
        if (uri.scheme != SCHEME || uri.host != HOST_TEST) {
            return null
        }

        val idValue = uri.getQueryParameter(PARAM_ID)
        if (idValue != null) {
            val caseId = idValue.toIntOrNull() ?: return null
            return TestQuery.ByCaseId(caseId)
        }

        val title = uri.getQueryParameter(PARAM_TITLE) ?: return null
        return TestQuery.ByTitle(title)
    }

    /**
     * Parses `playground://json?url=<encoded-json-url>`.
     * Returns the decoded JSON URL string, or null if the URI is not a valid json deep link.
     */
    fun parseJson(uri: Uri): String? {
        if (uri.scheme != SCHEME || uri.host != HOST_JSON) {
            return null
        }
        return uri.getQueryParameter(PARAM_URL)?.takeIf { it.isNotBlank() }
    }
}
