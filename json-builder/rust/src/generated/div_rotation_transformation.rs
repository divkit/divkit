// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Rotation transformation.
    pub struct DivRotationTransformation {
        type_name: "rotation",
        fields: {
            #[required]
            angle: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Rotation angle in degrees."), default_value: None, constraints: Constraints::default() },
            pivot_x: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivPivotFixed".into()), SchemaFieldType::Ref("DivPivotPercentage".into())]), description: Some("X coordinate of the rotation pivot point."), default_value: Some(serde_json::json!("{\"type\": \"pivot-percentage\",\"value\":50}")), constraints: Constraints::default() },
            pivot_y: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivPivotFixed".into()), SchemaFieldType::Ref("DivPivotPercentage".into())]), description: Some("Y coordinate of the rotation pivot point."), default_value: Some(serde_json::json!("{\"type\": \"pivot-percentage\",\"value\":50}")), constraints: Constraints::default() },
        }
    }
}
