// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Slide animation.
    pub struct DivSlideTransition {
        type_name: "slide",
        fields: {
            distance: DivDimension => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivDimension".into()), description: Some("A fixed value of an offset which the element starts appearing from or at which it finishes disappearing. If no value is specified, the distance to the selected edge of a parent element is used."), default_value: None, constraints: Constraints::default() },
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: Some(serde_json::json!(200)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            edge: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["left".into(), "top".into(), "right".into(), "bottom".into()]), SchemaFieldType::Expr]), description: Some("Edge of a parent element for one of the action types:<li>where the element will move from when appearing;</li><li>where the element will move to when disappearing.</li>"), default_value: Some(serde_json::json!("bottom")), constraints: Constraints::default() },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Transition speed nature."), default_value: Some(serde_json::json!("ease_in_out")), constraints: Constraints::default() },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay in milliseconds before animation starts."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}

div_enum! {
    /// Edge of a parent element for one of the action types:where the element will move
    /// from when appearing;where the element will move to when disappearing.
    pub enum DivSlideTransitionEdge {
        Left => "left",
        Top => "top",
        Right => "right",
        Bottom => "bottom",
    }
}
