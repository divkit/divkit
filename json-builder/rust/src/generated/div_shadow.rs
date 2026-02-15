// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element shadow.
    pub struct DivShadow {
        fields: {
            alpha: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Shadow transparency."), default_value: Some(serde_json::json!(0.19)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            blur: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Blur intensity."), default_value: Some(serde_json::json!(2)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Shadow color."), default_value: Some(serde_json::json!("#000000")), constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            #[required]
            offset: DivPoint => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivPoint".into()), description: Some("Shadow offset."), default_value: None, constraints: Constraints::default() },
        }
    }
}
