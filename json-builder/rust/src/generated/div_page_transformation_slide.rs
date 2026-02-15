// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Pages move without overlapping during pager scrolling.
    pub struct DivPageTransformationSlide {
        type_name: "slide",
        fields: {
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Animation speed adjustment. When the value is set to `spring`, it’s a damped oscillation animation truncated to 0.7, with the `damping=1` parameter. Other values correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1)</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1)</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1)</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1)</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1)</li>"), default_value: Some(serde_json::json!("ease_in_out")), constraints: Constraints::default() },
            next_page_alpha: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Minimum transparency of the next page, within the range [0, 1], when scrolling through the pager. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            next_page_scale: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Scaling the next page during pager scrolling. The following page is always the page with a larger ordinal number in the `items` list, regardless of the scrolling direction."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            previous_page_alpha: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Minimum transparency of the previous page, in the range [0, 1], during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            previous_page_scale: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Scaling the previous page during pager scrolling. The previous page is always the page with a lower ordinal number in the `items` list, regardless of the scrolling direction."), default_value: Some(serde_json::json!(1.0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
