// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Callbacks that are called after [data loading](../../interaction#loading-data).
    pub struct DivDownloadCallbacks {
        fields: {
            on_fail_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions in case of unsuccessful loading if the host reported it or the waiting time expired."), default_value: None, constraints: Constraints::default() },
            on_success_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions in case of successful loading."), default_value: None, constraints: Constraints::default() },
        }
    }
}
