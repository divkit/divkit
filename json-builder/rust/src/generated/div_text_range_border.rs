// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Character range border.
    pub struct DivTextRangeBorder {
        fields: {
            corner_radius: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("One radius of element and stroke corner rounding. Has a lower priority than `corners_radius`."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            stroke: DivStroke => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivStroke".into()), description: Some("Stroke style."), default_value: None, constraints: Constraints::default() },
        }
    }
}
