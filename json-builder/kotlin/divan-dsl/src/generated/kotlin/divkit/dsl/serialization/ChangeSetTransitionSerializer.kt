package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ChangeSetTransition

@Generated
internal class ChangeSetTransitionSerializer : EntitySerializer<ChangeSetTransition>(ChangeSetTransition::class.java, "set") {
    override fun writeFields(value: ChangeSetTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ITEMS = PropertyName("items")
    }
}
