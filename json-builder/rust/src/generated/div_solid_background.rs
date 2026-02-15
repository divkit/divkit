// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Solid background color.
    pub struct DivSolidBackground {
        type_name: "solid",
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Color."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
        }
    }
}
