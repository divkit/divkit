// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element size (%).
    pub struct DivPercentageSize {
        type_name: "percentage",
        fields: {
            #[required]
            value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Element size value."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
