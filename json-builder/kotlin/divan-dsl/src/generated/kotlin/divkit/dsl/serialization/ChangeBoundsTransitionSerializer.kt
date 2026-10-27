package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.ChangeBoundsTransition

@Generated
internal class ChangeBoundsTransitionSerializer : EntitySerializer<ChangeBoundsTransition>(ChangeBoundsTransition::class.java, "change_bounds") {
    override fun writeFields(value: ChangeBoundsTransition, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(INTERPOLATOR, properties.interpolator, provider)
        gen.writeProperty(START_DELAY, properties.startDelay, provider)
    }

    private companion object {
        val DURATION = PropertyName("duration")
        val INTERPOLATOR = PropertyName("interpolator")
        val START_DELAY = PropertyName("start_delay")
    }
}
