// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivLayoutProvider {
        fields: {
            height_variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable that stores the element’s height."), default_value: None, constraints: Constraints::default() },
            width_variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable that stores the element’s width."), default_value: None, constraints: Constraints::default() },
        }
    }
}
