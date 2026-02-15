// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Fixed coordinates of the rotation axis.
    pub struct DivPivotFixed {
        type_name: "pivot-fixed",
        fields: {
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: Some("Measurement unit. To learn more about units of size measurement, see [Layout inside the card](../../layout)."), default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Coordinate value."), default_value: None, constraints: Constraints::default() },
        }
    }
}
