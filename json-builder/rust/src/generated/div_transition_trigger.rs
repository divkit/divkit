// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Event type that triggers animation.
    pub enum DivTransitionTrigger {
        DataChange => "data_change",
        StateChange => "state_change",
        VisibilityChange => "visibility_change",
    }
}
