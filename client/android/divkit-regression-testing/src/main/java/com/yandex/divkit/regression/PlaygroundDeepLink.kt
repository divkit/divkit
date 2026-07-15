package com.yandex.divkit.regression

import android.net.Uri

object PlaygroundDeepLink {
    private const val SCHEME = "playground"
    private const val HOST_TEST = "test"
    private const val PARAM_ID = "id"
    private const val PARAM_TITLE = "title"

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
}
