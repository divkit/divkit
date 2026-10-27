package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ActionAnimatorStop

@Generated
internal class ActionAnimatorStopSerializer : EntitySerializer<ActionAnimatorStop>(ActionAnimatorStop::class.java, "animator_stop") {
    override fun writeFields(value: ActionAnimatorStop, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATOR_ID, properties.animatorId, provider)
    }

    private companion object {
        val ANIMATOR_ID = PropertyName("animator_id")
    }
}
