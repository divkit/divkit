// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A trigger that causes an action when activated.
    pub struct DivTrigger {
        fields: {
            #[required]
            actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Action when a trigger is activated."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
            #[required]
            condition: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Condition for activating a trigger. For example, `liked && subscribed`."), default_value: None, constraints: Constraints::default() },
            mode: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["on_condition".into(), "on_variable".into()]), SchemaFieldType::Expr]), description: Some("Trigger activation mode:<li>`on_condition` — a trigger is activated when the condition changes from `false` to `true`;</li><li>`on_variable` — a trigger is activated when the condition is met and the variable value changes.</li>"), default_value: Some(serde_json::json!("on_condition")), constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Trigger activation mode:`on_condition` — a trigger is activated when the
    /// condition changes from `false` to `true`;`on_variable` — a trigger is activated
    /// when the condition is met and the variable value changes.
    pub enum DivTriggerMode {
        OnCondition => "on_condition",
        OnVariable => "on_variable",
    }
}
