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
    object Empty : TemplatesPayloadForStorage

    /**
     * Variation of [Empty] payloads that is empty because of issues in cards.
     */
    object AllCardsInvalid : TemplatesPayloadForStorage
}
