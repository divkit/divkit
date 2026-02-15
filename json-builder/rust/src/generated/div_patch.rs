// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Edits the element.
    pub struct DivPatch {
        fields: {
            #[required]
            changes: Vec<DivPatchChange> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivPatchChange".into()))), description: Some("Element changes."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
            mode: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["transactional".into(), "partial".into()]), SchemaFieldType::Expr]), description: Some("Procedure for applying changes:<li>`transactional` — if an error occurs during application of at least one element, the changes aren't applied.</li><li>`partial` — all possible changes are applied. If there are errors, they are reported.</li>"), default_value: Some(serde_json::json!("partial")), constraints: Constraints::default() },
            on_applied_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions to perform after changes are applied."), default_value: None, constraints: Constraints::default() },
            on_failed_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions to perform if there’s an error when applying changes in transaction mode."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Procedure for applying changes:`transactional` — if an error occurs during
    /// application of at least one element, the changes aren't applied.`partial` — all
    /// possible changes are applied. If there are errors, they are reported.
    pub enum DivPatchMode {
        Transactional => "transactional",
        Partial => "partial",
    }
}

div_entity! {
    pub struct DivPatchChange {
        fields: {
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("ID of an element to be replaced or removed."), default_value: None, constraints: Constraints::default() },
            items: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]))), description: Some("Elements to be inserted. If the parameter isn't specified, the element will be removed."), default_value: None, constraints: Constraints::default() },
        }
    }
}
