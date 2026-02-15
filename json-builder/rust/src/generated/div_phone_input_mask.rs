// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Mask for entering phone numbers with dynamic regional format identification.
    pub struct DivPhoneInputMask {
        type_name: "phone",
        fields: {
            #[required]
            raw_text_variable: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable to store the unprocessed value."), default_value: None, constraints: Constraints::default() },
        }
    }
}
