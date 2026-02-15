// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Scrolls the container by `item_count` or `offset` starting from the current
    /// position. If both values are specified, the action will be combined. For
    /// scrolling back, use negative values.
    pub struct DivActionScrollBy {
        type_name: "scroll_by",
        fields: {
            animated: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Enables scrolling animation."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("ID of the element where the action should be performed."), default_value: None, constraints: Constraints::default() },
            item_count: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Number of container elements to scroll through. For scrolling back, use negative values."), default_value: Some(serde_json::json!(0)), constraints: Constraints::default() },
            offset: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Scrolling distance measured in `dp` from the current position. For scrolling back, use negative values. Only applies in `gallery`."), default_value: Some(serde_json::json!(0)), constraints: Constraints::default() },
            overflow: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["clamp".into(), "ring".into()]), SchemaFieldType::Expr]), description: Some("Defines navigation behavior at boundary elements:<li>`clamp`: Stop navigation at the boundary element (default)</li><li>`ring`: Navigate to the start or end, depending on the current element.</li>"), default_value: Some(serde_json::json!("clamp")), constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Defines navigation behavior at boundary elements:`clamp`: Stop navigation at the
    /// boundary element (default)`ring`: Navigate to the start or end, depending on the
    /// current element.
    pub enum DivActionScrollByOverflow {
        Clamp => "clamp",
        Ring => "ring",
    }
}
