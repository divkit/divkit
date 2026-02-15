// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// An arbitrary array in JSON format.
    pub struct ArrayVariable {
        type_name: "array",
        fields: {
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Variable name."), default_value: None, constraints: Constraints::default() },
            #[required]
            value: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Array(Box::new(SchemaFieldType::Any)), SchemaFieldType::Expr]), description: Some("Value. Supports expressions for variable initialization."), default_value: None, constraints: Constraints::default() },
        }
    }
}
