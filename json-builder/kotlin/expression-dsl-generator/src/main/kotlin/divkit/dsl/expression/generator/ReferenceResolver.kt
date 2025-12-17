package divkit.dsl.expression.generator

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File


class ReferenceResolver(
    private val objectMapper: ObjectMapper = ObjectMapper()
) {

    inline fun <reified T> resolveReference(
        location: File,
        reference: String,
    ): T {
        return resolveReference(location, reference, T::class.java)
    }

    fun <T> resolveReference(
        location: File,
        reference: String,
        valueType: Class<T>,
    ): T {
        return objectMapper.treeToValue(
            readReference(location, reference),
            valueType
        )
    }

    private fun readReference(
        location: File,
        reference: String
    ): JsonNode {
        val components = reference.split('#')
        val filePath = components[0]
        val nodePath = components.getOrNull(1) ?: "/"
        val fileTree = objectMapper.readTree(File(location.parentFile, filePath))
        return fileTree.at(nodePath)
    }
}
