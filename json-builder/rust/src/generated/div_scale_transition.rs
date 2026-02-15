// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Scale animation.
    pub struct DivScaleTransition {
        type_name: "scale",
        fields: {
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: Some(serde_json::json!(200)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Transition speed nature."), default_value: Some(serde_json::json!("ease_in_out")), constraints: Constraints::default() },
            pivot_x: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Relative coordinate `X` of the point that won't change its position in case of scaling."), default_value: Some(serde_json::json!(0.5)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            pivot_y: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Relative coordinate `Y` of the point that won't change its position in case of scaling."), default_value: Some(serde_json::json!(0.5)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            scale: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Value of the scale  from which the element starts appearing or at which it finishes disappearing."), default_value: Some(serde_json::json!(0.0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay in milliseconds before animation starts."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
