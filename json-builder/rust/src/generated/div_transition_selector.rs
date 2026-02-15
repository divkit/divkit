// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Variants of events that will trigger animations in child elements. The parent
    /// element can be [data](div-data.md) or [state](div-state.md).
    pub enum DivTransitionSelector {
        None => "none",
        DataChange => "data_change",
        StateChange => "state_change",
        AnyChange => "any_change",
    }
}
