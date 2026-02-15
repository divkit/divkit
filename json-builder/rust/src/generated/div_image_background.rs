// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Background image.
    pub struct DivImageBackground {
        type_name: "image",
        fields: {
            alpha: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Image transparency."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            content_alignment_horizontal: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["left".into(), "center".into(), "right".into(), "start".into(), "end".into()]), SchemaFieldType::Expr]), description: Some("Horizontal image alignment."), default_value: Some(serde_json::json!("center")), constraints: Constraints::default() },
            content_alignment_vertical: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["top".into(), "center".into(), "bottom".into(), "baseline".into()]), SchemaFieldType::Expr]), description: Some("Vertical image alignment."), default_value: Some(serde_json::json!("center")), constraints: Constraints::default() },
            filters: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivBlur".into()), SchemaFieldType::Ref("DivFilterRtlMirror".into())]))), description: Some("Image filters."), default_value: None, constraints: Constraints::default() },
            #[required]
            image_url: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Image URL."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
            preload_required: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Background image must be loaded before the display."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            scale: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["fill".into(), "no_scale".into(), "fit".into(), "stretch".into()]), SchemaFieldType::Expr]), description: Some("Image scaling."), default_value: Some(serde_json::json!("fill")), constraints: Constraints::default() },
        }
    }
}
