// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Stroke.
    pub struct DivStroke {
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Stroke color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            style: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivStrokeStyleSolid".into()), SchemaFieldType::Ref("DivStrokeStyleDashed".into())]), description: Some("Stroke style. Supported for border stroke only."), default_value: Some(serde_json::json!("{\"type\": \"solid\"}")), constraints: Constraints::default() },
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: None, default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
            width: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Stroke width."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
