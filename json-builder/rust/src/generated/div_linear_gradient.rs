// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Linear gradient.
    pub struct DivLinearGradient {
        type_name: "gradient",
        fields: {
            angle: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Angle of gradient direction."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), maximum: Some(360.0), ..Constraints::default() } },
            color_map: Vec<DivLinearGradientColorPoint> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivLinearGradientColorPoint".into()))), description: Some("Colors and positions of gradient points. When using this parameter, the `colors` parameter is ignored."), default_value: None, constraints: Constraints { min_items: Some(2), ..Constraints::default() } },
            colors: Vec<String> => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Array(Box::new(SchemaFieldType::String)), SchemaFieldType::Expr]), description: Some("Colors. Gradient points are located at an equal distance from each other."), default_value: None, constraints: Constraints { min_items: Some(2), ..Constraints::default() } },
        }
    }
}

div_entity! {
    /// Describes color at particular gradient position.
    pub struct DivLinearGradientColorPoint {
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Gradient color corresponding to gradient point."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            #[required]
            position: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("The position of the gradient point."), default_value: None, constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
        }
    }
}
