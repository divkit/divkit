package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.AppearanceSetTransition

@Generated
internal class AppearanceSetTransitionSerializer : EntitySerializer<AppearanceSetTransition>(AppearanceSetTransition::class.java, "set") {
    override fun writeFields(value: AppearanceSetTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ITEMS, properties.items, provider)
    }

    private companion object {
        val ITEMS = PropertyName("items")
    }
}
