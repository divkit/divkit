// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivTransitionBase {
        fields: {
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: Some(serde_json::json!(200)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Transition speed nature."), default_value: Some(serde_json::json!("ease_in_out")), constraints: Constraints::default() },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay in milliseconds before animation starts."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
