// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Sends variables from the container by link. Data sending configuration can be
    /// defined by the host app. By default, variables are sent as JSON in the request
    /// body using the POST method.
    pub struct DivActionSubmit {
        type_name: "submit",
        fields: {
            #[required]
            container_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("ID of the container with the variables to be sent."), default_value: None, constraints: Constraints::default() },
            on_fail_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions when sending data is unsuccessful."), default_value: None, constraints: Constraints::default() },
            on_success_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions when sending data is successful."), default_value: None, constraints: Constraints::default() },
            #[required]
            request: DivActionSubmitRequest => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivActionSubmitRequest".into()), description: Some("HTTP request parameters for configuring the sending of data."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_entity! {
    /// HTTP request parameters for configuring the sending of data.
    pub struct DivActionSubmitRequest {
        fields: {
            headers: Vec<RequestHeader> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("RequestHeader".into()))), description: Some("HTTP request headers. Please note that DivKit does not clean duplicate headers, which can lead to errors in request processing. Keep this in mind when assembling actions with complex JSON-builders."), default_value: None, constraints: Constraints::default() },
            method: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["get".into(), "post".into(), "put".into(), "patch".into(), "delete".into(), "head".into(), "options".into()]), SchemaFieldType::Expr]), description: Some("HTTP request method."), default_value: Some(serde_json::json!("post")), constraints: Constraints::default() },
            #[required]
            url: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Link for sending data from the container."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
        }
    }
}

div_enum! {
    /// HTTP request method.
    pub enum RequestMethod {
        Get => "get",
        Post => "post",
        Put => "put",
        Patch => "patch",
        Delete => "delete",
        Head => "head",
        Options => "options",
    }
}

div_entity! {
    pub struct RequestHeader {
        fields: {
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            #[required]
            value: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
