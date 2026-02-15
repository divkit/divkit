// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Root structure.
    pub struct DivData {
        fields: {
            functions: Vec<DivFunction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivFunction".into()))), description: Some("User functions."), default_value: None, constraints: Constraints::default() },
            #[required]
            log_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Logging ID."), default_value: None, constraints: Constraints::default() },
            #[required]
            states: Vec<DivDataState> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivDataState".into()))), description: Some("A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md)."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
            timers: Vec<DivTimer> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivTimer".into()))), description: Some("List of timers."), default_value: None, constraints: Constraints::default() },
            transition_animation_selector: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["none".into(), "data_change".into(), "state_change".into(), "any_change".into()]), SchemaFieldType::Expr]), description: Some("Events that trigger transition animations."), default_value: Some(serde_json::json!("none")), constraints: Constraints::default() },
            variable_triggers: Vec<DivTrigger> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivTrigger".into()))), description: Some("Triggers for changing variables."), default_value: None, constraints: Constraints::default() },
            variables: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("StringVariable".into()), SchemaFieldType::Ref("NumberVariable".into()), SchemaFieldType::Ref("IntegerVariable".into()), SchemaFieldType::Ref("BooleanVariable".into()), SchemaFieldType::Ref("ColorVariable".into()), SchemaFieldType::Ref("UrlVariable".into()), SchemaFieldType::Ref("DictVariable".into()), SchemaFieldType::Ref("ArrayVariable".into()), SchemaFieldType::Ref("PropertyVariable".into())]))), description: Some("Declaration of variables that can be used in an element."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_entity! {
    pub struct DivDataState {
        fields: {
            #[required]
            div: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]), description: Some("Contents."), default_value: None, constraints: Constraints::default() },
            #[required]
            state_id: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::Integer, description: Some("State ID."), default_value: None, constraints: Constraints::default() },
        }
    }
}
