// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Unit of measurement:`px` — a physical pixel.`dp` — a logical pixel that doesn't
    /// depend on screen density.`sp` — a logical pixel that depends on the font size on
    /// a device. Specify height in `sp`. Only available on Android.
    pub enum DivSizeUnit {
        Dp => "dp",
        Sp => "sp",
        Px => "px",
    }
}
