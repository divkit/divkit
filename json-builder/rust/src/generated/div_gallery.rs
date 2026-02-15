// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Gallery. It contains a horizontal or vertical set of cards that can be scrolled.
    pub struct DivGallery {
        type_name: "gallery",
        fields: {
            accessibility: DivAccessibility => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivAccessibility".into()), description: Some("Accessibility settings."), default_value: None, constraints: Constraints::default() },
            alignment_horizontal: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["left".into(), "center".into(), "right".into(), "start".into(), "end".into()]), SchemaFieldType::Expr]), description: Some("Horizontal alignment of an element inside the parent element."), default_value: None, constraints: Constraints::default() },
            alignment_vertical: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["top".into(), "center".into(), "bottom".into(), "baseline".into()]), SchemaFieldType::Expr]), description: Some("Vertical alignment of an element inside the parent element."), default_value: None, constraints: Constraints::default() },
            alpha: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Sets transparency of the entire element: `0` — completely transparent, `1` — opaque."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            animators: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivColorAnimator".into()), SchemaFieldType::Ref("DivNumberAnimator".into())]))), description: Some("Declaration of animators that change variable values over time."), default_value: None, constraints: Constraints::default() },
            background: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivLinearGradient".into()), SchemaFieldType::Ref("DivRadialGradient".into()), SchemaFieldType::Ref("DivImageBackground".into()), SchemaFieldType::Ref("DivSolidBackground".into()), SchemaFieldType::Ref("DivNinePatchBackground".into())]))), description: Some("Element background. It can contain multiple layers."), default_value: None, constraints: Constraints::default() },
            border: DivBorder => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivBorder".into()), description: Some("Element stroke."), default_value: None, constraints: Constraints::default() },
            column_count: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Number of columns for block layout."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
            column_span: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Merges cells in a column of the [grid](div-grid.md) element."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            cross_content_alignment: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["start".into(), "center".into(), "end".into()]), SchemaFieldType::Expr]), description: Some("Aligning elements in the direction perpendicular to the scroll direction. In horizontal galleries:<li>`start` — alignment to the top of the card;</li><li>`center` — to the center;</li><li>`end` — to the bottom.</li></p><p>In vertical galleries:<li>`start` — alignment to the left of the card;</li><li>`center` — to the center;</li><li>`end` — to the right.</li>"), default_value: Some(serde_json::json!("start")), constraints: Constraints::default() },
            cross_spacing: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Spacing between elements across the scroll axis. By default, the value set to `item_spacing`."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            default_item: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Ordinal number of the gallery element to be scrolled to by default. For `scroll_mode`:<li>`default` — the scroll position is set to the beginning of the element, without taking into account `item_spacing`;</li><li>`paging` — the scroll position is set to the center of the element.</li>"), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            disappear_actions: Vec<DivDisappearAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivDisappearAction".into()))), description: Some("Actions when an element disappears from the screen."), default_value: None, constraints: Constraints::default() },
            extensions: Vec<DivExtension> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivExtension".into()))), description: Some("Extensions for additional processing of an element. The list of extensions is given in  [DivExtension](../../extensions)."), default_value: None, constraints: Constraints::default() },
            focus: DivFocus => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFocus".into()), description: Some("Parameters when focusing on an element or losing focus."), default_value: None, constraints: Constraints::default() },
            functions: Vec<DivFunction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivFunction".into()))), description: Some("User functions."), default_value: None, constraints: Constraints::default() },
            height: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivFixedSize".into()), SchemaFieldType::Ref("DivMatchParentSize".into()), SchemaFieldType::Ref("DivWrapContentSize".into())]), description: Some("Element height. For Android: if there is text in this or in a child element, specify height in `sp` to scale the element together with the text. To learn more about units of size measurement, see [Layout inside the card](../../layout)."), default_value: Some(serde_json::json!("{\"type\": \"wrap_content\"}")), constraints: Constraints::default() },
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Element ID. It must be unique within the root element. It is used as `accessibilityIdentifier` on iOS."), default_value: None, constraints: Constraints::default() },
            item_builder: DivCollectionItemBuilder => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivCollectionItemBuilder".into()), description: Some("Sets collection elements dynamically using `data` and `prototypes`."), default_value: None, constraints: Constraints::default() },
            item_spacing: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Spacing between elements."), default_value: Some(serde_json::json!(8)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            items: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]))), description: Some("Gallery elements. Scrolling to elements can be implemented using:<li>`div-action://set_current_item?id=&item=` — scrolling to the element with an ordinal number `item` inside an element, with the specified `id`;</li><li>`div-action://set_next_item?id=[&overflow={clamp\\|ring}]` — scrolling to the next element inside an element, with the specified `id`;</li><li>`div-action://set_previous_item?id=[&overflow={clamp\\|ring}]` — scrolling to the previous element inside an element, with the specified `id`.</li></p><p>The optional `overflow` parameter is used to set navigation when the first or last element is reached:<li>`clamp` — transition will stop at the border element;</li><li>`ring` — go to the beginning or end, depending on the current element.</li></p><p>By default, `clamp`."), default_value: None, constraints: Constraints::default() },
            layout_provider: DivLayoutProvider => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivLayoutProvider".into()), description: Some("Provides data on the actual size of the element."), default_value: None, constraints: Constraints::default() },
            margins: DivEdgeInsets => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivEdgeInsets".into()), description: Some("External margins from the element stroke."), default_value: None, constraints: Constraints::default() },
            orientation: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["horizontal".into(), "vertical".into()]), SchemaFieldType::Expr]), description: Some("Gallery orientation."), default_value: Some(serde_json::json!("horizontal")), constraints: Constraints::default() },
            paddings: DivEdgeInsets => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivEdgeInsets".into()), description: Some("Internal margins from the element stroke."), default_value: None, constraints: Constraints::default() },
            restrict_parent_scroll: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("If the parameter is enabled, the gallery won't transmit the scroll gesture to the parent element."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            reuse_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("ID for the div object structure. Used to optimize block reuse. See [block reuse](../../reuse/reuse.md)."), default_value: None, constraints: Constraints::default() },
            row_span: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Merges cells in a string of the [grid](div-grid.md) element."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            scroll_mode: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["paging".into(), "default".into()]), SchemaFieldType::Expr]), description: Some("Scroll type: `default` — continuous, `paging` — page-by-page."), default_value: Some(serde_json::json!("default")), constraints: Constraints::default() },
            scrollbar: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["none".into(), "auto".into()]), SchemaFieldType::Expr]), description: Some("Scrollbar behavior. Hidden by default. When choosing a gallery size, keep in mind that the scrollbar may have a different height and width depending on the platform and user settings. <li>`none` — the scrollbar is hidden.</li><li>`auto` — the scrollbar is shown if there isn't enough space and it needs to be displayed on the current platform.</li>"), default_value: Some(serde_json::json!("none")), constraints: Constraints::default() },
            selected_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("List of [actions](div-action.md) to be executed when selecting an element in [pager](div-pager.md)."), default_value: None, constraints: Constraints::default() },
            tooltips: Vec<DivTooltip> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivTooltip".into()))), description: Some("Tooltips linked to an element. A tooltip can be shown by `div-action://show_tooltip?id=`, hidden by `div-action://hide_tooltip?id=` where `id` — tooltip id."), default_value: None, constraints: Constraints::default() },
            transform: DivTransform => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivTransform".into()), description: Some("Applies the passed transformation to the element. Content that doesn't fit into the original view area is cut off."), default_value: None, constraints: Constraints::default() },
            transformations: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivRotationTransformation".into()), SchemaFieldType::Ref("DivTranslationTransformation".into())]))), description: Some("Array of transformations to be applied to the element in sequence."), default_value: None, constraints: Constraints::default() },
            transition_change: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivChangeSetTransition".into()), SchemaFieldType::Ref("DivChangeBoundsTransition".into())]), description: Some("Change animation. It is played when the position or size of an element changes in the new layout."), default_value: None, constraints: Constraints::default() },
            transition_in: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivAppearanceSetTransition".into()), SchemaFieldType::Ref("DivFadeTransition".into()), SchemaFieldType::Ref("DivScaleTransition".into()), SchemaFieldType::Ref("DivSlideTransition".into())]), description: Some("Appearance animation. It is played when an element with a new ID appears. To learn more about the concept of transitions, see [Animated transitions](../../interaction#animation/transition-animation)."), default_value: None, constraints: Constraints::default() },
            transition_out: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivAppearanceSetTransition".into()), SchemaFieldType::Ref("DivFadeTransition".into()), SchemaFieldType::Ref("DivScaleTransition".into()), SchemaFieldType::Ref("DivSlideTransition".into())]), description: Some("Disappearance animation. It is played when an element disappears in the new layout."), default_value: None, constraints: Constraints::default() },
            transition_triggers: Vec<String> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Enum(vec!["data_change".into(), "state_change".into(), "visibility_change".into()]))), description: Some("Animation starting triggers. Default value: `[state_change, visibility_change]`."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
            variable_triggers: Vec<DivTrigger> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivTrigger".into()))), description: Some("Triggers for changing variables within an element."), default_value: None, constraints: Constraints::default() },
            variables: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringVariable".into()), SchemaFieldType::Ref("NumberVariable".into()), SchemaFieldType::Ref("IntegerVariable".into()), SchemaFieldType::Ref("BooleanVariable".into()), SchemaFieldType::Ref("ColorVariable".into()), SchemaFieldType::Ref("UrlVariable".into()), SchemaFieldType::Ref("DictVariable".into()), SchemaFieldType::Ref("ArrayVariable".into()), SchemaFieldType::Ref("PropertyVariable".into())]))), description: Some("Declaration of variables that can be used within an element. Variables declared in this array can only be used within the element and its child elements."), default_value: None, constraints: Constraints::default() },
            visibility: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["visible".into(), "invisible".into(), "gone".into()]), SchemaFieldType::Expr]), description: Some("Element visibility."), default_value: Some(serde_json::json!("visible")), constraints: Constraints::default() },
            visibility_action: DivVisibilityAction => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivVisibilityAction".into()), description: Some("Tracking visibility of a single element. Not used if the `visibility_actions` parameter is set."), default_value: None, constraints: Constraints::default() },
            visibility_actions: Vec<DivVisibilityAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivVisibilityAction".into()))), description: Some("Actions when an element appears on the screen."), default_value: None, constraints: Constraints::default() },
            width: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivFixedSize".into()), SchemaFieldType::Ref("DivMatchParentSize".into()), SchemaFieldType::Ref("DivWrapContentSize".into())]), description: Some("Element width."), default_value: Some(serde_json::json!("{\"type\": \"match_parent\"}")), constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Aligning elements in the direction perpendicular to the scroll direction. In
    /// horizontal galleries:`start` — alignment to the top of the card;`center` — to the
    /// center;`end` — to the bottom.</p><p>In vertical galleries:`start` — alignment to
    /// the left of the card;`center` — to the center;`end` — to the right.
    pub enum DivGalleryCrossContentAlignment {
        Start => "start",
        Center => "center",
        End => "end",
    }
}

div_enum! {
    /// Gallery orientation.
    pub enum DivGalleryOrientation {
        Horizontal => "horizontal",
        Vertical => "vertical",
    }
}

div_enum! {
    /// Scroll type: `default` — continuous, `paging` — page-by-page.
    pub enum DivGalleryScrollMode {
        Paging => "paging",
        Default => "default",
    }
}

div_enum! {
    /// Scrollbar behavior. Hidden by default. When choosing a gallery size, keep in mind
    /// that the scrollbar may have a different height and width depending on the
    /// platform and user settings. `none` — the scrollbar is hidden.`auto` — the
    /// scrollbar is shown if there isn't enough space and it needs to be displayed on
    /// the current platform.
    pub enum DivGalleryScrollbar {
        None => "none",
        Auto => "auto",
    }
}
