package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Slider

@Generated
internal class SliderSerializer : EntitySerializer<Slider>(Slider::class.java, "slider") {
    override fun writeFields(value: Slider, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACCESSIBILITY, properties.accessibility, provider)
        gen.writeProperty(ALIGNMENT_HORIZONTAL, properties.alignmentHorizontal, provider)
        gen.writeProperty(ALIGNMENT_VERTICAL, properties.alignmentVertical, provider)
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(ANIMATORS, properties.animators, provider)
        gen.writeProperty(BACKGROUND, properties.background, provider)
        gen.writeProperty(BORDER, properties.border, provider)
        gen.writeProperty(COLUMN_SPAN, properties.columnSpan, provider)
        gen.writeProperty(DISAPPEAR_ACTIONS, properties.disappearActions, provider)
        gen.writeProperty(EXTENSIONS, properties.extensions, provider)
        gen.writeProperty(FOCUS, properties.focus, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(IS_ENABLED, properties.isEnabled, provider)
        gen.writeProperty(LAYOUT_PROVIDER, properties.layoutProvider, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(MAX_VALUE, properties.maxValue, provider)
        gen.writeProperty(MIN_VALUE, properties.minValue, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
        gen.writeProperty(PRESS_END_ACTIONS, properties.pressEndActions, provider)
        gen.writeProperty(PRESS_START_ACTIONS, properties.pressStartActions, provider)
        gen.writeProperty(RANGES, properties.ranges, provider)
        gen.writeProperty(REUSE_ID, properties.reuseId, provider)
        gen.writeProperty(ROW_SPAN, properties.rowSpan, provider)
        gen.writeProperty(SECONDARY_VALUE_ACCESSIBILITY, properties.secondaryValueAccessibility, provider)
        gen.writeProperty(SELECTED_ACTIONS, properties.selectedActions, provider)
        gen.writeProperty(THUMB_SECONDARY_STYLE, properties.thumbSecondaryStyle, provider)
        gen.writeProperty(THUMB_SECONDARY_TEXT_STYLE, properties.thumbSecondaryTextStyle, provider)
        gen.writeProperty(THUMB_SECONDARY_VALUE_VARIABLE, properties.thumbSecondaryValueVariable, provider)
        gen.writeProperty(THUMB_STYLE, properties.thumbStyle, provider)
        gen.writeProperty(THUMB_TEXT_STYLE, properties.thumbTextStyle, provider)
        gen.writeProperty(THUMB_VALUE_VARIABLE, properties.thumbValueVariable, provider)
        gen.writeProperty(TICK_MARK_ACTIVE_STYLE, properties.tickMarkActiveStyle, provider)
        gen.writeProperty(TICK_MARK_INACTIVE_STYLE, properties.tickMarkInactiveStyle, provider)
        gen.writeProperty(TOOLTIPS, properties.tooltips, provider)
        gen.writeProperty(TRACK_ACTIVE_STYLE, properties.trackActiveStyle, provider)
        gen.writeProperty(TRACK_INACTIVE_STYLE, properties.trackInactiveStyle, provider)
        gen.writeProperty(TRANSFORM, properties.transform, provider)
        gen.writeProperty(TRANSFORMATIONS, properties.transformations, provider)
        gen.writeProperty(TRANSITION_CHANGE, properties.transitionChange, provider)
        gen.writeProperty(TRANSITION_IN, properties.transitionIn, provider)
        gen.writeProperty(TRANSITION_OUT, properties.transitionOut, provider)
        gen.writeProperty(TRANSITION_TRIGGERS, properties.transitionTriggers, provider)
        gen.writeProperty(VARIABLE_TRIGGERS, properties.variableTriggers, provider)
        gen.writeProperty(VARIABLES, properties.variables, provider)
        gen.writeProperty(VISIBILITY, properties.visibility, provider)
        gen.writeProperty(VISIBILITY_ACTION, properties.visibilityAction, provider)
        gen.writeProperty(VISIBILITY_ACTIONS, properties.visibilityActions, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val ACCESSIBILITY = PropertyName("accessibility")
        val ALIGNMENT_HORIZONTAL = PropertyName("alignment_horizontal")
        val ALIGNMENT_VERTICAL = PropertyName("alignment_vertical")
        val ALPHA = PropertyName("alpha")
        val ANIMATORS = PropertyName("animators")
        val BACKGROUND = PropertyName("background")
        val BORDER = PropertyName("border")
        val COLUMN_SPAN = PropertyName("column_span")
        val DISAPPEAR_ACTIONS = PropertyName("disappear_actions")
        val EXTENSIONS = PropertyName("extensions")
        val FOCUS = PropertyName("focus")
        val FUNCTIONS = PropertyName("functions")
        val HEIGHT = PropertyName("height")
        val ID = PropertyName("id")
        val IS_ENABLED = PropertyName("is_enabled")
        val LAYOUT_PROVIDER = PropertyName("layout_provider")
        val MARGINS = PropertyName("margins")
        val MAX_VALUE = PropertyName("max_value")
        val MIN_VALUE = PropertyName("min_value")
        val PADDINGS = PropertyName("paddings")
        val PRESS_END_ACTIONS = PropertyName("press_end_actions")
        val PRESS_START_ACTIONS = PropertyName("press_start_actions")
        val RANGES = PropertyName("ranges")
        val REUSE_ID = PropertyName("reuse_id")
        val ROW_SPAN = PropertyName("row_span")
        val SECONDARY_VALUE_ACCESSIBILITY = PropertyName("secondary_value_accessibility")
        val SELECTED_ACTIONS = PropertyName("selected_actions")
        val THUMB_SECONDARY_STYLE = PropertyName("thumb_secondary_style")
        val THUMB_SECONDARY_TEXT_STYLE = PropertyName("thumb_secondary_text_style")
        val THUMB_SECONDARY_VALUE_VARIABLE = PropertyName("thumb_secondary_value_variable")
        val THUMB_STYLE = PropertyName("thumb_style")
        val THUMB_TEXT_STYLE = PropertyName("thumb_text_style")
        val THUMB_VALUE_VARIABLE = PropertyName("thumb_value_variable")
        val TICK_MARK_ACTIVE_STYLE = PropertyName("tick_mark_active_style")
        val TICK_MARK_INACTIVE_STYLE = PropertyName("tick_mark_inactive_style")
        val TOOLTIPS = PropertyName("tooltips")
        val TRACK_ACTIVE_STYLE = PropertyName("track_active_style")
        val TRACK_INACTIVE_STYLE = PropertyName("track_inactive_style")
        val TRANSFORM = PropertyName("transform")
        val TRANSFORMATIONS = PropertyName("transformations")
        val TRANSITION_CHANGE = PropertyName("transition_change")
        val TRANSITION_IN = PropertyName("transition_in")
        val TRANSITION_OUT = PropertyName("transition_out")
        val TRANSITION_TRIGGERS = PropertyName("transition_triggers")
        val VARIABLE_TRIGGERS = PropertyName("variable_triggers")
        val VARIABLES = PropertyName("variables")
        val VISIBILITY = PropertyName("visibility")
        val VISIBILITY_ACTION = PropertyName("visibility_action")
        val VISIBILITY_ACTIONS = PropertyName("visibility_actions")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class SliderRangeSerializer : EntitySerializer<Slider.Range>(Slider.Range::class.java) {
    override fun writeFields(value: Slider.Range, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(END, properties.end, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(START, properties.start, provider)
        gen.writeProperty(TRACK_ACTIVE_STYLE, properties.trackActiveStyle, provider)
        gen.writeProperty(TRACK_INACTIVE_STYLE, properties.trackInactiveStyle, provider)
    }

    private companion object {
        val END = PropertyName("end")
        val MARGINS = PropertyName("margins")
        val START = PropertyName("start")
        val TRACK_ACTIVE_STYLE = PropertyName("track_active_style")
        val TRACK_INACTIVE_STYLE = PropertyName("track_inactive_style")
    }
}

@Generated
internal class SliderTextStyleSerializer : EntitySerializer<Slider.TextStyle>(Slider.TextStyle::class.java) {
    override fun writeFields(value: Slider.TextStyle, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(FONT_FAMILY, properties.fontFamily, provider)
        gen.writeProperty(FONT_SIZE, properties.fontSize, provider)
        gen.writeProperty(FONT_SIZE_UNIT, properties.fontSizeUnit, provider)
        gen.writeProperty(FONT_VARIATION_SETTINGS, properties.fontVariationSettings, provider)
        gen.writeProperty(FONT_WEIGHT, properties.fontWeight, provider)
        gen.writeProperty(FONT_WEIGHT_VALUE, properties.fontWeightValue, provider)
        gen.writeProperty(LETTER_SPACING, properties.letterSpacing, provider)
        gen.writeProperty(OFFSET, properties.offset, provider)
        gen.writeProperty(TEXT_COLOR, properties.textColor, provider)
    }

    private companion object {
        val FONT_FAMILY = PropertyName("font_family")
        val FONT_SIZE = PropertyName("font_size")
        val FONT_SIZE_UNIT = PropertyName("font_size_unit")
        val FONT_VARIATION_SETTINGS = PropertyName("font_variation_settings")
        val FONT_WEIGHT = PropertyName("font_weight")
        val FONT_WEIGHT_VALUE = PropertyName("font_weight_value")
        val LETTER_SPACING = PropertyName("letter_spacing")
        val OFFSET = PropertyName("offset")
        val TEXT_COLOR = PropertyName("text_color")
    }
}
