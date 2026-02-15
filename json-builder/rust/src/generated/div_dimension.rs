// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element dimension value.
    pub struct DivDimension {
        fields: {
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: None, default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
            #[required]
            value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Value."), default_value: None, constraints: Constraints::default() },
        }
    }
}
