// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Fixed size of an element.
    pub struct DivFixedSize {
        type_name: "fixed",
        fields: {
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: Some("Unit of measurement. To learn more about units of size measurement, see [Layout inside the card](../../layout)."), default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
            #[required]
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Element size."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
