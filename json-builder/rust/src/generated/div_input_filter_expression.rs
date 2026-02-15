// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Filter based on [calculated expressions](../../expressions).
    pub struct DivInputFilterExpression {
        type_name: "expression",
        fields: {
            #[required]
            condition: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("[Calculated expression](../../expressions) used to verify the validity of the value."), default_value: None, constraints: Constraints::default() },
        }
    }
}
