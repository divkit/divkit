// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Function argument.
    pub struct DivFunctionArgument {
        fields: {
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Function argument name."), default_value: None, constraints: Constraints::default() },
            #[required]
            r#type: String => FieldSchemaInfo { schema_type: SchemaFieldType::Enum(vec!["string".into(), "integer".into(), "number".into(), "boolean".into(), "datetime".into(), "color".into(), "url".into(), "dict".into(), "array".into()]), description: Some("Function argument type."), default_value: None, constraints: Constraints::default() },
        }
    }
}
