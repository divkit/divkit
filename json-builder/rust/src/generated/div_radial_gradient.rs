// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Radial gradient.
    pub struct DivRadialGradient {
        type_name: "radial_gradient",
        fields: {
            center_x: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivRadialGradientFixedCenter".into()), SchemaFieldType::Ref("DivRadialGradientRelativeCenter".into())]), description: Some("Shift of the central point of the gradient relative to the left edge along the X axis."), default_value: Some(serde_json::json!("{\"type\": \"relative\", \"value\": 0.5 }")), constraints: Constraints::default() },
            center_y: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivRadialGradientFixedCenter".into()), SchemaFieldType::Ref("DivRadialGradientRelativeCenter".into())]), description: Some("Shift of the central point of the gradient relative to the top edge along the Y axis."), default_value: Some(serde_json::json!("{\"type\": \"relative\", \"value\": 0.5 }")), constraints: Constraints::default() },
            color_map: Vec<DivRadialGradientColorPoint> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivRadialGradientColorPoint".into()))), description: Some("Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored."), default_value: None, constraints: Constraints { min_items: Some(2), ..Constraints::default() } },
            colors: Vec<String> => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Array(Box::new(SchemaFieldType::String)), SchemaFieldType::Expr]), description: Some("Colors. Gradient points are located at an equal distance from each other."), default_value: None, constraints: Constraints { min_items: Some(2), ..Constraints::default() } },
            radius: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivFixedSize".into()), SchemaFieldType::Ref("DivRadialGradientRelativeRadius".into())]), description: Some("Radius of the gradient transition."), default_value: Some(serde_json::json!("{\"type\": \"relative\", \"value\": \"farthest_corner\" }")), constraints: Constraints::default() },
        }
    }
}

div_entity! {
    /// Describes color at particular gradient position.
    pub struct DivRadialGradientColorPoint {
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Gradient color corresponding to gradient point."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            #[required]
            position: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("The position of the gradient point."), default_value: None, constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
        }
    }
}
