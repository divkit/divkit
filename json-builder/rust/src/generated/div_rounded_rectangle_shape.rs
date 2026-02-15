// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A rectangle with rounded corners.
    pub struct DivRoundedRectangleShape {
        type_name: "rounded_rectangle",
        fields: {
            background_color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Fill color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            corner_radius: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Corner rounding radius."), default_value: Some(serde_json::json!("{\"type\":\"fixed\",\"value\":5}")), constraints: Constraints::default() },
            item_height: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Height."), default_value: Some(serde_json::json!("{\"type\":\"fixed\",\"value\":10}")), constraints: Constraints::default() },
            item_width: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Width."), default_value: Some(serde_json::json!("{\"type\":\"fixed\",\"value\":10}")), constraints: Constraints::default() },
            stroke: DivStroke => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivStroke".into()), description: Some("Stroke style."), default_value: None, constraints: Constraints::default() },
        }
    }
}
