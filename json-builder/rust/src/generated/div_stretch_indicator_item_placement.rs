// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element size adjusts to a parent element.
    pub struct DivStretchIndicatorItemPlacement {
        type_name: "stretch",
        fields: {
            item_spacing: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Spacing between indicator centers."), default_value: Some(serde_json::json!("{\"type\": \"fixed\",\"value\":5}")), constraints: Constraints::default() },
            max_visible_items: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Maximum number of visible indicators."), default_value: Some(serde_json::json!(10)), constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
