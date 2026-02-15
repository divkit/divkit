// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Location of the coordinate of the rotation axis as a percentage relative to the
    /// element size.
    pub struct DivPivotPercentage {
        type_name: "pivot-percentage",
        fields: {
            #[required]
            value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Coordinate value as a percentage."), default_value: None, constraints: Constraints::default() },
        }
    }
}
