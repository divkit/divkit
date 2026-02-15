// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Fixed number of repetitions.
    pub struct DivFixedCount {
        type_name: "fixed",
        fields: {
            #[required]
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Number of repetitions."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
