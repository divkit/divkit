// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Set values ​​in a variable of type array or dictionary with different nesting.
    pub struct DivActionUpdateStructure {
        type_name: "update_structure",
        fields: {
            #[required]
            path: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Path within an array/dictionary where a value needs to be set. Path format: <li>Each path element is separated by a '/' symbol.</li><li>Path elements can be of two types: an index of an element in an array, starting from 0 or dictionary keys in the form of arbitrary strings.</li><li>The path is read from left to right, each element determines the transition to the next level of nesting.</li>Example path: `key/0/inner_key/1`."), default_value: None, constraints: Constraints { regex: Some("^(?!/)(.+)(?<!/)$".into()), ..Constraints::default() } },
            #[required]
            value: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringValue".into()), SchemaFieldType::Ref("IntegerValue".into()), SchemaFieldType::Ref("NumberValue".into()), SchemaFieldType::Ref("ColorValue".into()), SchemaFieldType::Ref("BooleanValue".into()), SchemaFieldType::Ref("UrlValue".into()), SchemaFieldType::Ref("DictValue".into()), SchemaFieldType::Ref("ArrayValue".into())]), description: Some("Value set into dictionary/array."), default_value: None, constraints: Constraints::default() },
            #[required]
            variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Variable name of array or dictionary type."), default_value: None, constraints: Constraints::default() },
        }
    }
}
