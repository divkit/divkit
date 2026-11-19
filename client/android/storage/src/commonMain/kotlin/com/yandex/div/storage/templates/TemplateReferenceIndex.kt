package com.yandex.div.storage.templates

import com.yandex.div.core.annotations.InternalApi
import kotlin.jvm.JvmInline

private typealias TemplateHash = String
private typealias TemplateId = String

/**
 * Index of stored template references. Callers serialize access to the index and its groups.
 */
@InternalApi
public class TemplateReferenceIndex(private val logError: (Exception) -> Unit) {
    private val groups = mutableMapOf<String, TemplateReferenceResolver>()

    public operator fun get(groupId: String): TemplateReferenceResolver? = groups[groupId]

    public fun groupIds(): Set<String> = groups.keys.toSet()

    public fun resetGroup(groupId: String) {
        groups[groupId] = TemplateReferenceResolver(logError)
    }

    public fun add(groupId: String, templateId: String, templateHash: String) {
        val group = groups.getOrPut(groupId) { TemplateReferenceResolver(logError) }
        group.add(templateId, templateHash)
    }
}

@InternalApi
public class TemplateReferenceResolver internal constructor(
    private val logError: (Exception) -> Unit,
) {
    private val templateHashRefs = mutableMapOf<TemplateId, TemplateHash>()
    private val _templateHashes = mutableSetOf<TemplateHash>()
    public val templateHashes: Set<String> = _templateHashes

    private val templateIdRefs: Map<TemplateHash, TemplateHashIds> by lazy {
        val refs = HashMap<TemplateHash, TemplateHashIds>(templateHashRefs.size)
        templateHashRefs.forEach { (id, hash) ->
            val existent = refs[hash]
            refs[hash] = when (existent) {
                null -> TemplateHashIds.Single(id)
                is TemplateHashIds.Single -> {
                    TemplateHashIds.Collection(mutableListOf(existent.id, id))
                }
                is TemplateHashIds.Collection -> {
                    existent.ids.add(id)
                    existent
                }
            }
        }
        refs
    }

    internal fun add(templateId: String, templateHash: String) {
        templateHashRefs[templateId] = templateHash
        _templateHashes.add(templateHash)
    }

    public fun doOnEachResolvedId(hash: String, block: (templateId: String) -> Unit) {
        when (val ids = templateIdRefs[hash]) {
            is TemplateHashIds.Collection -> ids.ids.forEach { block(it) }
            is TemplateHashIds.Single -> block(ids.id)
            null -> logError(IllegalStateException("No template id was found for hash!"))
        }
    }

    public fun resolveTemplateHash(id: String): String? {
        return templateHashRefs[id]
    }
}

private sealed interface TemplateHashIds {
    @JvmInline
    value class Single(val id: String) : TemplateHashIds
    @JvmInline
    value class Collection(val ids: MutableList<String>) : TemplateHashIds
}
