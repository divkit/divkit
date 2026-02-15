// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element size adjusts to a parent element.
    pub struct DivDefaultIndicatorItemPlacement {
        type_name: "default",
        fields: {
            space_between_centers: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Spacing between indicator centers."), default_value: Some(serde_json::json!("{\"type\": \"fixed\",\"value\":15}")), constraints: Constraints::default() },
        }
    }
}
