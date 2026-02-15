// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivSizeUnitValue {
        fields: {
            unit: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["dp".into(), "sp".into(), "px".into()]), SchemaFieldType::Expr]), description: Some("Unit of measurement:<li>`px` — a physical pixel.</li><li>`dp` — a logical pixel that doesn't depend on screen density.</li><li>`sp` — a logical pixel that depends on the font size on a device. Specify height in `sp`. Only available on Android.</li>"), default_value: Some(serde_json::json!("dp")), constraints: Constraints::default() },
            #[required]
            value: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Element size."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
