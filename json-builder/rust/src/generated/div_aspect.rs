// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Fixed aspect ratio. The element's height is calculated based on the width,
    /// ignoring the `height` value.
    pub struct DivAspect {
        fields: {
            #[required]
            ratio: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("`height = width / ratio`."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
