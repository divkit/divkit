package com.yandex.div.storage.templates

internal sealed interface TemplatesPayloadForStorage {
    class Filled(
            val source: String,
            val extendExistingTemplates: Boolean,
            val templates: Map<String, ByteArray>,
    ): TemplatesPayloadForStorage

    /**
     * Indicates that source from which templates were assembled not contained any templates.
     */
    data object Empty : TemplatesPayloadForStorage

    /**
     * Variation of [Empty] payloads that is empty because of issues in cards.
     */
    data object AllCardsInvalid : TemplatesPayloadForStorage
}
