package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Focus

@Generated
internal class FocusSerializer : EntitySerializer<Focus>(Focus::class.java) {
    override fun writeFields(value: Focus, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(BACKGROUND, properties.background, provider)
        gen.writeProperty(BORDER, properties.border, provider)
        gen.writeProperty(NEXT_FOCUS_IDS, properties.nextFocusIds, provider)
        gen.writeProperty(ON_BLUR, properties.onBlur, provider)
        gen.writeProperty(ON_FOCUS, properties.onFocus, provider)
    }

    private companion object {
        val BACKGROUND = PropertyName("background")
        val BORDER = PropertyName("border")
        val NEXT_FOCUS_IDS = PropertyName("next_focus_ids")
        val ON_BLUR = PropertyName("on_blur")
        val ON_FOCUS = PropertyName("on_focus")
    }
}

@Generated
internal class FocusNextFocusIdsSerializer : EntitySerializer<Focus.NextFocusIds>(Focus.NextFocusIds::class.java) {
    override fun writeFields(value: Focus.NextFocusIds, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DOWN, properties.down, provider)
        gen.writeProperty(FORWARD, properties.forward, provider)
        gen.writeProperty(LEFT, properties.left, provider)
        gen.writeProperty(RIGHT, properties.right, provider)
        gen.writeProperty(UP, properties.up, provider)
    }

    private companion object {
        val DOWN = PropertyName("down")
        val FORWARD = PropertyName("forward")
        val LEFT = PropertyName("left")
        val RIGHT = PropertyName("right")
        val UP = PropertyName("up")
    }
}
