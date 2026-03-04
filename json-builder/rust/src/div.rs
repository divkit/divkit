//! DivKit entity type definitions.
//!
//! These mirror the auto-generated Python types in `pydivkit.div`.
//! Each type uses the `div_entity!` macro to generate a struct with
//! `dict()`, `build()`, and `schema()` methods.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};

// ============================================================
// Enums
// ============================================================

div_enum! {
    pub enum DivAlignmentVertical {
        Top => "top",
        Center => "center",
        Bottom => "bottom",
        Baseline => "baseline",
    }
}

div_enum! {
    pub enum DivAlignmentHorizontal {
        Left => "left",
        Center => "center",
        Right => "right",
        Start => "start",
        End => "end",
    }
}

div_enum! {
    pub enum DivContainerOrientation {
        Vertical => "vertical",
        Horizontal => "horizontal",
        Overlap => "overlap",
    }
}

div_enum! {
    pub enum DivVisibility {
        Visible => "visible",
        Invisible => "invisible",
        Gone => "gone",
    }
}

div_enum! {
    pub enum DivFontWeight {
        Light => "light",
        Regular => "regular",
        Medium => "medium",
        Bold => "bold",
    }
}

div_enum! {
    pub enum DivLineStyle {
        None => "none",
        Single => "single",
    }
}

div_enum! {
    pub enum DivImageScale {
        Fill => "fill",
        NoScale => "no_scale",
        Fit => "fit",
        Stretch => "stretch",
    }
}

div_enum! {
    pub enum DivSizeUnit {
        Dp => "dp",
        Sp => "sp",
        Px => "px",
    }
}

div_enum! {
    pub enum DivGalleryScrollMode {
        Paging => "paging",
        Default => "default",
    }
}

div_enum! {
    pub enum DivGalleryCrossContentAlignment {
        Start => "start",
        Center => "center",
        End => "end",
    }
}

div_enum! {
    pub enum DivContentAlignmentHorizontal {
        Center => "center",
        End => "end",
        Left => "left",
        Right => "right",
        SpaceAround => "space_around",
        SpaceBetween => "space_between",
        SpaceEvenly => "space_evenly",
        Start => "start",
    }
}

div_enum! {
    pub enum DivContentAlignmentVertical {
        Baseline => "baseline",
        Bottom => "bottom",
        Center => "center",
        SpaceAround => "space_around",
        SpaceBetween => "space_between",
        SpaceEvenly => "space_evenly",
        Top => "top",
    }
}

div_enum! {
    pub enum DivBlendMode {
        Darken => "darken",
        Lighten => "lighten",
        Multiply => "multiply",
        Screen => "screen",
        SourceAtop => "source_atop",
        SourceIn => "source_in",
    }
}

div_enum! {
    pub enum DivAccessibilityMode {
        Default => "default",
        Exclude => "exclude",
        Merge => "merge",
    }
}

div_enum! {
    pub enum DivAccessibilityType {
        Auto => "auto",
        Button => "button",
        Checkbox => "checkbox",
        EditText => "edit_text",
        Header => "header",
        Image => "image",
        List => "list",
        None => "none",
        Radio => "radio",
        Select => "select",
        TabBar => "tab_bar",
        Text => "text",
    }
}

div_enum! {
    pub enum DivContainerLayoutMode {
        NoWrap => "no_wrap",
        Wrap => "wrap",
    }
}

div_enum! {
    pub enum DivAnimationInterpolator {
        Ease => "ease",
        EaseIn => "ease_in",
        EaseInOut => "ease_in_out",
        EaseOut => "ease_out",
        Linear => "linear",
        Spring => "spring",
    }
}

div_enum! {
    pub enum TabTitleStyleAnimationType {
        Fade => "fade",
        None => "none",
        Slide => "slide",
    }
}

// ============================================================
// Size types
// ============================================================

div_entity! {
    pub struct DivFixedSize {
        type_name: "fixed",
        fields: {
            #[required]
            value: i64,
            unit: String,
        }
    }
}

div_entity! {
    pub struct DivMatchParentSize {
        type_name: "match_parent",
        fields: {
            weight: f64,
        }
    }
}

div_entity! {
    pub struct DivWrapContentSize {
        type_name: "wrap_content",
        fields: {
            constrained: bool,
            max_size: DivFixedSize,
            min_size: DivFixedSize,
        }
    }
}

// ============================================================
// Basic component types
// ============================================================

div_entity! {
    pub struct DivEdgeInsets {
        fields: {
            left: i64,
            top: i64,
            right: i64,
            bottom: i64,
            start: i64,
            end: i64,
            unit: String,
        }
    }
}

div_entity! {
    pub struct DivBorder {
        fields: {
            corner_radius: i64,
            corners_radius: DivCornersRadius,
            has_shadow: bool,
            shadow: DivShadow,
            stroke: DivStroke,
        }
    }
}

div_entity! {
    pub struct DivCornersRadius {
        fields: {
            bottom_left: i64,
            bottom_right: i64,
            top_left: i64,
            top_right: i64,
        }
    }
}

div_entity! {
    pub struct DivShadow {
        fields: {
            alpha: f64,
            blur: i64,
            color: String,
            offset: DivPoint,
        }
    }
}

div_entity! {
    pub struct DivPoint {
        fields: {
            x: DivDimension,
            y: DivDimension,
        }
    }
}

div_entity! {
    pub struct DivDimension {
        fields: {
            value: f64,
            unit: String,
        }
    }
}

div_entity! {
    pub struct DivStroke {
        fields: {
            color: String,
            unit: String,
            width: i64,
        }
    }
}

div_entity! {
    pub struct DivAction {
        fields: {
            log_id: String,
            url: String,
            log_url: String,
            payload: String,
            typed: String,
        }
    }
}

// ============================================================
// Background types
// ============================================================

div_entity! {
    pub struct DivSolidBackground {
        type_name: "solid",
        fields: {
            #[required]
            color: String,
        }
    }
}

div_entity! {
    pub struct DivGradientBackground {
        type_name: "gradient",
        fields: {
            angle: i64,
            #[required]
            colors: Vec<String>,
        }
    }
}

div_entity! {
    pub struct DivImageBackground {
        type_name: "image",
        fields: {
            alpha: f64,
            content_alignment_horizontal: String,
            content_alignment_vertical: String,
            #[required]
            image_url: String,
            preload_required: bool,
            scale: String,
        }
    }
}

// ============================================================
// Shape types
// ============================================================

div_entity! {
    pub struct DivRoundedRectangleShape {
        type_name: "rounded_rectangle",
        fields: {
            corner_radius: DivFixedSize,
            item_height: DivFixedSize,
            item_width: DivFixedSize,
        }
    }
}

div_entity! {
    pub struct DivCircleShape {
        type_name: "circle",
        fields: {
            radius: DivFixedSize,
            background_color: String,
            stroke: DivStroke,
        }
    }
}

div_entity! {
    pub struct DivShapeDrawable {
        type_name: "shape_drawable",
        fields: {
            #[required]
            color: String,
            shape: DivRoundedRectangleShape,
            stroke: DivStroke,
        }
    }
}

// ============================================================
// Main Div components
// ============================================================

div_entity! {
    /// Text component.
    pub struct DivText {
        type_name: "text",
        fields: {
            text: String,
            font_size: i64,
            font_weight: String,
            font_family: String,
            text_color: String,
            text_alignment_horizontal: String,
            text_alignment_vertical: String,
            line_height: i64,
            max_lines: i64,
            letter_spacing: f64,
            strike: String,
            underline: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            alignment_horizontal: String,
            alignment_vertical: String,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            background: Vec<DivSolidBackground>,
            visibility: String,
            actions: Vec<DivAction>,
            action: DivAction,
            accessibility: DivAccessibility,
            id: String,
            column_span: i64,
            row_span: i64,
        }
    }
}

div_entity! {
    /// Image component.
    pub struct DivImage {
        type_name: "image",
        fields: {
            image_url: String,
            placeholder_color: String,
            scale: String,
            content_alignment_horizontal: String,
            content_alignment_vertical: String,
            preview: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            alignment_horizontal: String,
            alignment_vertical: String,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            background: Vec<DivSolidBackground>,
            visibility: String,
            actions: Vec<DivAction>,
            action: DivAction,
            accessibility: DivAccessibility,
            id: String,
            column_span: i64,
            row_span: i64,
        }
    }
}

div_entity! {
    /// Container component.
    pub struct DivContainer {
        type_name: "container",
        fields: {
            items: Vec<DivValue>,
            orientation: String,
            content_alignment_horizontal: String,
            content_alignment_vertical: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            alignment_horizontal: String,
            alignment_vertical: String,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            background: Vec<DivSolidBackground>,
            visibility: String,
            actions: Vec<DivAction>,
            action: DivAction,
            accessibility: DivAccessibility,
            id: String,
            column_span: i64,
            row_span: i64,
        }
    }
}

div_entity! {
    /// Gallery component.
    pub struct DivGallery {
        type_name: "gallery",
        fields: {
            #[required]
            items: Vec<DivValue>,
            scroll_mode: String,
            item_spacing: i64,
            cross_content_alignment: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            background: Vec<DivSolidBackground>,
            visibility: String,
            column_span: i64,
            row_span: i64,
        }
    }
}

div_entity! {
    /// Slider component.
    pub struct DivSlider {
        type_name: "slider",
        fields: {
            min_value: i64,
            max_value: i64,
            thumb_style: DivShapeDrawable,
            thumb_text_style: DivSliderTextStyle,
            track_active_style: DivShapeDrawable,
            track_inactive_style: DivShapeDrawable,
            thumb_secondary_style: DivShapeDrawable,
            thumb_secondary_text_style: DivSliderTextStyle,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            visibility: String,
        }
    }
}

div_entity! {
    pub struct DivSliderTextStyle {
        fields: {
            font_size: i64,
            font_weight: String,
            text_color: String,
            offset: DivPoint,
        }
    }
}

div_entity! {
    pub struct DivAccessibility {
        fields: {
            description: String,
            hint: String,
            mode: String,
            state_description: String,
            r#type: String,
        }
    }
}

div_entity! {
    /// GIF image component.
    pub struct DivGifImage {
        type_name: "gif",
        fields: {
            gif_url: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            accessibility: DivAccessibility,
        }
    }
}

div_entity! {
    /// Separator component.
    pub struct DivSeparator {
        type_name: "separator",
        fields: {
            delimiter_style: DivSeparatorDelimiterStyle,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            visibility: String,
        }
    }
}

div_entity! {
    pub struct DivSeparatorDelimiterStyle {
        fields: {
            color: String,
            orientation: String,
        }
    }
}

div_entity! {
    /// Input component.
    pub struct DivInput {
        type_name: "input",
        fields: {
            text_variable: String,
            font_size: i64,
            font_weight: String,
            text_color: String,
            hint_text: String,
            hint_color: String,
            highlight_color: String,
            line_height: i64,
            max_lines: i64,
            keyboard_type: String,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            visibility: String,
        }
    }
}

div_entity! {
    /// Pager component.
    pub struct DivPager {
        type_name: "pager",
        fields: {
            #[required]
            items: Vec<DivValue>,
            layout_mode: DivPagerLayoutMode,
            orientation: String,
            width: DivSize,
            height: DivSize,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
        }
    }
}

div_entity! {
    pub struct DivPagerLayoutMode {
        fields: {
            neighbour_page_width: DivFixedSize,
            page_width: DivPercentageSize,
        }
    }
}

div_entity! {
    pub struct DivPercentageSize {
        type_name: "percentage",
        fields: {
            #[required]
            value: f64,
        }
    }
}

div_entity! {
    /// Tabs component.
    pub struct DivTabs {
        type_name: "tabs",
        fields: {
            #[required]
            items: Vec<DivTabsItem>,
            selected_tab: i64,
            width: DivSize,
            height: DivSize,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
        }
    }
}

div_entity! {
    pub struct DivTabsItem {
        fields: {
            #[required]
            title: String,
            #[required]
            div: DivValue,
        }
    }
}

div_entity! {
    /// State component.
    pub struct DivState {
        type_name: "state",
        fields: {
            #[required]
            states: Vec<DivStateState>,
            div_id: String,
            default_state_id: String,
            width: DivSize,
            height: DivSize,
        }
    }
}

div_entity! {
    pub struct DivStateState {
        fields: {
            #[required]
            state_id: String,
            div: DivValue,
        }
    }
}

div_entity! {
    /// Custom component.
    pub struct DivCustom {
        type_name: "custom",
        fields: {
            #[required]
            custom_type: String,
            custom_props: String,
            width: DivSize,
            height: DivSize,
        }
    }
}

div_entity! {
    /// Indicator component.
    pub struct DivIndicator {
        type_name: "indicator",
        fields: {
            pager_id: String,
            active_item_color: String,
            inactive_item_color: String,
            space_between_centers: DivFixedSize,
            shape: DivRoundedRectangleShape,
            width: DivSize,
            height: DivSize,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
        }
    }
}

// ============================================================
// Animation types
// ============================================================

div_entity! {
    pub struct DivAnimation {
        type_name: "animation",
        fields: {
            #[required]
            name: String,
            duration: i64,
            start_delay: i64,
            interpolator: String,
            repeat_count: i64,
            end_value: f64,
            start_value: f64,
        }
    }
}

div_entity! {
    pub struct DivTransform {
        fields: {
            pivot_x: DivPivot,
            pivot_y: DivPivot,
            rotation: f64,
        }
    }
}

div_entity! {
    pub struct DivPivot {
        fields: {
            r#type: String,
            value: f64,
        }
    }
}

// ============================================================
// Tooltip
// ============================================================

div_entity! {
    pub struct DivTooltip {
        fields: {
            #[required]
            id: String,
            #[required]
            div: DivValue,
            #[required]
            position: String,
            duration: i64,
            offset: DivPoint,
        }
    }
}

// ============================================================
// Grid component
// ============================================================

div_entity! {
    /// Grid component.
    pub struct DivGrid {
        type_name: "grid",
        fields: {
            #[required]
            items: Vec<DivValue>,
            #[required]
            column_count: i64,
            width: DivSize,
            height: DivSize,
            alpha: f64,
            alignment_horizontal: String,
            alignment_vertical: String,
            paddings: DivEdgeInsets,
            margins: DivEdgeInsets,
            border: DivBorder,
            background: Vec<DivSolidBackground>,
            visibility: String,
            actions: Vec<DivAction>,
            action: DivAction,
            accessibility: DivAccessibility,
            id: String,
            column_span: i64,
            row_span: i64,
            content_alignment_horizontal: String,
            content_alignment_vertical: String,
        }
    }
}

// ============================================================
// Gradient types
// ============================================================

div_entity! {
    /// Linear gradient.
    pub struct DivLinearGradient {
        type_name: "gradient",
        fields: {
            angle: i64,
            color_map: DivValue,
            colors: Vec<String>,
        }
    }
}

// ============================================================
// Animator types
// ============================================================

div_entity! {
    /// Number animator.
    pub struct DivNumberAnimator {
        type_name: "number_animator",
        fields: {
            cancel_actions: Vec<DivAction>,
            direction: String,
            #[required]
            duration: i64,
            end_actions: Vec<DivAction>,
            #[required]
            end_value: f64,
            #[required]
            id: String,
            interpolator: String,
            repeat_count: i64,
            start_delay: i64,
            start_value: f64,
            #[required]
            variable_name: String,
        }
    }
}

// ============================================================
// Action types (typed actions)
// ============================================================

div_entity! {
    /// Start animator action.
    pub struct DivActionAnimatorStart {
        type_name: "animator_start",
        fields: {
            #[required]
            animator_id: String,
            direction: String,
            duration: i64,
            end_value: f64,
            interpolator: String,
            repeat_count: i64,
            start_delay: i64,
            start_value: f64,
        }
    }
}

div_entity! {
    /// Set variable action.
    pub struct DivActionSetVariable {
        type_name: "set_variable",
        fields: {
            #[required]
            value: DivValue,
            #[required]
            variable_name: String,
        }
    }
}

// ============================================================
// Variable types
// ============================================================

div_entity! {
    /// String variable.
    pub struct StringVariable {
        type_name: "string",
        fields: {
            #[required]
            name: String,
            #[required]
            value: String,
        }
    }
}

div_entity! {
    /// Number variable.
    pub struct NumberVariable {
        type_name: "number",
        fields: {
            #[required]
            name: String,
            #[required]
            value: f64,
        }
    }
}

div_entity! {
    /// Boolean variable.
    pub struct BooleanVariable {
        type_name: "boolean",
        fields: {
            #[required]
            name: String,
            #[required]
            value: bool,
        }
    }
}

// ============================================================
// Value types
// ============================================================

div_entity! {
    /// String value.
    pub struct StringValue {
        type_name: "string",
        fields: {
            #[required]
            value: String,
        }
    }
}

div_entity! {
    /// Boolean value.
    pub struct BooleanValue {
        type_name: "boolean",
        fields: {
            #[required]
            value: bool,
        }
    }
}

// ============================================================
// Page size
// ============================================================

div_entity! {
    /// Page size (percentage-based).
    pub struct DivPageSize {
        type_name: "percentage",
        fields: {
            #[required]
            page_width: DivPercentageSize,
        }
    }
}

// ============================================================
// Utility types
// ============================================================

div_entity! {
    /// Aspect ratio.
    pub struct DivAspect {
        fields: {
            #[required]
            ratio: f64,
        }
    }
}

div_entity! {
    /// Download callbacks.
    pub struct DivDownloadCallbacks {
        fields: {
            on_fail_actions: Vec<DivAction>,
            on_success_actions: Vec<DivAction>,
        }
    }
}

div_entity! {
    /// Extension.
    pub struct DivExtension {
        fields: {
            #[required]
            id: String,
            params: DivValue,
        }
    }
}

div_entity! {
    /// Visibility action.
    pub struct DivVisibilityAction {
        fields: {
            download_callbacks: DivDownloadCallbacks,
            is_enabled: bool,
            #[required]
            log_id: String,
            log_limit: i64,
            payload: DivValue,
            referer: String,
            scope_id: String,
            typed: DivValue,
            url: String,
            visibility_duration: i64,
            visibility_percentage: i64,
        }
    }
}

div_entity! {
    /// Trigger.
    pub struct DivTrigger {
        fields: {
            #[required]
            actions: Vec<DivAction>,
            #[required]
            condition: String,
            mode: String,
        }
    }
}

div_entity! {
    /// Timer.
    pub struct DivTimer {
        fields: {
            #[required]
            duration: i64,
            end_actions: Vec<DivAction>,
            #[required]
            id: String,
            tick_actions: Vec<DivAction>,
            tick_interval: i64,
            value_variable: String,
        }
    }
}

div_entity! {
    /// Tab title style.
    pub struct DivTabsTabTitleStyle {
        fields: {
            active_background_color: String,
            active_font_weight: String,
            active_text_color: String,
            animation_duration: i64,
            animation_type: String,
            corner_radius: i64,
            corners_radius: DivCornersRadius,
            font_family: String,
            font_size: i64,
            font_size_unit: String,
            font_weight: String,
            inactive_background_color: String,
            inactive_font_weight: String,
            inactive_text_color: String,
            item_spacing: i64,
            letter_spacing: f64,
            line_height: i64,
            paddings: DivEdgeInsets,
        }
    }
}
