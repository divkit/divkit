package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.LayoutProvider

@Generated
internal class LayoutProviderSerializer : EntitySerializer<LayoutProvider>(LayoutProvider::class.java) {
    override fun writeFields(value: LayoutProvider, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(HEIGHT_VARIABLE_NAME, properties.heightVariableName, provider)
        gen.writeProperty(WIDTH_VARIABLE_NAME, properties.widthVariableName, provider)
    }

    private companion object {
        val HEIGHT_VARIABLE_NAME = PropertyName("height_variable_name")
        val WIDTH_VARIABLE_NAME = PropertyName("width_variable_name")
    }
}
