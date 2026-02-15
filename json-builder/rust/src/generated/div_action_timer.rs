// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Controls the timer.
    pub struct DivActionTimer {
        type_name: "timer",
        fields: {
            #[required]
            action: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["start".into(), "stop".into(), "pause".into(), "resume".into(), "cancel".into(), "reset".into()]), SchemaFieldType::Expr]), description: Some("Timer actions:<li>`start` — starts the timer from a stopped state</li><li>`stop`— stops the timer and performs the `onEnd` action</li><li>`pause` — pauses the timer, saves the current time</li><li>`resume` — restarts the timer after a pause</li><li>`cancel` — interrupts the timer, resets the time</li><li>`reset` — cancels the timer, then starts it again</li>"), default_value: None, constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Timer ID."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Timer actions:`start` — starts the timer from a stopped state`stop`— stops the
    /// timer and performs the `onEnd` action`pause` — pauses the timer, saves the
    /// current time`resume` — restarts the timer after a pause`cancel` — interrupts the
    /// timer, resets the time`reset` — cancels the timer, then starts it again
    pub enum DivActionTimerAction {
        Start => "start",
        Stop => "stop",
        Pause => "pause",
        Resume => "resume",
        Cancel => "cancel",
        Reset => "reset",
    }
}
