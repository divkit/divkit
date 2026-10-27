package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Patch

@Generated
internal class PatchSerializer : EntitySerializer<Patch>(Patch::class.java) {
    override fun writeFields(value: Patch, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(CHANGES, properties.changes, provider)
        gen.writeProperty(MODE, properties.mode, provider)
        gen.writeProperty(ON_APPLIED_ACTIONS, properties.onAppliedActions, provider)
        gen.writeProperty(ON_FAILED_ACTIONS, properties.onFailedActions, provider)
    }

    private companion object {
        val CHANGES = PropertyName("changes")
        val MODE = PropertyName("mode")
        val ON_APPLIED_ACTIONS = PropertyName("on_applied_actions")
        val ON_FAILED_ACTIONS = PropertyName("on_failed_actions")
    }
}

@Generated
internal class PatchChangeSerializer : EntitySerializer<Patch.Change>(Patch.Change::class.java) {
    override fun writeFields(value: Patch.Change, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ID = PropertyName("id")
        val ITEMS = PropertyName("items")
    }
}
