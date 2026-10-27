package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Text

@Generated
internal class TextSerializer : EntitySerializer<Text>(Text::class.java, "text") {
    override fun writeFields(value: Text, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(TEXT, properties.text, provider)
        gen.writeProperty(ACCESSIBILITY, properties.accessibility, provider)
        gen.writeProperty(ACTION, properties.action, provider)
        gen.writeProperty(ACTION_ANIMATION, properties.actionAnimation, provider)
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(ALIGNMENT_HORIZONTAL, properties.alignmentHorizontal, provider)
        gen.writeProperty(ALIGNMENT_VERTICAL, properties.alignmentVertical, provider)
        gen.writeProperty(ALPHA, properties.alpha, provider)
        gen.writeProperty(ANIMATORS, properties.animators, provider)
        gen.writeProperty(AUTO_ELLIPSIZE, properties.autoEllipsize, provider)
        gen.writeProperty(BACKGROUND, properties.background, provider)
        gen.writeProperty(BORDER, properties.border, provider)
        gen.writeProperty(CAPTURE_FOCUS_ON_ACTION, properties.captureFocusOnAction, provider)
        gen.writeProperty(COLUMN_SPAN, properties.columnSpan, provider)
        gen.writeProperty(DISAPPEAR_ACTIONS, properties.disappearActions, provider)
        gen.writeProperty(DOUBLETAP_ACTIONS, properties.doubletapActions, provider)
        gen.writeProperty(ELLIPSIS, properties.ellipsis, provider)
        gen.writeProperty(EXTENSIONS, properties.extensions, provider)
        gen.writeProperty(FOCUS, properties.focus, provider)
        gen.writeProperty(FOCUSED_TEXT_COLOR, properties.focusedTextColor, provider)
        gen.writeProperty(FONT_FAMILY, properties.fontFamily, provider)
        gen.writeProperty(FONT_FEATURE_SETTINGS, properties.fontFeatureSettings, provider)
        gen.writeProperty(FONT_SIZE, properties.fontSize, provider)
        gen.writeProperty(FONT_SIZE_UNIT, properties.fontSizeUnit, provider)
        gen.writeProperty(FONT_VARIATION_SETTINGS, properties.fontVariationSettings, provider)
        gen.writeProperty(FONT_WEIGHT, properties.fontWeight, provider)
        gen.writeProperty(FONT_WEIGHT_VALUE, properties.fontWeightValue, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(HOVER_END_ACTIONS, properties.hoverEndActions, provider)
        gen.writeProperty(HOVER_START_ACTIONS, properties.hoverStartActions, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(IMAGE_BUILDER, properties.imageBuilder, provider)
        gen.writeProperty(IMAGES, properties.images, provider)
        gen.writeProperty(LAYOUT_PROVIDER, properties.layoutProvider, provider)
        gen.writeProperty(LETTER_SPACING, properties.letterSpacing, provider)
        gen.writeProperty(LINE_HEIGHT, properties.lineHeight, provider)
        gen.writeProperty(LONGTAP_ACTIONS, properties.longtapActions, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(MAX_LINES, properties.maxLines, provider)
        gen.writeProperty(MIN_HIDDEN_LINES, properties.minHiddenLines, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
        gen.writeProperty(PRESS_END_ACTIONS, properties.pressEndActions, provider)
        gen.writeProperty(PRESS_START_ACTIONS, properties.pressStartActions, provider)
        gen.writeProperty(RANGE_BUILDER, properties.rangeBuilder, provider)
        gen.writeProperty(RANGES, properties.ranges, provider)
        gen.writeProperty(REUSE_ID, properties.reuseId, provider)
        gen.writeProperty(ROW_SPAN, properties.rowSpan, provider)
        gen.writeProperty(SELECTABLE, properties.selectable, provider)
        gen.writeProperty(SELECTED_ACTIONS, properties.selectedActions, provider)
        gen.writeProperty(STRIKE, properties.strike, provider)
        gen.writeProperty(TEXT_ALIGNMENT_HORIZONTAL, properties.textAlignmentHorizontal, provider)
        gen.writeProperty(TEXT_ALIGNMENT_VERTICAL, properties.textAlignmentVertical, provider)
        gen.writeProperty(TEXT_COLOR, properties.textColor, provider)
        gen.writeProperty(TEXT_GRADIENT, properties.textGradient, provider)
        gen.writeProperty(TEXT_SHADOW, properties.textShadow, provider)
        gen.writeProperty(TIGHTEN_WIDTH, properties.tightenWidth, provider)
        gen.writeProperty(TOOLTIPS, properties.tooltips, provider)
        gen.writeProperty(TRANSFORM, properties.transform, provider)
        gen.writeProperty(TRANSFORMATIONS, properties.transformations, provider)
        gen.writeProperty(TRANSITION_CHANGE, properties.transitionChange, provider)
        gen.writeProperty(TRANSITION_IN, properties.transitionIn, provider)
        gen.writeProperty(TRANSITION_OUT, properties.transitionOut, provider)
        gen.writeProperty(TRANSITION_TRIGGERS, properties.transitionTriggers, provider)
        gen.writeProperty(TRUNCATE, properties.truncate, provider)
        gen.writeProperty(TRUNCATE_POLICY, properties.truncatePolicy, provider)
        gen.writeProperty(UNDERLINE, properties.underline, provider)
        gen.writeProperty(VARIABLE_TRIGGERS, properties.variableTriggers, provider)
        gen.writeProperty(VARIABLES, properties.variables, provider)
        gen.writeProperty(VISIBILITY, properties.visibility, provider)
        gen.writeProperty(VISIBILITY_ACTION, properties.visibilityAction, provider)
        gen.writeProperty(VISIBILITY_ACTIONS, properties.visibilityActions, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val TEXT = PropertyName("text")
        val ACCESSIBILITY = PropertyName("accessibility")
        val ACTION = PropertyName("action")
        val ACTION_ANIMATION = PropertyName("action_animation")
        val ACTIONS = PropertyName("actions")
        val ALIGNMENT_HORIZONTAL = PropertyName("alignment_horizontal")
        val ALIGNMENT_VERTICAL = PropertyName("alignment_vertical")
        val ALPHA = PropertyName("alpha")
        val ANIMATORS = PropertyName("animators")
        val AUTO_ELLIPSIZE = PropertyName("auto_ellipsize")
        val BACKGROUND = PropertyName("background")
        val BORDER = PropertyName("border")
        val CAPTURE_FOCUS_ON_ACTION = PropertyName("capture_focus_on_action")
        val COLUMN_SPAN = PropertyName("column_span")
        val DISAPPEAR_ACTIONS = PropertyName("disappear_actions")
        val DOUBLETAP_ACTIONS = PropertyName("doubletap_actions")
        val ELLIPSIS = PropertyName("ellipsis")
        val EXTENSIONS = PropertyName("extensions")
        val FOCUS = PropertyName("focus")
        val FOCUSED_TEXT_COLOR = PropertyName("focused_text_color")
        val FONT_FAMILY = PropertyName("font_family")
        val FONT_FEATURE_SETTINGS = PropertyName("font_feature_settings")
        val FONT_SIZE = PropertyName("font_size")
        val FONT_SIZE_UNIT = PropertyName("font_size_unit")
        val FONT_VARIATION_SETTINGS = PropertyName("font_variation_settings")
        val FONT_WEIGHT = PropertyName("font_weight")
        val FONT_WEIGHT_VALUE = PropertyName("font_weight_value")
        val FUNCTIONS = PropertyName("functions")
        val HEIGHT = PropertyName("height")
        val HOVER_END_ACTIONS = PropertyName("hover_end_actions")
        val HOVER_START_ACTIONS = PropertyName("hover_start_actions")
        val ID = PropertyName("id")
        val IMAGE_BUILDER = PropertyName("image_builder")
        val IMAGES = PropertyName("images")
        val LAYOUT_PROVIDER = PropertyName("layout_provider")
        val LETTER_SPACING = PropertyName("letter_spacing")
        val LINE_HEIGHT = PropertyName("line_height")
        val LONGTAP_ACTIONS = PropertyName("longtap_actions")
        val MARGINS = PropertyName("margins")
        val MAX_LINES = PropertyName("max_lines")
        val MIN_HIDDEN_LINES = PropertyName("min_hidden_lines")
        val PADDINGS = PropertyName("paddings")
        val PRESS_END_ACTIONS = PropertyName("press_end_actions")
        val PRESS_START_ACTIONS = PropertyName("press_start_actions")
        val RANGE_BUILDER = PropertyName("range_builder")
        val RANGES = PropertyName("ranges")
        val REUSE_ID = PropertyName("reuse_id")
        val ROW_SPAN = PropertyName("row_span")
        val SELECTABLE = PropertyName("selectable")
        val SELECTED_ACTIONS = PropertyName("selected_actions")
        val STRIKE = PropertyName("strike")
        val TEXT_ALIGNMENT_HORIZONTAL = PropertyName("text_alignment_horizontal")
        val TEXT_ALIGNMENT_VERTICAL = PropertyName("text_alignment_vertical")
        val TEXT_COLOR = PropertyName("text_color")
        val TEXT_GRADIENT = PropertyName("text_gradient")
        val TEXT_SHADOW = PropertyName("text_shadow")
        val TIGHTEN_WIDTH = PropertyName("tighten_width")
        val TOOLTIPS = PropertyName("tooltips")
        val TRANSFORM = PropertyName("transform")
        val TRANSFORMATIONS = PropertyName("transformations")
        val TRANSITION_CHANGE = PropertyName("transition_change")
        val TRANSITION_IN = PropertyName("transition_in")
        val TRANSITION_OUT = PropertyName("transition_out")
        val TRANSITION_TRIGGERS = PropertyName("transition_triggers")
        val TRUNCATE = PropertyName("truncate")
        val TRUNCATE_POLICY = PropertyName("truncate_policy")
        val UNDERLINE = PropertyName("underline")
        val VARIABLE_TRIGGERS = PropertyName("variable_triggers")
        val VARIABLES = PropertyName("variables")
        val VISIBILITY = PropertyName("visibility")
        val VISIBILITY_ACTION = PropertyName("visibility_action")
        val VISIBILITY_ACTIONS = PropertyName("visibility_actions")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class TextEllipsisSerializer : EntitySerializer<Text.Ellipsis>(Text.Ellipsis::class.java) {
    override fun writeFields(value: Text.Ellipsis, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(IMAGE_BUILDER, properties.imageBuilder, provider)
        gen.writeProperty(IMAGES, properties.images, provider)
        gen.writeProperty(RANGE_BUILDER, properties.rangeBuilder, provider)
        gen.writeProperty(RANGES, properties.ranges, provider)
        gen.writeProperty(TEXT, properties.text, provider)
    }

    private companion object {
        val ACTIONS = PropertyName("actions")
        val IMAGE_BUILDER = PropertyName("image_builder")
        val IMAGES = PropertyName("images")
        val RANGE_BUILDER = PropertyName("range_builder")
        val RANGES = PropertyName("ranges")
        val TEXT = PropertyName("text")
    }
}

@Generated
internal class TextImageSerializer : EntitySerializer<Text.Image>(Text.Image::class.java) {
    override fun writeFields(value: Text.Image, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACCESSIBILITY, properties.accessibility, provider)
        gen.writeProperty(ALIGNMENT_VERTICAL, properties.alignmentVertical, provider)
        gen.writeProperty(BASELINE_OFFSET, properties.baselineOffset, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(INDEXING_DIRECTION, properties.indexingDirection, provider)
        gen.writeProperty(PRELOAD_REQUIRED, properties.preloadRequired, provider)
        gen.writeProperty(START, properties.start, provider)
        gen.writeProperty(TINT_COLOR, properties.tintColor, provider)
        gen.writeProperty(TINT_MODE, properties.tintMode, provider)
        gen.writeProperty(URL, properties.url, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val ACCESSIBILITY = PropertyName("accessibility")
        val ALIGNMENT_VERTICAL = PropertyName("alignment_vertical")
        val BASELINE_OFFSET = PropertyName("baseline_offset")
        val HEIGHT = PropertyName("height")
        val INDEXING_DIRECTION = PropertyName("indexing_direction")
        val PRELOAD_REQUIRED = PropertyName("preload_required")
        val START = PropertyName("start")
        val TINT_COLOR = PropertyName("tint_color")
        val TINT_MODE = PropertyName("tint_mode")
        val URL = PropertyName("url")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class TextImageAccessibilitySerializer : EntitySerializer<Text.Image.Accessibility>(Text.Image.Accessibility::class.java) {
    override fun writeFields(value: Text.Image.Accessibility, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DESCRIPTION, properties.description, provider)
        gen.writeProperty(TYPE, properties.type, provider)
    }

    private companion object {
        val DESCRIPTION = PropertyName("description")
        val TYPE = PropertyName("type")
    }
}

@Generated
internal class TextImageBuilderSerializer : EntitySerializer<Text.ImageBuilder>(Text.ImageBuilder::class.java) {
    override fun writeFields(value: Text.ImageBuilder, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DATA, properties.data, provider)
        gen.writeProperty(DATA_ELEMENT_NAME, properties.dataElementName, provider)
        gen.writeProperty(PROTOTYPES, properties.prototypes, provider)
    }

    private companion object {
        val DATA = PropertyName("data")
        val DATA_ELEMENT_NAME = PropertyName("data_element_name")
        val PROTOTYPES = PropertyName("prototypes")
    }
}

@Generated
internal class TextImageBuilderPrototypeSerializer : EntitySerializer<Text.ImageBuilder.Prototype>(Text.ImageBuilder.Prototype::class.java) {
    override fun writeFields(value: Text.ImageBuilder.Prototype, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(IMAGE, properties.image, provider)
        gen.writeProperty(SELECTOR, properties.selector, provider)
    }

    private companion object {
        val IMAGE = PropertyName("image")
        val SELECTOR = PropertyName("selector")
    }
}

@Generated
internal class TextRangeSerializer : EntitySerializer<Text.Range>(Text.Range::class.java) {
    override fun writeFields(value: Text.Range, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTIONS, properties.actions, provider)
        gen.writeProperty(ALIGNMENT_VERTICAL, properties.alignmentVertical, provider)
        gen.writeProperty(BACKGROUND, properties.background, provider)
        gen.writeProperty(BASELINE_OFFSET, properties.baselineOffset, provider)
        gen.writeProperty(BORDER, properties.border, provider)
        gen.writeProperty(END, properties.end, provider)
        gen.writeProperty(FONT_FAMILY, properties.fontFamily, provider)
        gen.writeProperty(FONT_FEATURE_SETTINGS, properties.fontFeatureSettings, provider)
        gen.writeProperty(FONT_SIZE, properties.fontSize, provider)
        gen.writeProperty(FONT_SIZE_UNIT, properties.fontSizeUnit, provider)
        gen.writeProperty(FONT_VARIATION_SETTINGS, properties.fontVariationSettings, provider)
        gen.writeProperty(FONT_WEIGHT, properties.fontWeight, provider)
        gen.writeProperty(FONT_WEIGHT_VALUE, properties.fontWeightValue, provider)
        gen.writeProperty(LETTER_SPACING, properties.letterSpacing, provider)
        gen.writeProperty(LINE_HEIGHT, properties.lineHeight, provider)
        gen.writeProperty(MASK, properties.mask, provider)
        gen.writeProperty(START, properties.start, provider)
        gen.writeProperty(STRIKE, properties.strike, provider)
        gen.writeProperty(TEXT_COLOR, properties.textColor, provider)
        gen.writeProperty(TEXT_SHADOW, properties.textShadow, provider)
        gen.writeProperty(TOP_OFFSET, properties.topOffset, provider)
        gen.writeProperty(UNDERLINE, properties.underline, provider)
    }

    private companion object {
        val ACTIONS = PropertyName("actions")
        val ALIGNMENT_VERTICAL = PropertyName("alignment_vertical")
        val BACKGROUND = PropertyName("background")
        val BASELINE_OFFSET = PropertyName("baseline_offset")
        val BORDER = PropertyName("border")
        val END = PropertyName("end")
        val FONT_FAMILY = PropertyName("font_family")
        val FONT_FEATURE_SETTINGS = PropertyName("font_feature_settings")
        val FONT_SIZE = PropertyName("font_size")
        val FONT_SIZE_UNIT = PropertyName("font_size_unit")
        val FONT_VARIATION_SETTINGS = PropertyName("font_variation_settings")
        val FONT_WEIGHT = PropertyName("font_weight")
        val FONT_WEIGHT_VALUE = PropertyName("font_weight_value")
        val LETTER_SPACING = PropertyName("letter_spacing")
        val LINE_HEIGHT = PropertyName("line_height")
        val MASK = PropertyName("mask")
        val START = PropertyName("start")
        val STRIKE = PropertyName("strike")
        val TEXT_COLOR = PropertyName("text_color")
        val TEXT_SHADOW = PropertyName("text_shadow")
        val TOP_OFFSET = PropertyName("top_offset")
        val UNDERLINE = PropertyName("underline")
    }
}

@Generated
internal class TextRangeBuilderSerializer : EntitySerializer<Text.RangeBuilder>(Text.RangeBuilder::class.java) {
    override fun writeFields(value: Text.RangeBuilder, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DATA, properties.data, provider)
        gen.writeProperty(DATA_ELEMENT_NAME, properties.dataElementName, provider)
        gen.writeProperty(PROTOTYPES, properties.prototypes, provider)
    }

    private companion object {
        val DATA = PropertyName("data")
        val DATA_ELEMENT_NAME = PropertyName("data_element_name")
        val PROTOTYPES = PropertyName("prototypes")
    }
}

@Generated
internal class TextRangeBuilderPrototypeSerializer : EntitySerializer<Text.RangeBuilder.Prototype>(Text.RangeBuilder.Prototype::class.java) {
    override fun writeFields(value: Text.RangeBuilder.Prototype, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(RANGE, properties.range, provider)
        gen.writeProperty(SELECTOR, properties.selector, provider)
    }

    private companion object {
        val RANGE = PropertyName("range")
        val SELECTOR = PropertyName("selector")
    }
}
