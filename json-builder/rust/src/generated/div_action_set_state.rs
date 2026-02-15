// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Applies a new appearance to the content in `div-state'.
    pub struct DivActionSetState {
        type_name: "set_state",
        fields: {
            #[required]
            state_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("The path of the state inside `state` that needs to be activated. Set in the format `div_data_state_id/id/state_id'. Can be hierarchical: `div_data_state_id/id_1/state_id_1/../id_n/state_id_n`. Consists of:<li>`div_data_state_id` — the numeric value of the `state_id` of the `state` object in `data`</li><li>'id` — the `id` value of the `state` object</li><li>`state_id` — the `state_id` value of the `state` object in `state`</li>"), default_value: None, constraints: Constraints::default() },
            temporary: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Indicates a state change:<li>`true` — the change is temporary and will switch to the original one (default value) when the element is recreated</li><li>`false` — the change is permanent</li>"), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
        }
    }
}
