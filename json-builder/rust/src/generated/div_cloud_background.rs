// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Cloud-style text background. Rows have a rectangular background in the specified
    /// color with rounded corners.
    pub struct DivCloudBackground {
        type_name: "cloud",
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Fill color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            #[required]
            corner_radius: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Corner rounding radius."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            paddings: DivEdgeInsets => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivEdgeInsets".into()), description: Some("Margins between the row border and background border."), default_value: None, constraints: Constraints::default() },
        }
    }
}
