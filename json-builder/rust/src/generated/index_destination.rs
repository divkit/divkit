// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Specifies the element with the given index as the scrolling end position.
    pub struct IndexDestination {
        type_name: "index",
        fields: {
            #[required]
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Container element index."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
