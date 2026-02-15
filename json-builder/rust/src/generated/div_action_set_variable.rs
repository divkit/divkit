// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Assigns a value to the variable
    pub struct DivActionSetVariable {
        type_name: "set_variable",
        fields: {
            #[required]
            value: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringValue".into()), SchemaFieldType::Ref("IntegerValue".into()), SchemaFieldType::Ref("NumberValue".into()), SchemaFieldType::Ref("ColorValue".into()), SchemaFieldType::Ref("BooleanValue".into()), SchemaFieldType::Ref("UrlValue".into()), SchemaFieldType::Ref("DictValue".into()), SchemaFieldType::Ref("ArrayValue".into())]), description: None, default_value: None, constraints: Constraints::default() },
            #[required]
            variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
