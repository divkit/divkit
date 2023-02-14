package com.yandex.div.storage.analytics

import com.yandex.div.core.annotations.Mockable
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.templates.TemplatesContainer
import com.yandex.div.storage.util.CardErrorTransformer
import com.yandex.div.storage.util.LazyProvider
import org.json.JSONObject
import javax.inject.Provider

@Mockable
internal class CardErrorLoggerFactory(
        externalErrorTransformer: Provider<out CardErrorTransformer>?,
        private val templateContainer: TemplatesContainer,
        private val parsingErrorLogger: ParsingErrorLogger,
) {
    private val errorTransformer: Provider<CardErrorTransformer> = LazyProvider {
        if (externalErrorTransformer == null) {
            TemplateCardErrorTransformer(templateContainer, parsingErrorLogger)
        } else {
            CardErrorTransformer.Composite(
                    externalErrorTransformer.get(),
                    TemplateCardErrorTransformer(templateContainer, parsingErrorLogger),
            )
        }
    }

    fun createContextualLogger(origin: ParsingErrorLogger,
                               cardId: String,
                               groupId: String,
                               metadata: JSONObject?): ParsingErrorLogger {
        return object : ParsingErrorLogger {
            override fun logError(e: Exception) {
                val detailedErrorException = CardErrorTransformer.CardDetailedErrorException(
                        message = e.message,
                        cause = e,
                        cardId = cardId,
                        groupId = groupId,
                        metadata = metadata,
                )

                if (errorTransformer.get().tryTransformAndLog(detailedErrorException)) {
                    return
                }

                origin.logError(detailedErrorException)
            }

            override fun logTemplateError(e: Exception, templateId: String) {
                val detailedErrorException = CardErrorTransformer.CardDetailedErrorException(
                        message = e.message,
                        cause = e,
                        cardId = cardId,
                        groupId = groupId,
                        templateId = templateId,
                        metadata = metadata,
                )

                if (errorTransformer.get().tryTransformAndLog(detailedErrorException)) {
                    return
                }

                origin.logTemplateError(detailedErrorException, templateId)
            }
        }
    }
}
