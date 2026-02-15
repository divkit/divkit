// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivSightAction {
        fields: {
            download_callbacks: DivDownloadCallbacks => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivDownloadCallbacks".into()), description: Some("Callbacks that are called after [data loading](../../interaction#loading-data)."), default_value: None, constraints: Constraints::default() },
            is_enabled: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on)."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
            #[required]
            log_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Logging ID."), default_value: None, constraints: Constraints::default() },
            log_limit: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Limit on the number of loggings. If `0`, the limit is removed."), default_value: Some(serde_json::json!(1)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            payload: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::Object, description: Some("Additional parameters, passed to the host application."), default_value: None, constraints: Constraints::default() },
            referer: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Referer URL for logging."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
            scope_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("The ID of the element within which the specified action will be performed."), default_value: None, constraints: Constraints::default() },
            typed: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivActionAnimatorStart".into()), SchemaFieldType::Ref("DivActionAnimatorStop".into()), SchemaFieldType::Ref("DivActionArrayInsertValue".into()), SchemaFieldType::Ref("DivActionArrayRemoveValue".into()), SchemaFieldType::Ref("DivActionArraySetValue".into()), SchemaFieldType::Ref("DivActionClearFocus".into()), SchemaFieldType::Ref("DivActionCopyToClipboard".into()), SchemaFieldType::Ref("DivActionDictSetValue".into()), SchemaFieldType::Ref("DivActionDownload".into()), SchemaFieldType::Ref("DivActionFocusElement".into()), SchemaFieldType::Ref("DivActionHideTooltip".into()), SchemaFieldType::Ref("DivActionScrollBy".into()), SchemaFieldType::Ref("DivActionScrollTo".into()), SchemaFieldType::Ref("DivActionSetState".into()), SchemaFieldType::Ref("DivActionSetStoredValue".into()), SchemaFieldType::Ref("DivActionSetVariable".into()), SchemaFieldType::Ref("DivActionShowTooltip".into()), SchemaFieldType::Ref("DivActionSubmit".into()), SchemaFieldType::Ref("DivActionTimer".into()), SchemaFieldType::Ref("DivActionUpdateStructure".into()), SchemaFieldType::Ref("DivActionVideo".into()), SchemaFieldType::Ref("DivActionCustom".into())]), description: None, default_value: None, constraints: Constraints::default() },
            url: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](../../interaction)."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
        }
    }
}
