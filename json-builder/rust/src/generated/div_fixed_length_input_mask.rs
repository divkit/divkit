// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Mask for entering text with a fixed number of characters.
    pub struct DivFixedLengthInputMask {
        type_name: "fixed_length",
        fields: {
            always_visible: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("If this option is enabled, the text field contains the mask before being filled in."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            #[required]
            pattern: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("String that sets the text input template. For example, the `+7 (###) ###-##-## ` template for a phone number."), default_value: None, constraints: Constraints::default() },
            #[required]
            pattern_elements: Vec<DivFixedLengthInputMaskPatternElement> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivFixedLengthInputMaskPatternElement".into()))), description: Some("Template decoding is a description of the characters that will be replaced with user input."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
            #[required]
            raw_text_variable: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable to store the unprocessed value."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_entity! {
    /// Template decoding is a description of the characters that will be replaced with
    /// user input.
    pub struct DivFixedLengthInputMaskPatternElement {
        fields: {
            #[required]
            key: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("A character in the template that will be replaced with a user-defined character."), default_value: None, constraints: Constraints { min_length: Some(1), ..Constraints::default() } },
            placeholder: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("The character that's displayed in the input field where the user is expected to enter text. This is used if mask display is enabled."), default_value: Some(serde_json::json!("_")), constraints: Constraints { min_length: Some(1), ..Constraints::default() } },
            regex: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Regular expression for validating character inputs. For example, when a mask is digit-only."), default_value: None, constraints: Constraints::default() },
        }
    }
}
