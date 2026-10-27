package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Tooltip

@Generated
internal class TooltipSerializer : EntitySerializer<Tooltip>(Tooltip::class.java) {
    override fun writeFields(value: Tooltip, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ANIMATION_IN, properties.animationIn, provider)
        gen.writeProperty(ANIMATION_OUT, properties.animationOut, provider)
        gen.writeProperty(BACKGROUND_ACCESSIBILITY_DESCRIPTION, properties.backgroundAccessibilityDescription, provider)
        gen.writeProperty(BRING_TO_TOP_ID, properties.bringToTopId, provider)
        gen.writeProperty(CLOSE_BY_TAP_OUTSIDE, properties.closeByTapOutside, provider)
        gen.writeProperty(DIV, properties.div, provider)
        gen.writeProperty(DURATION, properties.duration, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(MODE, properties.mode, provider)
        gen.writeProperty(OFFSET, properties.offset, provider)
        gen.writeProperty(POSITION, properties.position, provider)
        gen.writeProperty(SUBSTRATE_DIV, properties.substrateDiv, provider)
        gen.writeProperty(TAP_OUTSIDE_ACTIONS, properties.tapOutsideActions, provider)
    }

    private companion object {
        val ANIMATION_IN = PropertyName("animation_in")
        val ANIMATION_OUT = PropertyName("animation_out")
        val BACKGROUND_ACCESSIBILITY_DESCRIPTION = PropertyName("background_accessibility_description")
        val BRING_TO_TOP_ID = PropertyName("bring_to_top_id")
        val CLOSE_BY_TAP_OUTSIDE = PropertyName("close_by_tap_outside")
        val DIV = PropertyName("div")
        val DURATION = PropertyName("duration")
        val ID = PropertyName("id")
        val MODE = PropertyName("mode")
        val OFFSET = PropertyName("offset")
        val POSITION = PropertyName("position")
        val SUBSTRATE_DIV = PropertyName("substrate_div")
        val TAP_OUTSIDE_ACTIONS = PropertyName("tap_outside_actions")
    }
}
