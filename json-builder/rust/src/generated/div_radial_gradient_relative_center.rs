// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Location of the central point of the gradient relative to the element borders.
    pub struct DivRadialGradientRelativeCenter {
        type_name: "relative",
        fields: {
            #[required]
            value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Coordinate value in the range \"0...1\"."), default_value: None, constraints: Constraints::default() },
        }
    }
}
