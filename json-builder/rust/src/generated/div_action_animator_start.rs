// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Launches the specified animator.
    pub struct DivActionAnimatorStart {
        type_name: "animator_start",
        fields: {
            #[required]
            animator_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("ID of the animator launched."), default_value: None, constraints: Constraints::default() },
            direction: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["normal".into(), "reverse".into(), "alternate".into(), "alternate_reverse".into()]), SchemaFieldType::Expr]), description: Some("Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward."), default_value: None, constraints: Constraints::default() },
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            end_value: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringValue".into()), SchemaFieldType::Ref("IntegerValue".into()), SchemaFieldType::Ref("NumberValue".into()), SchemaFieldType::Ref("ColorValue".into()), SchemaFieldType::Ref("BooleanValue".into()), SchemaFieldType::Ref("UrlValue".into()), SchemaFieldType::Ref("DictValue".into()), SchemaFieldType::Ref("ArrayValue".into())]), description: Some("Overrides the value that will be set after the animation finishes."), default_value: None, constraints: Constraints::default() },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Animated value interpolation function."), default_value: None, constraints: Constraints::default() },
            repeat_count: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivInfinityCount".into()), SchemaFieldType::Ref("DivFixedCount".into())]), description: Some("Number of times the animation will repeat before stopping. A value of `0` enables infinite looping."), default_value: None, constraints: Constraints::default() },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay before the animation is launched in milliseconds."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            start_value: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringValue".into()), SchemaFieldType::Ref("IntegerValue".into()), SchemaFieldType::Ref("NumberValue".into()), SchemaFieldType::Ref("ColorValue".into()), SchemaFieldType::Ref("BooleanValue".into()), SchemaFieldType::Ref("UrlValue".into()), SchemaFieldType::Ref("DictValue".into()), SchemaFieldType::Ref("ArrayValue".into())]), description: Some("Overrides the value that will be set before the animation begins."), default_value: None, constraints: Constraints::default() },
        }
    }
}
