// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A mask to hide text (spoiler). Looks like a rectangle filled with the color
    /// specified in the `color` parameter, same as in Telegram.
    pub struct DivTextRangeMaskSolid {
        type_name: "solid",
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            is_enabled: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
        }
    }
}
