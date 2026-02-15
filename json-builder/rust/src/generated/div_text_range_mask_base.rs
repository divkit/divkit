// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivTextRangeMaskBase {
        fields: {
            is_enabled: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
        }
    }
}
