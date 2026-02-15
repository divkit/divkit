// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Drawable of a simple geometric shape.
    pub struct DivShapeDrawable {
        type_name: "shape_drawable",
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Fill color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            #[required]
            shape: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivRoundedRectangleShape".into()), SchemaFieldType::Ref("DivCircleShape".into())]), description: Some("Shape."), default_value: None, constraints: Constraints::default() },
            stroke: DivStroke => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivStroke".into()), description: Some("Stroke style."), default_value: None, constraints: Constraints::default() },
        }
    }
}
