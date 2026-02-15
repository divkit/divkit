// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Transformation of the element.
    pub struct DivTransform {
        fields: {
            pivot_x: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivPivotFixed".into()), SchemaFieldType::Ref("DivPivotPercentage".into())]), description: Some("X coordinate of the rotation axis."), default_value: Some(serde_json::json!("{\"type\": \"pivot-percentage\",\"value\":50}")), constraints: Constraints::default() },
            pivot_y: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivPivotFixed".into()), SchemaFieldType::Ref("DivPivotPercentage".into())]), description: Some("Y coordinate of the rotation axis."), default_value: Some(serde_json::json!("{\"type\": \"pivot-percentage\",\"value\":50}")), constraints: Constraints::default() },
            rotation: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Degrees of the element rotation. Positive values used for clockwise rotation."), default_value: None, constraints: Constraints::default() },
        }
    }
}
