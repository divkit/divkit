// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Modal mode. Clicks outside do not pass through to underlying elements. Restricts
    /// focus to the tooltip. Back button on Android and back gesture on iOS close the
    /// tooltip.
    pub struct DivTooltipModeModal {
        type_name: "modal",
        fields: {
        }
    }
}
