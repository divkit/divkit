// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Tooltip.
    pub struct DivTooltip {
        fields: {
            animation_in: DivAnimation => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivAnimation".into()), description: Some("Tooltip appearance animation. By default, the tooltip will be appearing gradually with an offset from the anchor point by 10 dp."), default_value: None, constraints: Constraints::default() },
            animation_out: DivAnimation => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivAnimation".into()), description: Some("Tooltip disappearance animation. By default, the tooltip will disappear gradually with an offset from the anchor point by 10 dp."), default_value: None, constraints: Constraints::default() },
            background_accessibility_description: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Description for accessibility of the tap action on the background of the tooltip."), default_value: None, constraints: Constraints::default() },
            bring_to_top_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("An element that will be brought to the top of the substrate."), default_value: None, constraints: Constraints::default() },
            close_by_tap_outside: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Allows dismissing tooltip by tapping outside of it."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
            #[required]
            div: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]), description: Some("An element that will be shown in a tooltip. If there are tooltips inside an element, they won't be shown."), default_value: None, constraints: Constraints::default() },
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Duration of the tooltip visibility in milliseconds. When the value is set to `0`, the tooltip will be visible until the user hides it."), default_value: Some(serde_json::json!(5000)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Tooltip ID. It is used to avoid re-showing. It must be unique for all element tooltips."), default_value: None, constraints: Constraints::default() },
            mode: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivTooltipModeNonModal".into()), SchemaFieldType::Ref("DivTooltipModeModal".into())]), description: Some("Tooltip modes."), default_value: Some(serde_json::json!("{ \"type\": \"modal\" }")), constraints: Constraints::default() },
            offset: DivPoint => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivPoint".into()), description: Some("Shift relative to an anchor point."), default_value: None, constraints: Constraints::default() },
            #[required]
            position: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["left".into(), "top-left".into(), "top".into(), "top-right".into(), "right".into(), "bottom-right".into(), "bottom".into(), "bottom-left".into(), "center".into()]), SchemaFieldType::Expr]), description: Some("The position of a tooltip relative to an element it belongs to."), default_value: None, constraints: Constraints::default() },
            substrate_div: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]), description: Some("An element that will be used as a substrate for the tooltip."), default_value: None, constraints: Constraints::default() },
            tap_outside_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Specifies actions triggered by tapping outside the tooltip."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// The position of a tooltip relative to an element it belongs to.
    pub enum DivTooltipPosition {
        Left => "left",
        TopLeft => "top-left",
        Top => "top",
        TopRight => "top-right",
        Right => "right",
        BottomRight => "bottom-right",
        Bottom => "bottom",
        BottomLeft => "bottom-left",
        Center => "center",
    }
}
