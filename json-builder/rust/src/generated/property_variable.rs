// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A property that is handeled with `get` and `set` methods.
    pub struct PropertyVariable {
        type_name: "property",
        fields: {
            #[required]
            get: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Value. Supports expressions for property initialization."), default_value: None, constraints: Constraints::default() },
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Property name."), default_value: None, constraints: Constraints::default() },
            new_value_variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name for accessing the data passed to the setter."), default_value: Some(serde_json::json!("new_value")), constraints: Constraints::default() },
            set: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Action when setting a property."), default_value: None, constraints: Constraints::default() },
            #[required]
            value_type: String => FieldSchemaInfo { schema_type: SchemaFieldType::Enum(vec!["string".into(), "integer".into(), "number".into(), "boolean".into(), "datetime".into(), "color".into(), "url".into(), "dict".into(), "array".into()]), description: Some("Return property value type."), default_value: None, constraints: Constraints::default() },
        }
    }
}
