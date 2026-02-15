// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Extension that affects an element.
    pub struct DivExtension {
        fields: {
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Extension ID."), default_value: None, constraints: Constraints::default() },
            params: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::Object, description: Some("Additional extension parameters."), default_value: None, constraints: Constraints::default() },
        }
    }
}
