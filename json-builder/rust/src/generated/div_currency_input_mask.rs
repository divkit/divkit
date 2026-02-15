// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Mask for entering currency in the specified regional format.
    pub struct DivCurrencyInputMask {
        type_name: "currency",
        fields: {
            locale: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Language tag that the currency format should match, as per [IETF BCP 47](https://en.wikipedia.org/wiki/IETF_language_tag). If the language is not set, it is defined automatically."), default_value: None, constraints: Constraints::default() },
            #[required]
            raw_text_variable: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable to store the unprocessed value."), default_value: None, constraints: Constraints::default() },
        }
    }
}
