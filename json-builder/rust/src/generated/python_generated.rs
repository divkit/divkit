// Generated code. Do not modify.

use pyo3::prelude::*;

use crate::py_entity::register_entity_class;

/// Register all entity types on the module.
pub fn register_all_entities(py: Python<'_>, m: &Bound<'_, PyModule>) -> PyResult<()> {
    register_entity_class(py, m, "ArrayValue", Some("array"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "ArrayVariable", Some("array"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "BooleanValue", Some("boolean"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "BooleanVariable", Some("boolean"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "ColorValue", Some("color"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "ColorVariable", Some("color"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "ContentText", Some("text"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "ContentUrl", Some("url"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DictValue", Some("dict"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DictVariable", Some("dict"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "DivAbsoluteEdgeInsets", None,
        &["bottom", "left", "right", "top"], &[])?;
    register_entity_class(py, m, "DivAccessibility", None,
        &["description", "hint", "is_checked", "mode", "mute_after_action", "state_description", "type"], &[])?;
    register_entity_class(py, m, "DivAction", None,
        &["download_callbacks", "is_enabled", "log_id", "log_url", "menu_items", "payload", "referer", "scope_id", "target", "typed", "url"], &["log_id"])?;
    register_entity_class(py, m, "DivActionMenuItem", None,
        &["action", "actions", "text"], &["text"])?;
    register_entity_class(py, m, "DivActionAnimatorStart", Some("animator_start"),
        &["animator_id", "direction", "duration", "end_value", "interpolator", "repeat_count", "start_delay", "start_value"], &["animator_id"])?;
    register_entity_class(py, m, "DivActionAnimatorStop", Some("animator_stop"),
        &["animator_id"], &["animator_id"])?;
    register_entity_class(py, m, "DivActionArrayInsertValue", Some("array_insert_value"),
        &["index", "value", "variable_name"], &["value", "variable_name"])?;
    register_entity_class(py, m, "DivActionArrayRemoveValue", Some("array_remove_value"),
        &["index", "variable_name"], &["index", "variable_name"])?;
    register_entity_class(py, m, "DivActionArraySetValue", Some("array_set_value"),
        &["index", "value", "variable_name"], &["index", "value", "variable_name"])?;
    register_entity_class(py, m, "DivActionClearFocus", Some("clear_focus"),
        &[], &[])?;
    register_entity_class(py, m, "DivActionCopyToClipboard", Some("copy_to_clipboard"),
        &["content"], &["content"])?;
    register_entity_class(py, m, "DivActionCustom", Some("custom"),
        &[], &[])?;
    register_entity_class(py, m, "DivActionDictSetValue", Some("dict_set_value"),
        &["key", "value", "variable_name"], &["key", "variable_name"])?;
    register_entity_class(py, m, "DivActionDownload", Some("download"),
        &["on_fail_actions", "on_success_actions", "url"], &["url"])?;
    register_entity_class(py, m, "DivActionFocusElement", Some("focus_element"),
        &["element_id"], &["element_id"])?;
    register_entity_class(py, m, "DivActionHideTooltip", Some("hide_tooltip"),
        &["id"], &["id"])?;
    register_entity_class(py, m, "DivActionScrollBy", Some("scroll_by"),
        &["animated", "id", "item_count", "offset", "overflow"], &["id"])?;
    register_entity_class(py, m, "DivActionScrollTo", Some("scroll_to"),
        &["animated", "destination", "id"], &["destination", "id"])?;
    register_entity_class(py, m, "DivActionSetState", Some("set_state"),
        &["state_id", "temporary"], &["state_id"])?;
    register_entity_class(py, m, "DivActionSetStoredValue", Some("set_stored_value"),
        &["lifetime", "name", "value"], &["lifetime", "name", "value"])?;
    register_entity_class(py, m, "DivActionSetVariable", Some("set_variable"),
        &["value", "variable_name"], &["value", "variable_name"])?;
    register_entity_class(py, m, "DivActionShowTooltip", Some("show_tooltip"),
        &["id", "multiple"], &["id"])?;
    register_entity_class(py, m, "DivActionSubmit", Some("submit"),
        &["container_id", "on_fail_actions", "on_success_actions", "request"], &["container_id", "request"])?;
    register_entity_class(py, m, "DivActionSubmitRequest", None,
        &["headers", "method", "url"], &["url"])?;
    register_entity_class(py, m, "RequestHeader", None,
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "DivActionTimer", Some("timer"),
        &["action", "id"], &["action", "id"])?;
    register_entity_class(py, m, "DivActionUpdateStructure", Some("update_structure"),
        &["path", "value", "variable_name"], &["path", "value", "variable_name"])?;
    register_entity_class(py, m, "DivActionVideo", Some("video"),
        &["action", "id"], &["action", "id"])?;
    register_entity_class(py, m, "DivAnimation", None,
        &["duration", "end_value", "interpolator", "items", "name", "repeat", "start_delay", "start_value"], &["name"])?;
    register_entity_class(py, m, "DivAnimatorBase", None,
        &["cancel_actions", "direction", "duration", "end_actions", "id", "interpolator", "repeat_count", "start_delay", "variable_name"], &["duration", "id", "variable_name"])?;
    register_entity_class(py, m, "DivAppearanceSetTransition", Some("set"),
        &["items"], &["items"])?;
    register_entity_class(py, m, "DivAspect", None,
        &["ratio"], &["ratio"])?;
    register_entity_class(py, m, "DivBase", None,
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "disappear_actions", "extensions", "focus", "functions", "height", "id", "layout_provider", "margins", "paddings", "reuse_id", "row_span", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &[])?;
    register_entity_class(py, m, "DivBlur", Some("blur"),
        &["radius"], &["radius"])?;
    register_entity_class(py, m, "DivBorder", None,
        &["corner_radius", "corners_radius", "has_shadow", "shadow", "stroke"], &[])?;
    register_entity_class(py, m, "DivChangeBoundsTransition", Some("change_bounds"),
        &["duration", "interpolator", "start_delay"], &[])?;
    register_entity_class(py, m, "DivChangeSetTransition", Some("set"),
        &["items"], &["items"])?;
    register_entity_class(py, m, "DivCircleShape", Some("circle"),
        &["background_color", "radius", "stroke"], &[])?;
    register_entity_class(py, m, "DivCloudBackground", Some("cloud"),
        &["color", "corner_radius", "paddings"], &["color", "corner_radius"])?;
    register_entity_class(py, m, "DivCollectionItemBuilder", None,
        &["data", "data_element_name", "prototypes"], &["data", "prototypes"])?;
    register_entity_class(py, m, "DivCollectionItemBuilderPrototype", None,
        &["div", "id", "selector"], &["div"])?;
    register_entity_class(py, m, "DivColorAnimator", Some("color_animator"),
        &["cancel_actions", "direction", "duration", "end_actions", "end_value", "id", "interpolator", "repeat_count", "start_delay", "start_value", "variable_name"], &["duration", "end_value", "id", "variable_name"])?;
    register_entity_class(py, m, "DivContainer", Some("container"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "aspect", "background", "border", "capture_focus_on_action", "clip_to_bounds", "column_span", "content_alignment_horizontal", "content_alignment_vertical", "disappear_actions", "doubletap_actions", "extensions", "focus", "functions", "height", "hover_end_actions", "hover_start_actions", "id", "item_builder", "item_spacing", "items", "layout_mode", "layout_provider", "line_separator", "line_spacing", "longtap_actions", "margins", "orientation", "paddings", "press_end_actions", "press_start_actions", "reuse_id", "row_span", "selected_actions", "separator", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &[])?;
    register_entity_class(py, m, "DivContainerSeparator", None,
        &["margins", "show_at_end", "show_at_start", "show_between", "style"], &["style"])?;
    register_entity_class(py, m, "DivCornersRadius", None,
        &["bottom_left", "bottom_right", "top_left", "top_right"], &[])?;
    register_entity_class(py, m, "DivCurrencyInputMask", Some("currency"),
        &["locale", "raw_text_variable"], &["raw_text_variable"])?;
    register_entity_class(py, m, "DivCustom", Some("custom"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "custom_props", "custom_type", "disappear_actions", "extensions", "focus", "functions", "height", "id", "items", "layout_provider", "margins", "paddings", "reuse_id", "row_span", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["custom_type"])?;
    register_entity_class(py, m, "DivData", None,
        &["functions", "log_id", "states", "timers", "transition_animation_selector", "variable_triggers", "variables"], &["log_id", "states"])?;
    register_entity_class(py, m, "DivDataState", None,
        &["div", "state_id"], &["div", "state_id"])?;
    register_entity_class(py, m, "DivDefaultIndicatorItemPlacement", Some("default"),
        &["space_between_centers"], &[])?;
    register_entity_class(py, m, "DivDimension", None,
        &["unit", "value"], &["value"])?;
    register_entity_class(py, m, "DivDisappearAction", None,
        &["disappear_duration", "download_callbacks", "is_enabled", "log_id", "log_limit", "payload", "referer", "scope_id", "typed", "url", "visibility_percentage"], &["log_id"])?;
    register_entity_class(py, m, "DivDownloadCallbacks", None,
        &["on_fail_actions", "on_success_actions"], &[])?;
    register_entity_class(py, m, "DivEdgeInsets", None,
        &["bottom", "end", "left", "right", "start", "top", "unit"], &[])?;
    register_entity_class(py, m, "DivExtension", None,
        &["id", "params"], &["id"])?;
    register_entity_class(py, m, "DivFadeTransition", Some("fade"),
        &["alpha", "duration", "interpolator", "start_delay"], &[])?;
    register_entity_class(py, m, "DivFilterRtlMirror", Some("rtl_mirror"),
        &[], &[])?;
    register_entity_class(py, m, "DivFixedCount", Some("fixed"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivFixedLengthInputMask", Some("fixed_length"),
        &["always_visible", "pattern", "pattern_elements", "raw_text_variable"], &["pattern", "pattern_elements", "raw_text_variable"])?;
    register_entity_class(py, m, "DivFixedLengthInputMaskPatternElement", None,
        &["key", "placeholder", "regex"], &["key"])?;
    register_entity_class(py, m, "DivFixedSize", Some("fixed"),
        &["unit", "value"], &["value"])?;
    register_entity_class(py, m, "DivFixedTranslation", Some("translation-fixed"),
        &["unit", "value"], &["value"])?;
    register_entity_class(py, m, "DivFocus", None,
        &["background", "border", "next_focus_ids", "on_blur", "on_focus"], &[])?;
    register_entity_class(py, m, "DivFocusNextFocusIds", None,
        &["down", "forward", "left", "right", "up"], &[])?;
    register_entity_class(py, m, "DivFunction", None,
        &["arguments", "body", "name", "return_type"], &["arguments", "body", "name", "return_type"])?;
    register_entity_class(py, m, "DivFunctionArgument", None,
        &["name", "type"], &["name", "type"])?;
    register_entity_class(py, m, "DivGallery", Some("gallery"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_count", "column_span", "cross_content_alignment", "cross_spacing", "default_item", "disappear_actions", "extensions", "focus", "functions", "height", "id", "item_builder", "item_spacing", "items", "layout_provider", "margins", "orientation", "paddings", "restrict_parent_scroll", "reuse_id", "row_span", "scroll_mode", "scrollbar", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &[])?;
    register_entity_class(py, m, "DivGifImage", Some("gif"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "aspect", "background", "border", "capture_focus_on_action", "column_span", "content_alignment_horizontal", "content_alignment_vertical", "disappear_actions", "doubletap_actions", "extensions", "focus", "functions", "gif_url", "height", "hover_end_actions", "hover_start_actions", "id", "layout_provider", "longtap_actions", "margins", "paddings", "placeholder_color", "preload_required", "press_end_actions", "press_start_actions", "preview", "preview_url", "reuse_id", "row_span", "scale", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["gif_url"])?;
    register_entity_class(py, m, "DivGrid", Some("grid"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "capture_focus_on_action", "column_count", "column_span", "content_alignment_horizontal", "content_alignment_vertical", "disappear_actions", "doubletap_actions", "extensions", "focus", "functions", "height", "hover_end_actions", "hover_start_actions", "id", "items", "layout_provider", "longtap_actions", "margins", "paddings", "press_end_actions", "press_start_actions", "reuse_id", "row_span", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["column_count"])?;
    register_entity_class(py, m, "DivImage", Some("image"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "appearance_animation", "aspect", "background", "border", "capture_focus_on_action", "column_span", "content_alignment_horizontal", "content_alignment_vertical", "disappear_actions", "doubletap_actions", "extensions", "filters", "focus", "functions", "height", "high_priority_preview_show", "hover_end_actions", "hover_start_actions", "id", "image_url", "layout_provider", "longtap_actions", "margins", "paddings", "placeholder_color", "preload_required", "press_end_actions", "press_start_actions", "preview", "reuse_id", "row_span", "scale", "selected_actions", "tint_color", "tint_mode", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["image_url"])?;
    register_entity_class(py, m, "DivImageBackground", Some("image"),
        &["alpha", "content_alignment_horizontal", "content_alignment_vertical", "filters", "image_url", "preload_required", "scale"], &["image_url"])?;
    register_entity_class(py, m, "DivIndicator", Some("indicator"),
        &["accessibility", "active_item_color", "active_item_size", "active_shape", "alignment_horizontal", "alignment_vertical", "alpha", "animation", "animators", "background", "border", "column_span", "disappear_actions", "extensions", "focus", "functions", "height", "id", "inactive_item_color", "inactive_minimum_shape", "inactive_shape", "items_placement", "layout_provider", "margins", "minimum_item_size", "paddings", "pager_id", "reuse_id", "row_span", "selected_actions", "shape", "space_between_centers", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &[])?;
    register_entity_class(py, m, "DivInfinityCount", Some("infinity"),
        &[], &[])?;
    register_entity_class(py, m, "DivInput", Some("input"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "autocapitalization", "background", "border", "column_span", "disappear_actions", "enter_key_actions", "enter_key_type", "extensions", "filters", "focus", "font_family", "font_size", "font_size_unit", "font_variation_settings", "font_weight", "font_weight_value", "functions", "height", "highlight_color", "hint_color", "hint_text", "id", "is_enabled", "keyboard_type", "layout_provider", "letter_spacing", "line_height", "margins", "mask", "max_length", "max_visible_lines", "native_interface", "paddings", "reuse_id", "row_span", "select_all_on_focus", "selected_actions", "text_alignment_horizontal", "text_alignment_vertical", "text_color", "text_variable", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "validators", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["text_variable"])?;
    register_entity_class(py, m, "DivInputNativeInterface", None,
        &["color"], &["color"])?;
    register_entity_class(py, m, "DivInputFilterExpression", Some("expression"),
        &["condition"], &["condition"])?;
    register_entity_class(py, m, "DivInputFilterRegex", Some("regex"),
        &["pattern"], &["pattern"])?;
    register_entity_class(py, m, "DivInputMaskBase", None,
        &["raw_text_variable"], &["raw_text_variable"])?;
    register_entity_class(py, m, "DivInputValidatorBase", None,
        &["allow_empty", "label_id", "variable"], &[])?;
    register_entity_class(py, m, "DivInputValidatorExpression", Some("expression"),
        &["allow_empty", "condition", "label_id", "variable"], &["condition", "label_id", "variable"])?;
    register_entity_class(py, m, "DivInputValidatorRegex", Some("regex"),
        &["allow_empty", "label_id", "pattern", "variable"], &["label_id", "pattern", "variable"])?;
    register_entity_class(py, m, "DivLayoutProvider", None,
        &["height_variable_name", "width_variable_name"], &[])?;
    register_entity_class(py, m, "DivLinearGradient", Some("gradient"),
        &["angle", "color_map", "colors"], &[])?;
    register_entity_class(py, m, "DivLinearGradientColorPoint", None,
        &["color", "position"], &["color", "position"])?;
    register_entity_class(py, m, "DivMatchParentSize", Some("match_parent"),
        &["max_size", "min_size", "weight"], &[])?;
    register_entity_class(py, m, "DivNeighbourPageSize", Some("fixed"),
        &["neighbour_page_width"], &["neighbour_page_width"])?;
    register_entity_class(py, m, "DivNinePatchBackground", Some("nine_patch_image"),
        &["image_url", "insets"], &["image_url", "insets"])?;
    register_entity_class(py, m, "DivNumberAnimator", Some("number_animator"),
        &["cancel_actions", "direction", "duration", "end_actions", "end_value", "id", "interpolator", "repeat_count", "start_delay", "start_value", "variable_name"], &["duration", "end_value", "id", "variable_name"])?;
    register_entity_class(py, m, "DivPageContentSize", Some("wrap_content"),
        &[], &[])?;
    register_entity_class(py, m, "DivPageSize", Some("percentage"),
        &["page_width"], &["page_width"])?;
    register_entity_class(py, m, "DivPageTransformationOverlap", Some("overlap"),
        &["interpolator", "next_page_alpha", "next_page_scale", "previous_page_alpha", "previous_page_scale", "reversed_stacking_order"], &[])?;
    register_entity_class(py, m, "DivPageTransformationSlide", Some("slide"),
        &["interpolator", "next_page_alpha", "next_page_scale", "previous_page_alpha", "previous_page_scale"], &[])?;
    register_entity_class(py, m, "DivPager", Some("pager"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "cross_axis_alignment", "default_item", "disappear_actions", "extensions", "focus", "functions", "height", "id", "infinite_scroll", "item_builder", "item_spacing", "items", "layout_mode", "layout_provider", "margins", "orientation", "paddings", "page_transformation", "restrict_parent_scroll", "reuse_id", "row_span", "scroll_axis_alignment", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["layout_mode"])?;
    register_entity_class(py, m, "DivPatch", None,
        &["changes", "mode", "on_applied_actions", "on_failed_actions"], &["changes"])?;
    register_entity_class(py, m, "DivPatchChange", None,
        &["id", "items"], &["id"])?;
    register_entity_class(py, m, "DivPercentageSize", Some("percentage"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivPercentageTranslation", Some("translation-percentage"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivPhoneInputMask", Some("phone"),
        &["raw_text_variable"], &["raw_text_variable"])?;
    register_entity_class(py, m, "DivPivotFixed", Some("pivot-fixed"),
        &["unit", "value"], &[])?;
    register_entity_class(py, m, "DivPivotPercentage", Some("pivot-percentage"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivPoint", None,
        &["x", "y"], &["x", "y"])?;
    register_entity_class(py, m, "DivRadialGradient", Some("radial_gradient"),
        &["center_x", "center_y", "color_map", "colors", "radius"], &[])?;
    register_entity_class(py, m, "DivRadialGradientColorPoint", None,
        &["color", "position"], &["color", "position"])?;
    register_entity_class(py, m, "DivRadialGradientFixedCenter", Some("fixed"),
        &["unit", "value"], &["value"])?;
    register_entity_class(py, m, "DivRadialGradientRelativeCenter", Some("relative"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivRadialGradientRelativeRadius", Some("relative"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "DivRotationTransformation", Some("rotation"),
        &["angle", "pivot_x", "pivot_y"], &["angle"])?;
    register_entity_class(py, m, "DivRoundedRectangleShape", Some("rounded_rectangle"),
        &["background_color", "corner_radius", "item_height", "item_width", "stroke"], &[])?;
    register_entity_class(py, m, "DivScaleTransition", Some("scale"),
        &["duration", "interpolator", "pivot_x", "pivot_y", "scale", "start_delay"], &[])?;
    register_entity_class(py, m, "DivSelect", Some("select"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "disappear_actions", "extensions", "focus", "font_family", "font_size", "font_size_unit", "font_variation_settings", "font_weight", "font_weight_value", "functions", "height", "hint_color", "hint_text", "id", "layout_provider", "letter_spacing", "line_height", "margins", "options", "paddings", "reuse_id", "row_span", "selected_actions", "text_color", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "value_variable", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["options", "value_variable"])?;
    register_entity_class(py, m, "DivSelectOption", None,
        &["text", "value"], &["value"])?;
    register_entity_class(py, m, "DivSeparator", Some("separator"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "capture_focus_on_action", "column_span", "delimiter_style", "disappear_actions", "doubletap_actions", "extensions", "focus", "functions", "height", "hover_end_actions", "hover_start_actions", "id", "layout_provider", "longtap_actions", "margins", "paddings", "press_end_actions", "press_start_actions", "reuse_id", "row_span", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &[])?;
    register_entity_class(py, m, "DivSeparatorDelimiterStyle", None,
        &["color", "orientation"], &[])?;
    register_entity_class(py, m, "DivShadow", None,
        &["alpha", "blur", "color", "offset"], &["offset"])?;
    register_entity_class(py, m, "DivShapeDrawable", Some("shape_drawable"),
        &["color", "shape", "stroke"], &["color", "shape"])?;
    register_entity_class(py, m, "DivSightAction", None,
        &["download_callbacks", "is_enabled", "log_id", "log_limit", "payload", "referer", "scope_id", "typed", "url"], &["log_id"])?;
    register_entity_class(py, m, "DivSizeUnitValue", None,
        &["unit", "value"], &["value"])?;
    register_entity_class(py, m, "DivSlideTransition", Some("slide"),
        &["distance", "duration", "edge", "interpolator", "start_delay"], &[])?;
    register_entity_class(py, m, "DivSlider", Some("slider"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "disappear_actions", "extensions", "focus", "functions", "height", "id", "is_enabled", "layout_provider", "margins", "max_value", "min_value", "paddings", "ranges", "reuse_id", "row_span", "secondary_value_accessibility", "selected_actions", "thumb_secondary_style", "thumb_secondary_text_style", "thumb_secondary_value_variable", "thumb_style", "thumb_text_style", "thumb_value_variable", "tick_mark_active_style", "tick_mark_inactive_style", "tooltips", "track_active_style", "track_inactive_style", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["thumb_style", "track_active_style", "track_inactive_style"])?;
    register_entity_class(py, m, "DivSliderTextStyle", None,
        &["font_family", "font_size", "font_size_unit", "font_variation_settings", "font_weight", "font_weight_value", "letter_spacing", "offset", "text_color"], &["font_size"])?;
    register_entity_class(py, m, "DivSliderRange", None,
        &["end", "margins", "start", "track_active_style", "track_inactive_style"], &[])?;
    register_entity_class(py, m, "DivSolidBackground", Some("solid"),
        &["color"], &["color"])?;
    register_entity_class(py, m, "DivState", Some("state"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "capture_focus_on_action", "clip_to_bounds", "column_span", "default_state_id", "disappear_actions", "div_id", "doubletap_actions", "extensions", "focus", "functions", "height", "hover_end_actions", "hover_start_actions", "id", "layout_provider", "longtap_actions", "margins", "paddings", "press_end_actions", "press_start_actions", "reuse_id", "row_span", "selected_actions", "state_id_variable", "states", "tooltips", "transform", "transformations", "transition_animation_selector", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["states"])?;
    register_entity_class(py, m, "DivStateState", None,
        &["animation_in", "animation_out", "div", "state_id", "swipe_out_actions"], &["state_id"])?;
    register_entity_class(py, m, "DivStretchIndicatorItemPlacement", Some("stretch"),
        &["item_spacing", "max_visible_items"], &[])?;
    register_entity_class(py, m, "DivStroke", None,
        &["color", "style", "unit", "width"], &["color"])?;
    register_entity_class(py, m, "DivStrokeStyleDashed", Some("dashed"),
        &[], &[])?;
    register_entity_class(py, m, "DivStrokeStyleSolid", Some("solid"),
        &[], &[])?;
    register_entity_class(py, m, "DivSwitch", Some("switch"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "disappear_actions", "extensions", "focus", "functions", "height", "id", "is_enabled", "is_on_variable", "layout_provider", "margins", "on_color", "paddings", "reuse_id", "row_span", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["is_on_variable"])?;
    register_entity_class(py, m, "DivTabs", Some("tabs"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "background", "border", "column_span", "disappear_actions", "dynamic_height", "extensions", "focus", "functions", "has_separator", "height", "id", "items", "layout_provider", "margins", "paddings", "restrict_parent_scroll", "reuse_id", "row_span", "selected_actions", "selected_tab", "separator_color", "separator_paddings", "switch_tabs_by_content_swipe_enabled", "tab_title_delimiter", "tab_title_style", "title_paddings", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["items"])?;
    register_entity_class(py, m, "DivTabsTabTitleStyle", None,
        &["active_background_color", "active_font_variation_settings", "active_font_weight", "active_text_color", "animation_duration", "animation_type", "corner_radius", "corners_radius", "font_family", "font_size", "font_size_unit", "font_weight", "inactive_background_color", "inactive_font_variation_settings", "inactive_font_weight", "inactive_text_color", "item_spacing", "letter_spacing", "line_height", "paddings"], &[])?;
    register_entity_class(py, m, "DivTabsTabTitleDelimiter", None,
        &["height", "image_url", "width"], &["image_url"])?;
    register_entity_class(py, m, "DivTabsItem", None,
        &["div", "title", "title_click_action"], &["div", "title"])?;
    register_entity_class(py, m, "DivText", Some("text"),
        &["accessibility", "action", "action_animation", "actions", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "auto_ellipsize", "background", "border", "capture_focus_on_action", "column_span", "disappear_actions", "doubletap_actions", "ellipsis", "extensions", "focus", "focused_text_color", "font_family", "font_feature_settings", "font_size", "font_size_unit", "font_variation_settings", "font_weight", "font_weight_value", "functions", "height", "hover_end_actions", "hover_start_actions", "id", "images", "layout_provider", "letter_spacing", "line_height", "longtap_actions", "margins", "max_lines", "min_hidden_lines", "paddings", "press_end_actions", "press_start_actions", "ranges", "reuse_id", "row_span", "selectable", "selected_actions", "strike", "text", "text_alignment_horizontal", "text_alignment_vertical", "text_color", "text_gradient", "text_shadow", "tighten_width", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "truncate", "underline", "variable_triggers", "variables", "visibility", "visibility_action", "visibility_actions", "width"], &["text"])?;
    register_entity_class(py, m, "DivTextRange", None,
        &["actions", "alignment_vertical", "background", "baseline_offset", "border", "end", "font_family", "font_feature_settings", "font_size", "font_size_unit", "font_variation_settings", "font_weight", "font_weight_value", "letter_spacing", "line_height", "mask", "start", "strike", "text_color", "text_shadow", "top_offset", "underline"], &[])?;
    register_entity_class(py, m, "DivTextImage", None,
        &["accessibility", "alignment_vertical", "height", "indexing_direction", "preload_required", "start", "tint_color", "tint_mode", "url", "width"], &["start", "url"])?;
    register_entity_class(py, m, "ImageAccessibility", None,
        &["description", "type"], &[])?;
    register_entity_class(py, m, "DivTextEllipsis", None,
        &["actions", "images", "ranges", "text"], &["text"])?;
    register_entity_class(py, m, "DivTextRangeBorder", None,
        &["corner_radius", "stroke"], &[])?;
    register_entity_class(py, m, "DivTextRangeMaskBase", None,
        &["is_enabled"], &[])?;
    register_entity_class(py, m, "DivTextRangeMaskParticles", Some("particles"),
        &["color", "density", "is_animated", "is_enabled", "particle_size"], &["color"])?;
    register_entity_class(py, m, "DivTextRangeMaskSolid", Some("solid"),
        &["color", "is_enabled"], &["color"])?;
    register_entity_class(py, m, "DivTimer", None,
        &["duration", "end_actions", "id", "tick_actions", "tick_interval", "value_variable"], &["id"])?;
    register_entity_class(py, m, "DivTooltip", None,
        &["animation_in", "animation_out", "background_accessibility_description", "bring_to_top_id", "close_by_tap_outside", "div", "duration", "id", "mode", "offset", "position", "substrate_div", "tap_outside_actions"], &["div", "id", "position"])?;
    register_entity_class(py, m, "DivTooltipModeModal", Some("modal"),
        &[], &[])?;
    register_entity_class(py, m, "DivTooltipModeNonModal", Some("non_modal"),
        &[], &[])?;
    register_entity_class(py, m, "DivTransform", None,
        &["pivot_x", "pivot_y", "rotation"], &[])?;
    register_entity_class(py, m, "DivTransitionBase", None,
        &["duration", "interpolator", "start_delay"], &[])?;
    register_entity_class(py, m, "DivTranslationTransformation", Some("translation"),
        &["x", "y"], &[])?;
    register_entity_class(py, m, "DivTrigger", None,
        &["actions", "condition", "mode"], &["actions", "condition"])?;
    register_entity_class(py, m, "DivVideo", Some("video"),
        &["accessibility", "alignment_horizontal", "alignment_vertical", "alpha", "animators", "aspect", "autostart", "background", "border", "buffering_actions", "column_span", "disappear_actions", "elapsed_time_variable", "end_actions", "extensions", "fatal_actions", "focus", "functions", "height", "id", "layout_provider", "margins", "muted", "paddings", "pause_actions", "player_settings_payload", "preload_required", "preview", "repeatable", "resume_actions", "reuse_id", "row_span", "scale", "selected_actions", "tooltips", "transform", "transformations", "transition_change", "transition_in", "transition_out", "transition_triggers", "variable_triggers", "variables", "video_sources", "visibility", "visibility_action", "visibility_actions", "width"], &["video_sources"])?;
    register_entity_class(py, m, "DivVideoSource", Some("video_source"),
        &["bitrate", "mime_type", "resolution", "url"], &["mime_type", "url"])?;
    register_entity_class(py, m, "DivVideoSourceResolution", Some("resolution"),
        &["height", "width"], &["height", "width"])?;
    register_entity_class(py, m, "DivVisibilityAction", None,
        &["download_callbacks", "is_enabled", "log_id", "log_limit", "payload", "referer", "scope_id", "typed", "url", "visibility_duration", "visibility_percentage"], &["log_id"])?;
    register_entity_class(py, m, "DivWrapContentSize", Some("wrap_content"),
        &["constrained", "max_size", "min_size"], &[])?;
    register_entity_class(py, m, "EndDestination", Some("end"),
        &[], &[])?;
    register_entity_class(py, m, "IndexDestination", Some("index"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "IntegerValue", Some("integer"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "IntegerVariable", Some("integer"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "NumberValue", Some("number"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "NumberVariable", Some("number"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "OffsetDestination", Some("offset"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "PropertyVariable", Some("property"),
        &["get", "name", "new_value_variable_name", "set", "value_type"], &["get", "name", "value_type"])?;
    register_entity_class(py, m, "StartDestination", Some("start"),
        &[], &[])?;
    register_entity_class(py, m, "StringValue", Some("string"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "StringVariable", Some("string"),
        &["name", "value"], &["name", "value"])?;
    register_entity_class(py, m, "UrlValue", Some("url"),
        &["value"], &["value"])?;
    register_entity_class(py, m, "UrlVariable", Some("url"),
        &["name", "value"], &["name", "value"])?;
    Ok(())
}

/// Register enum types as real `str, enum.Enum` subclasses.
///
/// This makes enums:
/// - Callable as constructors: `DivFontWeight("bold")` works
/// - Support `isinstance()`: `isinstance(DivFontWeight.BOLD, DivFontWeight)` is True
/// - Usable as Pydantic type annotations
/// - Compatible with pattern matching
///
/// Backward compatibility: `DivFontWeight.BOLD == "bold"` still holds because
/// these are `str` subclasses.
pub fn register_enums(py: Python<'_>, m: &Bound<'_, PyModule>) -> PyResult<()> {
    let enums: &[(&str, &[(&str, &str)])] = &[
        ("DivAlignmentHorizontal", &[("LEFT", "left"), ("CENTER", "center"), ("RIGHT", "right"), ("START", "start"), ("END", "end")]),
        ("DivAlignmentVertical", &[("TOP", "top"), ("CENTER", "center"), ("BOTTOM", "bottom"), ("BASELINE", "baseline")]),
        ("DivAnimationDirection", &[("NORMAL", "normal"), ("REVERSE", "reverse"), ("ALTERNATE", "alternate"), ("ALTERNATE_REVERSE", "alternate_reverse")]),
        ("DivAnimationInterpolator", &[("LINEAR", "linear"), ("EASE", "ease"), ("EASE_IN", "ease_in"), ("EASE_OUT", "ease_out"), ("EASE_IN_OUT", "ease_in_out"), ("SPRING", "spring")]),
        ("DivBlendMode", &[("SOURCE_IN", "source_in"), ("SOURCE_ATOP", "source_atop"), ("DARKEN", "darken"), ("LIGHTEN", "lighten"), ("MULTIPLY", "multiply"), ("SCREEN", "screen")]),
        ("DivContentAlignmentHorizontal", &[("LEFT", "left"), ("CENTER", "center"), ("RIGHT", "right"), ("START", "start"), ("END", "end"), ("SPACE_BETWEEN", "space-between"), ("SPACE_AROUND", "space-around"), ("SPACE_EVENLY", "space-evenly")]),
        ("DivContentAlignmentVertical", &[("TOP", "top"), ("CENTER", "center"), ("BOTTOM", "bottom"), ("BASELINE", "baseline"), ("SPACE_BETWEEN", "space-between"), ("SPACE_AROUND", "space-around"), ("SPACE_EVENLY", "space-evenly")]),
        ("DivEvaluableType", &[("STRING", "string"), ("INTEGER", "integer"), ("NUMBER", "number"), ("BOOLEAN", "boolean"), ("DATETIME", "datetime"), ("COLOR", "color"), ("URL", "url"), ("DICT", "dict"), ("ARRAY", "array")]),
        ("DivFontWeight", &[("LIGHT", "light"), ("MEDIUM", "medium"), ("REGULAR", "regular"), ("BOLD", "bold")]),
        ("DivImageScale", &[("FILL", "fill"), ("NO_SCALE", "no_scale"), ("FIT", "fit"), ("STRETCH", "stretch")]),
        ("DivLineStyle", &[("NONE", "none"), ("SINGLE", "single")]),
        ("DivSizeUnit", &[("DP", "dp"), ("SP", "sp"), ("PX", "px")]),
        ("DivTextAlignmentVertical", &[("TOP", "top"), ("CENTER", "center"), ("BOTTOM", "bottom"), ("BASELINE", "baseline")]),
        ("DivTransitionSelector", &[("NONE", "none"), ("DATA_CHANGE", "data_change"), ("STATE_CHANGE", "state_change"), ("ANY_CHANGE", "any_change")]),
        ("DivTransitionTrigger", &[("DATA_CHANGE", "data_change"), ("STATE_CHANGE", "state_change"), ("VISIBILITY_CHANGE", "visibility_change")]),
        ("DivVideoScale", &[("FILL", "fill"), ("NO_SCALE", "no_scale"), ("FIT", "fit")]),
        ("DivVisibility", &[("VISIBLE", "visible"), ("INVISIBLE", "invisible"), ("GONE", "gone")]),
        ("DivAccessibilityType", &[("NONE", "none"), ("BUTTON", "button"), ("IMAGE", "image"), ("TEXT", "text"), ("EDIT_TEXT", "edit_text"), ("HEADER", "header"), ("TAB_BAR", "tab_bar"), ("LIST", "list"), ("SELECT", "select"), ("CHECKBOX", "checkbox"), ("RADIO", "radio"), ("AUTO", "auto")]),
        ("DivAccessibilityMode", &[("DEFAULT", "default"), ("MERGE", "merge"), ("EXCLUDE", "exclude")]),
        ("DivActionTarget", &[("SELF", "_self"), ("BLANK", "_blank")]),
        ("DivActionScrollByOverflow", &[("CLAMP", "clamp"), ("RING", "ring")]),
        ("RequestMethod", &[("GET", "get"), ("POST", "post"), ("PUT", "put"), ("PATCH", "patch"), ("DELETE", "delete"), ("HEAD", "head"), ("OPTIONS", "options")]),
        ("DivActionTimerAction", &[("START", "start"), ("STOP", "stop"), ("PAUSE", "pause"), ("RESUME", "resume"), ("CANCEL", "cancel"), ("RESET", "reset")]),
        ("DivActionVideoAction", &[("START", "start"), ("PAUSE", "pause")]),
        ("DivAnimationName", &[("FADE", "fade"), ("TRANSLATE", "translate"), ("SCALE", "scale"), ("NATIVE", "native"), ("SET", "set"), ("NO_ANIMATION", "no_animation")]),
        ("DivContainerOrientation", &[("VERTICAL", "vertical"), ("HORIZONTAL", "horizontal"), ("OVERLAP", "overlap")]),
        ("DivContainerLayoutMode", &[("NO_WRAP", "no_wrap"), ("WRAP", "wrap")]),
        ("DivGalleryCrossContentAlignment", &[("START", "start"), ("CENTER", "center"), ("END", "end")]),
        ("DivGalleryScrollMode", &[("PAGING", "paging"), ("DEFAULT", "default")]),
        ("DivGalleryOrientation", &[("HORIZONTAL", "horizontal"), ("VERTICAL", "vertical")]),
        ("DivGalleryScrollbar", &[("NONE", "none"), ("AUTO", "auto")]),
        ("DivIndicatorAnimation", &[("SCALE", "scale"), ("WORM", "worm"), ("SLIDER", "slider")]),
        ("DivInputAutocapitalization", &[("AUTO", "auto"), ("NONE", "none"), ("WORDS", "words"), ("SENTENCES", "sentences"), ("ALL_CHARACTERS", "all_characters")]),
        ("DivInputEnterKeyType", &[("DEFAULT", "default"), ("GO", "go"), ("SEARCH", "search"), ("SEND", "send"), ("DONE", "done")]),
        ("DivInputKeyboardType", &[("SINGLE_LINE_TEXT", "single_line_text"), ("MULTI_LINE_TEXT", "multi_line_text"), ("PHONE", "phone"), ("NUMBER", "number"), ("EMAIL", "email"), ("URI", "uri"), ("PASSWORD", "password")]),
        ("DivPagerItemAlignment", &[("START", "start"), ("CENTER", "center"), ("END", "end")]),
        ("DivPagerOrientation", &[("HORIZONTAL", "horizontal"), ("VERTICAL", "vertical")]),
        ("DivPatchMode", &[("TRANSACTIONAL", "transactional"), ("PARTIAL", "partial")]),
        ("DivRadialGradientRelativeRadiusValue", &[("NEAREST_CORNER", "nearest_corner"), ("FARTHEST_CORNER", "farthest_corner"), ("NEAREST_SIDE", "nearest_side"), ("FARTHEST_SIDE", "farthest_side")]),
        ("DelimiterStyleOrientation", &[("VERTICAL", "vertical"), ("HORIZONTAL", "horizontal")]),
        ("DivSlideTransitionEdge", &[("LEFT", "left"), ("TOP", "top"), ("RIGHT", "right"), ("BOTTOM", "bottom")]),
        ("TabTitleStyleAnimationType", &[("SLIDE", "slide"), ("FADE", "fade"), ("NONE", "none")]),
        ("AccessibilityType", &[("NONE", "none"), ("BUTTON", "button"), ("IMAGE", "image"), ("TEXT", "text"), ("AUTO", "auto")]),
        ("ImageIndexingDirection", &[("NORMAL", "normal"), ("REVERSED", "reversed")]),
        ("DivTextTruncate", &[("NONE", "none"), ("START", "start"), ("END", "end"), ("MIDDLE", "middle")]),
        ("DivTooltipPosition", &[("LEFT", "left"), ("TOP_LEFT", "top-left"), ("TOP", "top"), ("TOP_RIGHT", "top-right"), ("RIGHT", "right"), ("BOTTOM_RIGHT", "bottom-right"), ("BOTTOM", "bottom"), ("BOTTOM_LEFT", "bottom-left"), ("CENTER", "center")]),
        ("DivTriggerMode", &[("ON_CONDITION", "on_condition"), ("ON_VARIABLE", "on_variable")]),
    ];

    // Create shared namespace with base enum class.
    // _DivEnum is a str+Enum base that preserves backward-compatible __str__ behavior:
    //   str(DivFontWeight.BOLD) == "bold"  (not "DivFontWeight.BOLD")
    let ns = pyo3::types::PyDict::new(py);
    ns.set_item("_Enum", py.import("enum")?.getattr("Enum")?)?;
    
    let base_code = std::ffi::CString::new(concat!(
        "class _DivEnum(str, _Enum):\n",
        "    def __str__(self):\n",
        "        return self.value\n",
        "    def __format__(self, format_spec):\n",
        "        return str.__format__(self.value, format_spec)\n",
    ))
    .unwrap();
    py.run(&base_code, Some(&ns), Some(&ns))?;
    
    for (enum_name, variants) in enums {
        let variants_code = variants
            .iter()
            .map(|(name, val)| format!("    {} = '{}'", name, val))
            .collect::<Vec<_>>()
            .join("\n");
    
        let code = format!(
            "class {}(_DivEnum):\n    __module__ = 'divkit_rs._native'\n{}\n",
            enum_name, variants_code
        );
        let code_cstr = std::ffi::CString::new(code).unwrap();
        py.run(&code_cstr, Some(&ns), Some(&ns))?;
    
        let cls = ns.get_item(*enum_name)?.ok_or_else(|| {
            pyo3::exceptions::PyRuntimeError::new_err(format!(
                "Failed to create enum {}",
                enum_name
            ))
        })?;
        m.setattr(*enum_name, cls)?;
    }

    Ok(())
}

/// Register type aliases (Union types) on the module.
pub fn register_type_aliases(_py: Python<'_>, m: &Bound<'_, PyModule>) -> PyResult<()> {
    let entity_cls = m.getattr("PyDivEntity")?;

    m.setattr("Div", &entity_cls)?;
    m.setattr("DivActionCopyToClipboardContent", &entity_cls)?;
    m.setattr("DivActionScrollDestination", &entity_cls)?;
    m.setattr("DivActionTyped", &entity_cls)?;
    m.setattr("DivAnimator", &entity_cls)?;
    m.setattr("DivAppearanceTransition", &entity_cls)?;
    m.setattr("DivBackground", &entity_cls)?;
    m.setattr("DivChangeTransition", &entity_cls)?;
    m.setattr("DivCount", &entity_cls)?;
    m.setattr("DivDrawable", &entity_cls)?;
    m.setattr("DivFilter", &entity_cls)?;
    m.setattr("DivIndicatorItemPlacement", &entity_cls)?;
    m.setattr("DivInputFilter", &entity_cls)?;
    m.setattr("DivInputMask", &entity_cls)?;
    m.setattr("DivInputValidator", &entity_cls)?;
    m.setattr("DivPageTransformation", &entity_cls)?;
    m.setattr("DivPagerLayoutMode", &entity_cls)?;
    m.setattr("DivPivot", &entity_cls)?;
    m.setattr("DivRadialGradientCenter", &entity_cls)?;
    m.setattr("DivRadialGradientRadius", &entity_cls)?;
    m.setattr("DivShape", &entity_cls)?;
    m.setattr("DivSize", &entity_cls)?;
    m.setattr("DivStrokeStyle", &entity_cls)?;
    m.setattr("DivTextGradient", &entity_cls)?;
    m.setattr("DivTextRangeBackground", &entity_cls)?;
    m.setattr("DivTextRangeMask", &entity_cls)?;
    m.setattr("DivTooltipMode", &entity_cls)?;
    m.setattr("DivTransformation", &entity_cls)?;
    m.setattr("DivTranslation", &entity_cls)?;
    m.setattr("DivTypedValue", &entity_cls)?;
    m.setattr("DivVariable", &entity_cls)?;

    Ok(())
}
