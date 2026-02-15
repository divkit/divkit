// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// An integer variable.
    pub struct IntegerVariable {
        type_name: "integer",
        fields: {
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Variable name."), default_value: None, constraints: Constraints::default() },
            #[required]
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Value. Supports expressions for variable initialization."), default_value: None, constraints: Constraints::default() },
        }
    }
}
