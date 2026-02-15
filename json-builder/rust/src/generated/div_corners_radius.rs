// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Sets corner rounding.
    pub struct DivCornersRadius {
        fields: {
            bottom_left: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Rounding radius of a lower left corner. If not specified, then `corner_radius` is used."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            bottom_right: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Rounding radius of a lower right corner. If not specified, then `corner_radius` is used."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            top_left: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Rounding radius of an upper left corner. If not specified, then `corner_radius` is used."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            top_right: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Rounding radius of an upper right corner. If not specified, then `corner_radius` is used."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
