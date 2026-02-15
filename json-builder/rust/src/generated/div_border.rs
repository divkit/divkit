// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Stroke around the element.
    pub struct DivBorder {
        fields: {
            corner_radius: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            corners_radius: DivCornersRadius => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivCornersRadius".into()), description: Some("Multiple radii of element and stroke corner rounding."), default_value: None, constraints: Constraints::default() },
            has_shadow: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Adding shadow."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            shadow: DivShadow => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivShadow".into()), description: Some("Parameters of the shadow applied to the element stroke."), default_value: None, constraints: Constraints::default() },
            stroke: DivStroke => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivStroke".into()), description: Some("Stroke style."), default_value: None, constraints: Constraints::default() },
        }
    }
}
