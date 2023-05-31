package com.yandex.div.dsl.serializer

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonRawValue
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.yandex.div.dsl.context.CardContext
import com.yandex.div.dsl.context.CardWithTemplates
import com.yandex.div.dsl.context.TemplateContext
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

private val objectMapper = ObjectMapper()

fun TemplateContext<*>.toJsonString(): String {
    if (this.asString == null) {
        this.asString = objectMapper.writeValueAsString(this)
    }
    return this.asString!!
}

@Throws(IOException::class)
fun TemplateContext<*>.toJsonFile(path: String): File {
    return File(path).also { file ->
        if (this.asString == null) {
            this.asString = objectMapper.writeValueAsString(this)
        }
        file.writeText(this.asString!!, StandardCharsets.UTF_8)
    }
}

fun TemplateContext<*>.toJsonNode(): ObjectNode {
    if (this.asNode == null) {
        this.asNode = objectMapper.valueToTree(this)
    }
    return this.asNode!!
}

fun CardContext.toJsonString(): String {
    if (this.asString == null) {
        this.asString = objectMapper.writeValueAsString(this)
    }
    return this.asString!!
}

@Throws(IOException::class)
fun CardContext.toJsonFile(path: String): File {
    return File(path).also { file ->
        if (this.asString == null) {
            this.asString = objectMapper.writeValueAsString(this)
        }
        file.writeText(this.asString!!, StandardCharsets.UTF_8)

    }
}

fun CardContext.toJsonNode(): ObjectNode {
    if (this.asNode == null) {
        this.asNode = objectMapper.valueToTree(this)
    }
    return this.asNode!!
}

@JsonInclude(JsonInclude.Include.NON_NULL)
internal class CardWithTemplatesAsStrings(@JsonRawValue val card: String, @JsonRawValue val templates: String? = null)

fun CardWithTemplates.toJsonString(): String {
    return objectMapper.writeValueAsString(
        CardWithTemplatesAsStrings(
            this.card.toJsonString(),
            this.templates?.toJsonString()
        )
    )
}

@Throws(IOException::class)
fun CardWithTemplates.toJsonFile(path: String): File {
    return File(path).also { file ->
        objectMapper.writeValue(file, this)
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
internal class CardWithTemplatesAsNodes(val card: ObjectNode, val templates: ObjectNode? = null)

fun CardWithTemplates.toJsonNode(): ObjectNode {
    return objectMapper.valueToTree(CardWithTemplatesAsNodes(this.card.toJsonNode(), this.templates?.toJsonNode()))
}
