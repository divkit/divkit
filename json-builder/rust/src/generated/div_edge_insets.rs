// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// It sets margins.
    pub struct DivEdgeInsets {
        fields: {
            bottom: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Bottom margin."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            end: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("End margin. Margin position depends on the interface orientation. Has higher priority than the left and right margins."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            left: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Left margin."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            right: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Right margin."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            start: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Start margin. Margin position depends on the interface orientation. Has higher priority than the left and right margins."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            top: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Top margin."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: None, default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
        }
    }
}
