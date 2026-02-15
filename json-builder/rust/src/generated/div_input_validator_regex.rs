// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Regex validator.
    pub struct DivInputValidatorRegex {
        type_name: "regex",
        fields: {
            allow_empty: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Determines whether the empty field value is valid."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            #[required]
            label_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("ID of the text element containing the error message. The message will also be used for providing access."), default_value: None, constraints: Constraints::default() },
            #[required]
            pattern: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("A regular expression (pattern) that the field value must match."), default_value: None, constraints: Constraints::default() },
            #[required]
            variable: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("The name of the variable that stores the calculation results."), default_value: None, constraints: Constraints::default() },
        }
    }
}
