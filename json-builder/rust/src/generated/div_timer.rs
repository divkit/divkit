// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Timer.
    pub struct DivTimer {
        fields: {
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs)."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            end_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received."), default_value: None, constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop."), default_value: None, constraints: Constraints::default() },
            tick_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions that are performed on each count of the timer."), default_value: None, constraints: Constraints::default() },
            tick_interval: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
            value_variable: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command."), default_value: None, constraints: Constraints::default() },
        }
    }
}
