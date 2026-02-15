// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Animation speed nature. When the value is set to `spring` — animation of damping
    /// fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond
    /// to the Bezier curve:`linear` — cubic-bezier(0, 0, 1, 1);`ease` —
    /// cubic-bezier(0.25, 0.1, 0.25, 1);`ease_in` — cubic-bezier(0.42, 0, 1,
    /// 1);`ease_out` — cubic-bezier(0, 0, 0.58, 1);`ease_in_out` — cubic-bezier(0.42, 0,
    /// 0.58, 1).
    pub enum DivAnimationInterpolator {
        Linear => "linear",
        Ease => "ease",
        EaseIn => "ease_in",
        EaseOut => "ease_out",
        EaseInOut => "ease_in_out",
        Spring => "spring",
    }
}
