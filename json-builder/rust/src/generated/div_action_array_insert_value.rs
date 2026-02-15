// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Adds a value to the array
    pub struct DivActionArrayInsertValue {
        type_name: "array_insert_value",
        fields: {
            index: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            #[required]
            value: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringValue".into()), SchemaFieldType::Ref("IntegerValue".into()), SchemaFieldType::Ref("NumberValue".into()), SchemaFieldType::Ref("ColorValue".into()), SchemaFieldType::Ref("BooleanValue".into()), SchemaFieldType::Ref("UrlValue".into()), SchemaFieldType::Ref("DictValue".into()), SchemaFieldType::Ref("ArrayValue".into())]), description: None, default_value: None, constraints: Constraints::default() },
            #[required]
            variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
