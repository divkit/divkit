// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Accessibility settings.
    pub struct DivAccessibility {
        fields: {
            description: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Element description. It is used as the main description for screen reading applications."), default_value: None, constraints: Constraints::default() },
            hint: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("A tooltip of what will happen during interaction. If Speak Hints is enabled in the VoiceOver settings on iOS, a tooltip is played after `description`."), default_value: None, constraints: Constraints::default() },
            is_checked: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Shows the current status of the checkbox or toggle: `true` means it's selected, `false` means it isn't selected."), default_value: None, constraints: Constraints::default() },
            mode: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["default".into(), "merge".into(), "exclude".into()]), SchemaFieldType::Expr]), description: Some("The way the accessibility tree is organized. In the `merge` mode the accessibility service perceives an element together with a subtree as a whole. In the `exclude` mode an element together with a subtree isn't available for accessibility."), default_value: Some(serde_json::json!("default")), constraints: Constraints::default() },
            mute_after_action: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Mutes the screen reader sound after interacting with the element."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            state_description: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Description of the current state of an element. For example, in the description you can specify a selected date for a date selection element and an on/off state for a switch."), default_value: None, constraints: Constraints::default() },
            r#type: String => FieldSchemaInfo { schema_type: SchemaFieldType::Enum(vec!["none".into(), "button".into(), "image".into(), "text".into(), "edit_text".into(), "header".into(), "tab_bar".into(), "list".into(), "select".into(), "checkbox".into(), "radio".into(), "auto".into()]), description: Some("Element role. Used to correctly identify an element by the accessibility service. For example, the `list` element is used to group list elements into one element."), default_value: Some(serde_json::json!("auto")), constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// The way the accessibility tree is organized. In the `merge` mode the
    /// accessibility service perceives an element together with a subtree as a whole. In
    /// the `exclude` mode an element together with a subtree isn't available for
    /// accessibility.
    pub enum DivAccessibilityMode {
        Default => "default",
        Merge => "merge",
        Exclude => "exclude",
    }
}

div_enum! {
    /// Element role. Used to correctly identify an element by the accessibility service.
    /// For example, the `list` element is used to group list elements into one element.
    pub enum DivAccessibilityType {
        None => "none",
        Button => "button",
        Image => "image",
        Text => "text",
        EditText => "edit_text",
        Header => "header",
        TabBar => "tab_bar",
        List => "list",
        Select => "select",
        Checkbox => "checkbox",
        Radio => "radio",
        Auto => "auto",
    }
}
