package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Select

@Generated
internal class SelectSerializer : EntitySerializer<Select>(Select::class.java, "select") {
    override fun writeFields(value: Select, gen: JsonGenerator, provider: SerializerProvider) {
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
        gen.writeProperty(FONT_FAMILY, properties.fontFamily, provider)
        gen.writeProperty(FONT_SIZE, properties.fontSize, provider)
        gen.writeProperty(FONT_SIZE_UNIT, properties.fontSizeUnit, provider)
        gen.writeProperty(FONT_VARIATION_SETTINGS, properties.fontVariationSettings, provider)
        gen.writeProperty(FONT_WEIGHT, properties.fontWeight, provider)
        gen.writeProperty(FONT_WEIGHT_VALUE, properties.fontWeightValue, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(HINT_COLOR, properties.hintColor, provider)
        gen.writeProperty(HINT_TEXT, properties.hintText, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(LAYOUT_PROVIDER, properties.layoutProvider, provider)
        gen.writeProperty(LETTER_SPACING, properties.letterSpacing, provider)
        gen.writeProperty(LINE_HEIGHT, properties.lineHeight, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(OPTIONS, properties.options, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
        gen.writeProperty(REUSE_ID, properties.reuseId, provider)
        gen.writeProperty(ROW_SPAN, properties.rowSpan, provider)
        gen.writeProperty(SELECTED_ACTIONS, properties.selectedActions, provider)
        gen.writeProperty(TEXT_COLOR, properties.textColor, provider)
        gen.writeProperty(TOOLTIPS, properties.tooltips, provider)
        gen.writeProperty(TRANSFORM, properties.transform, provider)
        gen.writeProperty(TRANSFORMATIONS, properties.transformations, provider)
        gen.writeProperty(TRANSITION_CHANGE, properties.transitionChange, provider)
        gen.writeProperty(TRANSITION_IN, properties.transitionIn, provider)
        gen.writeProperty(TRANSITION_OUT, properties.transitionOut, provider)
        gen.writeProperty(TRANSITION_TRIGGERS, properties.transitionTriggers, provider)
        gen.writeProperty(VALUE_VARIABLE, properties.valueVariable, provider)
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
        val FONT_FAMILY = PropertyName("font_family")
        val FONT_SIZE = PropertyName("font_size")
        val FONT_SIZE_UNIT = PropertyName("font_size_unit")
        val FONT_VARIATION_SETTINGS = PropertyName("font_variation_settings")
        val FONT_WEIGHT = PropertyName("font_weight")
        val FONT_WEIGHT_VALUE = PropertyName("font_weight_value")
        val FUNCTIONS = PropertyName("functions")
        val HEIGHT = PropertyName("height")
        val HINT_COLOR = PropertyName("hint_color")
        val HINT_TEXT = PropertyName("hint_text")
        val ID = PropertyName("id")
        val LAYOUT_PROVIDER = PropertyName("layout_provider")
        val LETTER_SPACING = PropertyName("letter_spacing")
        val LINE_HEIGHT = PropertyName("line_height")
        val MARGINS = PropertyName("margins")
        val OPTIONS = PropertyName("options")
        val PADDINGS = PropertyName("paddings")
        val REUSE_ID = PropertyName("reuse_id")
        val ROW_SPAN = PropertyName("row_span")
        val SELECTED_ACTIONS = PropertyName("selected_actions")
        val TEXT_COLOR = PropertyName("text_color")
        val TOOLTIPS = PropertyName("tooltips")
        val TRANSFORM = PropertyName("transform")
        val TRANSFORMATIONS = PropertyName("transformations")
        val TRANSITION_CHANGE = PropertyName("transition_change")
        val TRANSITION_IN = PropertyName("transition_in")
        val TRANSITION_OUT = PropertyName("transition_out")
        val TRANSITION_TRIGGERS = PropertyName("transition_triggers")
        val VALUE_VARIABLE = PropertyName("value_variable")
        val VARIABLE_TRIGGERS = PropertyName("variable_triggers")
        val VARIABLES = PropertyName("variables")
        val VISIBILITY = PropertyName("visibility")
        val VISIBILITY_ACTION = PropertyName("visibility_action")
        val VISIBILITY_ACTIONS = PropertyName("visibility_actions")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class SelectOptionSerializer : EntitySerializer<Select.Option>(Select.Option::class.java) {
    override fun writeFields(value: Select.Option, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(TEXT, properties.text, provider)
        gen.writeProperty(VALUE, properties.value, provider)
    }

    private companion object {
        val TEXT = PropertyName("text")
        val VALUE = PropertyName("value")
    }
}
