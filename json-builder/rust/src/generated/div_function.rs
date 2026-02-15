// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// User-defined function.
    pub struct DivFunction {
        fields: {
            #[required]
            arguments: Vec<DivFunctionArgument> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivFunctionArgument".into()))), description: Some("Function argument."), default_value: None, constraints: Constraints::default() },
            #[required]
            body: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Function body. Evaluated as an expression using the passed arguments. Doesn't capture external variables."), default_value: None, constraints: Constraints::default() },
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Function name."), default_value: None, constraints: Constraints { regex: Some("^[a-zA-Z_][a-zA-Z0-9_]*$".into()), ..Constraints::default() } },
            #[required]
            return_type: String => FieldSchemaInfo { schema_type: SchemaFieldType::Enum(vec!["string".into(), "integer".into(), "number".into(), "boolean".into(), "datetime".into(), "color".into(), "url".into(), "dict".into(), "array".into()]), description: Some("Return value type."), default_value: None, constraints: Constraints::default() },
        }
    }
}
