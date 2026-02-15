// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Filter based on regular expressions.
    pub struct DivInputFilterRegex {
        type_name: "regex",
        fields: {
            #[required]
            pattern: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Regular expression (pattern) that the entered value must match."), default_value: None, constraints: Constraints::default() },
        }
    }
}
