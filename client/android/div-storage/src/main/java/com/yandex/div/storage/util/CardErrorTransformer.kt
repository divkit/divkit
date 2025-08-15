package com.yandex.div.storage.util

import org.json.JSONObject

/**
 * Used to prevent sending of some error messages in order to transform,
 * saturate and send them later with extended details.
 */
interface CardErrorTransformer {
    fun tryTransformAndLog(cardError: CardDetailedErrorException): Boolean

    class CardDetailedErrorException(
            val cardId: String,
            message: String?,
            cause: Throwable? = null,
            val templateId: String? = null,
            val details: Map<String, String> = emptyMap(),
            internal val groupId: String,
            val metadata: JSONObject?,
    ): Exception(message, cause)

    class Composite(private vararg val transformers: CardErrorTransformer) : CardErrorTransformer {
        override fun tryTransformAndLog(cardError: CardDetailedErrorException): Boolean {
            transformers.forEach {
                if (it.tryTransformAndLog(cardError)) {
                    return true
                }
            }

            return false
        }
    }
}
