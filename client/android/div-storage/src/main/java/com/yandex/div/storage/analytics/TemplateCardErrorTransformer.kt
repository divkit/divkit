package com.yandex.div.storage.analytics

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.internal.KAssert
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.ParsingExceptionReason
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div.storage.util.CardErrorTransformer

/**
 * Consumes some of template related errors to fill them with actual reason and sends back to logger.
 */
@Mockable
internal class TemplateCardErrorTransformer(
        private val templateContainer: TemplatesContainer,
        private val internalLogger: ParsingErrorLogger,
) : CardErrorTransformer {

    override fun tryTransformAndLog(cardError: CardErrorTransformer.CardDetailedErrorException): Boolean {
        val missingTemplate = extractMissingTemplateException(cardError) ?: return false

        val templateName = missingTemplate.templateName ?: run {
            KAssert.fail { "Failed to parse template name from '${missingTemplate.message}'" }
            return false
        }

        val cardId = cardError.cardId
        val groupId = cardError.groupId
        val explanation = templateContainer.explainMissingTemplate(
                cardId, groupId, templateName
        )
        val details = explanation.getAllDetails()

        val errorMessage = "missing template = $templateName, reason = ${explanation.shortReason}"
        internalLogger.logError(
                CardErrorTransformer.CardDetailedErrorException(
                        cardId,
                        errorMessage,
                        cardError,
                        cardId,
                        details,
                        cardError.groupId,
                        cardError.metadata,
                )
        )

        return true
    }

    companion object {
        @VisibleForTesting
        internal fun extractMissingTemplateException(cardError: Throwable): ParsingException? {
            if (cardError !is ParsingException) {
                val cause = cardError.cause ?: return null
                return extractMissingTemplateException(cause)
            }

            val reason = cardError.reason
            if (reason == ParsingExceptionReason.MISSING_TEMPLATE) {
                return cardError
            }

            val cause = cardError.cause ?: return null
            return extractMissingTemplateException(cause)
        }
    }
}

private val ParsingException.templateName: String?
    get() {
        val m = this.message ?: return null
        return m.replace("Template '", "").replace("' is missing!", "")
    }
