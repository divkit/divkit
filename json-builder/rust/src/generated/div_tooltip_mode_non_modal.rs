// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Non-modal mode. Clicks outside pass through to underlying elements. Does not
    /// restrict focus to the tooltip. Back button on Android and back gesture on iOS do
    /// not close the tooltip.
    pub struct DivTooltipModeNonModal {
        type_name: "non_modal",
        fields: {
        }
    }
}
