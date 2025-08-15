package com.yandex.div.storage.analytics

/**
 * Entity that explains why concrete card template wasn't found during parsing of DivData.
 */
internal class ErrorExplanation(
        val shortReason: String,
        private val details: String? = null,
) {
    fun getAllDetails(): Map<String, String> {
        val results = linkedMapOf<String, String>()
        details?.let {
            results["details"] = details
        }

        return results
    }

}
