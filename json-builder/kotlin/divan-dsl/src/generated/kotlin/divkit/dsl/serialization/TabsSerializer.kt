package divkit.dsl.serialization

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import divkit.dsl.annotation.*
import divkit.dsl.Tabs

@Generated
internal class TabsSerializer : EntitySerializer<Tabs>(Tabs::class.java, "tabs") {
    override fun writeFields(value: Tabs, gen: JsonGenerator, provider: SerializerProvider) {
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
        gen.writeProperty(DYNAMIC_HEIGHT, properties.dynamicHeight, provider)
        gen.writeProperty(EXTENSIONS, properties.extensions, provider)
        gen.writeProperty(FOCUS, properties.focus, provider)
        gen.writeProperty(FUNCTIONS, properties.functions, provider)
        gen.writeProperty(HAS_SEPARATOR, properties.hasSeparator, provider)
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(ID, properties.id, provider)
        gen.writeProperty(ITEMS, properties.items, provider)
        gen.writeProperty(LAYOUT_PROVIDER, properties.layoutProvider, provider)
        gen.writeProperty(MARGINS, properties.margins, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
        gen.writeProperty(RESTRICT_PARENT_SCROLL, properties.restrictParentScroll, provider)
        gen.writeProperty(REUSE_ID, properties.reuseId, provider)
        gen.writeProperty(ROW_SPAN, properties.rowSpan, provider)
        gen.writeProperty(SELECTED_ACTIONS, properties.selectedActions, provider)
        gen.writeProperty(SELECTED_TAB, properties.selectedTab, provider)
        gen.writeProperty(SEPARATOR_COLOR, properties.separatorColor, provider)
        gen.writeProperty(SEPARATOR_PADDINGS, properties.separatorPaddings, provider)
        gen.writeProperty(SWITCH_TABS_BY_CONTENT_SWIPE_ENABLED, properties.switchTabsByContentSwipeEnabled, provider)
        gen.writeProperty(TAB_TITLE_DELIMITER, properties.tabTitleDelimiter, provider)
        gen.writeProperty(TAB_TITLE_STYLE, properties.tabTitleStyle, provider)
        gen.writeProperty(TITLE_PADDINGS, properties.titlePaddings, provider)
        gen.writeProperty(TOOLTIPS, properties.tooltips, provider)
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
        val DYNAMIC_HEIGHT = PropertyName("dynamic_height")
        val EXTENSIONS = PropertyName("extensions")
        val FOCUS = PropertyName("focus")
        val FUNCTIONS = PropertyName("functions")
        val HAS_SEPARATOR = PropertyName("has_separator")
        val HEIGHT = PropertyName("height")
        val ID = PropertyName("id")
        val ITEMS = PropertyName("items")
        val LAYOUT_PROVIDER = PropertyName("layout_provider")
        val MARGINS = PropertyName("margins")
        val PADDINGS = PropertyName("paddings")
        val RESTRICT_PARENT_SCROLL = PropertyName("restrict_parent_scroll")
        val REUSE_ID = PropertyName("reuse_id")
        val ROW_SPAN = PropertyName("row_span")
        val SELECTED_ACTIONS = PropertyName("selected_actions")
        val SELECTED_TAB = PropertyName("selected_tab")
        val SEPARATOR_COLOR = PropertyName("separator_color")
        val SEPARATOR_PADDINGS = PropertyName("separator_paddings")
        val SWITCH_TABS_BY_CONTENT_SWIPE_ENABLED = PropertyName("switch_tabs_by_content_swipe_enabled")
        val TAB_TITLE_DELIMITER = PropertyName("tab_title_delimiter")
        val TAB_TITLE_STYLE = PropertyName("tab_title_style")
        val TITLE_PADDINGS = PropertyName("title_paddings")
        val TOOLTIPS = PropertyName("tooltips")
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
internal class TabsItemSerializer : EntitySerializer<Tabs.Item>(Tabs.Item::class.java) {
    override fun writeFields(value: Tabs.Item, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(DIV, properties.div, provider)
        gen.writeProperty(TITLE, properties.title, provider)
        gen.writeProperty(TITLE_CLICK_ACTION, properties.titleClickAction, provider)
    }

    private companion object {
        val DIV = PropertyName("div")
        val TITLE = PropertyName("title")
        val TITLE_CLICK_ACTION = PropertyName("title_click_action")
    }
}

@Generated
internal class TabsTabTitleDelimiterSerializer : EntitySerializer<Tabs.TabTitleDelimiter>(Tabs.TabTitleDelimiter::class.java) {
    override fun writeFields(value: Tabs.TabTitleDelimiter, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(HEIGHT, properties.height, provider)
        gen.writeProperty(IMAGE_URL, properties.imageUrl, provider)
        gen.writeProperty(WIDTH, properties.width, provider)
    }

    private companion object {
        val HEIGHT = PropertyName("height")
        val IMAGE_URL = PropertyName("image_url")
        val WIDTH = PropertyName("width")
    }
}

@Generated
internal class TabsTabTitleStyleSerializer : EntitySerializer<Tabs.TabTitleStyle>(Tabs.TabTitleStyle::class.java) {
    override fun writeFields(value: Tabs.TabTitleStyle, gen: JsonGenerator, provider: SerializerProvider) {
        val properties = value.properties
        gen.writeProperty(ACTIVE_BACKGROUND_COLOR, properties.activeBackgroundColor, provider)
        gen.writeProperty(ACTIVE_FONT_VARIATION_SETTINGS, properties.activeFontVariationSettings, provider)
        gen.writeProperty(ACTIVE_FONT_WEIGHT, properties.activeFontWeight, provider)
        gen.writeProperty(ACTIVE_FONT_WEIGHT_VALUE, properties.activeFontWeightValue, provider)
        gen.writeProperty(ACTIVE_TEXT_COLOR, properties.activeTextColor, provider)
        gen.writeProperty(ANIMATION_DURATION, properties.animationDuration, provider)
        gen.writeProperty(ANIMATION_TYPE, properties.animationType, provider)
        gen.writeProperty(CORNER_RADIUS, properties.cornerRadius, provider)
        gen.writeProperty(CORNERS_RADIUS, properties.cornersRadius, provider)
        gen.writeProperty(FONT_FAMILY, properties.fontFamily, provider)
        gen.writeProperty(FONT_SIZE, properties.fontSize, provider)
        gen.writeProperty(FONT_SIZE_UNIT, properties.fontSizeUnit, provider)
        gen.writeProperty(FONT_WEIGHT, properties.fontWeight, provider)
        gen.writeProperty(INACTIVE_BACKGROUND_COLOR, properties.inactiveBackgroundColor, provider)
        gen.writeProperty(INACTIVE_FONT_VARIATION_SETTINGS, properties.inactiveFontVariationSettings, provider)
        gen.writeProperty(INACTIVE_FONT_WEIGHT, properties.inactiveFontWeight, provider)
        gen.writeProperty(INACTIVE_FONT_WEIGHT_VALUE, properties.inactiveFontWeightValue, provider)
        gen.writeProperty(INACTIVE_TEXT_COLOR, properties.inactiveTextColor, provider)
        gen.writeProperty(ITEM_SPACING, properties.itemSpacing, provider)
        gen.writeProperty(LETTER_SPACING, properties.letterSpacing, provider)
        gen.writeProperty(LINE_HEIGHT, properties.lineHeight, provider)
        gen.writeProperty(PADDINGS, properties.paddings, provider)
    }

    private companion object {
        val ACTIVE_BACKGROUND_COLOR = PropertyName("active_background_color")
        val ACTIVE_FONT_VARIATION_SETTINGS = PropertyName("active_font_variation_settings")
        val ACTIVE_FONT_WEIGHT = PropertyName("active_font_weight")
        val ACTIVE_FONT_WEIGHT_VALUE = PropertyName("active_font_weight_value")
        val ACTIVE_TEXT_COLOR = PropertyName("active_text_color")
        val ANIMATION_DURATION = PropertyName("animation_duration")
        val ANIMATION_TYPE = PropertyName("animation_type")
        val CORNER_RADIUS = PropertyName("corner_radius")
        val CORNERS_RADIUS = PropertyName("corners_radius")
        val FONT_FAMILY = PropertyName("font_family")
        val FONT_SIZE = PropertyName("font_size")
        val FONT_SIZE_UNIT = PropertyName("font_size_unit")
        val FONT_WEIGHT = PropertyName("font_weight")
        val INACTIVE_BACKGROUND_COLOR = PropertyName("inactive_background_color")
        val INACTIVE_FONT_VARIATION_SETTINGS = PropertyName("inactive_font_variation_settings")
        val INACTIVE_FONT_WEIGHT = PropertyName("inactive_font_weight")
        val INACTIVE_FONT_WEIGHT_VALUE = PropertyName("inactive_font_weight_value")
        val INACTIVE_TEXT_COLOR = PropertyName("inactive_text_color")
        val ITEM_SPACING = PropertyName("item_spacing")
        val LETTER_SPACING = PropertyName("letter_spacing")
        val LINE_HEIGHT = PropertyName("line_height")
        val PADDINGS = PropertyName("paddings")
    }
}
