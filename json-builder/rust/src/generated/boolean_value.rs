// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct BooleanValue {
        type_name: "boolean",
        fields: {
            #[required]
            value: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
