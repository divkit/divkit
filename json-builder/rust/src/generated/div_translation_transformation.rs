// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Translation transformation.
    pub struct DivTranslationTransformation {
        type_name: "translation",
        fields: {
            x: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivFixedTranslation".into()), SchemaFieldType::Ref("DivPercentageTranslation".into())]), description: Some("X coordinate of the translation."), default_value: None, constraints: Constraints::default() },
            y: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivFixedTranslation".into()), SchemaFieldType::Ref("DivPercentageTranslation".into())]), description: Some("Y coordinate of the translation."), default_value: None, constraints: Constraints::default() },
        }
    }
}
