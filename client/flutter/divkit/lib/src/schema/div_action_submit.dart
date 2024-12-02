// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Sends variables from the container by link. Data sending configuration can be defined by the host app. By default, variables are sent as JSON in the request body using the POST method.
class DivActionSubmit extends Resolvable with EquatableMixin {
  const DivActionSubmit({
    required this.containerId,
    this.onFailActions,
    this.onSuccessActions,
    required this.request,
  });

  static const type = "submit";

  /// ID of the container with the variables to be sent.
  final Expression<String> containerId;

  /// Actions when sending data is unsuccessful.
  final Arr<DivAction>? onFailActions;

  /// Actions when sending data is successful.
  final Arr<DivAction>? onSuccessActions;

  /// HTTP request parameters for configuring the sending of data.
  final DivActionSubmitRequest request;

  @override
  List<Object?> get props => [
        containerId,
        onFailActions,
        onSuccessActions,
        request,
      ];

  DivActionSubmit copyWith({
    Expression<String>? containerId,
    Arr<DivAction>? Function()? onFailActions,
    Arr<DivAction>? Function()? onSuccessActions,
    DivActionSubmitRequest? request,
  }) =>
      DivActionSubmit(
        containerId: containerId ?? this.containerId,
        onFailActions:
            onFailActions != null ? onFailActions.call() : this.onFailActions,
        onSuccessActions: onSuccessActions != null
            ? onSuccessActions.call()
            : this.onSuccessActions,
        request: request ?? this.request,
      );

  static DivActionSubmit? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmit(
        containerId: reqVProp<String>(
          safeParseStrExpr(
            json['container_id'],
          ),
          name: 'container_id',
        ),
        onFailActions: safeParseObjects(
          json['on_fail_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        onSuccessActions: safeParseObjects(
          json['on_success_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        request: reqProp<DivActionSubmitRequest>(
          safeParseObject(
            json['request'],
            parse: DivActionSubmitRequest.fromJson,
          ),
          name: 'request',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionSubmit resolve(DivVariableContext context) {
    containerId.resolve(context);
    tryResolveList(onFailActions, (v) => v.resolve(context));
    tryResolveList(onSuccessActions, (v) => v.resolve(context));
    request.resolve(context);
    return this;
  }
}

/// HTTP request parameters for configuring the sending of data.
class DivActionSubmitRequest extends Resolvable with EquatableMixin {
  const DivActionSubmitRequest({
    this.headers,
    this.method = const ValueExpression(DivActionSubmitRequestMethod.post),
    required this.url,
  });

  /// HTTP request headers.
  final Arr<DivActionSubmitRequestHeader>? headers;

  /// HTTP request method.
  // default value: DivActionSubmitRequestMethod.post
  final Expression<DivActionSubmitRequestMethod> method;

  /// Link for sending data from the container.
  final Expression<Uri> url;

  @override
  List<Object?> get props => [
        headers,
        method,
        url,
      ];

  DivActionSubmitRequest copyWith({
    Arr<DivActionSubmitRequestHeader>? Function()? headers,
    Expression<DivActionSubmitRequestMethod>? method,
    Expression<Uri>? url,
  }) =>
      DivActionSubmitRequest(
        headers: headers != null ? headers.call() : this.headers,
        method: method ?? this.method,
        url: url ?? this.url,
      );

  static DivActionSubmitRequest? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmitRequest(
        headers: safeParseObjects(
          json['headers'],
          (v) => reqProp<DivActionSubmitRequestHeader>(
            safeParseObject(
              v,
              parse: DivActionSubmitRequestHeader.fromJson,
            ),
          ),
        ),
        method: reqVProp<DivActionSubmitRequestMethod>(
          safeParseStrEnumExpr(
            json['method'],
            parse: DivActionSubmitRequestMethod.fromJson,
            fallback: DivActionSubmitRequestMethod.post,
          ),
          name: 'method',
        ),
        url: reqVProp<Uri>(
          safeParseUriExpr(
            json['url'],
          ),
          name: 'url',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionSubmitRequest resolve(DivVariableContext context) {
    tryResolveList(headers, (v) => v.resolve(context));
    method.resolve(context);
    url.resolve(context);
    return this;
  }
}

class DivActionSubmitRequestHeader extends Resolvable with EquatableMixin {
  const DivActionSubmitRequestHeader({
    required this.name,
    required this.value,
  });

  final Expression<String> name;
  final Expression<String> value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  DivActionSubmitRequestHeader copyWith({
    Expression<String>? name,
    Expression<String>? value,
  }) =>
      DivActionSubmitRequestHeader(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static DivActionSubmitRequestHeader? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmitRequestHeader(
        name: reqVProp<String>(
          safeParseStrExpr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqVProp<String>(
          safeParseStrExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionSubmitRequestHeader resolve(DivVariableContext context) {
    name.resolve(context);
    value.resolve(context);
    return this;
  }
}

enum DivActionSubmitRequestMethod implements Resolvable {
  get('get'),
  post('post'),
  put('put'),
  patch('patch'),
  delete('delete'),
  head('head'),
  options('options');

  final String value;

  const DivActionSubmitRequestMethod(this.value);
  bool get isGet => this == get;

  bool get isPost => this == post;

  bool get isPut => this == put;

  bool get isPatch => this == patch;

  bool get isDelete => this == delete;

  bool get isHead => this == head;

  bool get isOptions => this == options;

  T map<T>({
    required T Function() get,
    required T Function() post,
    required T Function() put,
    required T Function() patch,
    required T Function() delete,
    required T Function() head,
    required T Function() options,
  }) {
    switch (this) {
      case DivActionSubmitRequestMethod.get:
        return get();
      case DivActionSubmitRequestMethod.post:
        return post();
      case DivActionSubmitRequestMethod.put:
        return put();
      case DivActionSubmitRequestMethod.patch:
        return patch();
      case DivActionSubmitRequestMethod.delete:
        return delete();
      case DivActionSubmitRequestMethod.head:
        return head();
      case DivActionSubmitRequestMethod.options:
        return options();
    }
  }

  T maybeMap<T>({
    T Function()? get,
    T Function()? post,
    T Function()? put,
    T Function()? patch,
    T Function()? delete,
    T Function()? head,
    T Function()? options,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionSubmitRequestMethod.get:
        return get?.call() ?? orElse();
      case DivActionSubmitRequestMethod.post:
        return post?.call() ?? orElse();
      case DivActionSubmitRequestMethod.put:
        return put?.call() ?? orElse();
      case DivActionSubmitRequestMethod.patch:
        return patch?.call() ?? orElse();
      case DivActionSubmitRequestMethod.delete:
        return delete?.call() ?? orElse();
      case DivActionSubmitRequestMethod.head:
        return head?.call() ?? orElse();
      case DivActionSubmitRequestMethod.options:
        return options?.call() ?? orElse();
    }
  }

  static DivActionSubmitRequestMethod? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'get':
          return DivActionSubmitRequestMethod.get;
        case 'post':
          return DivActionSubmitRequestMethod.post;
        case 'put':
          return DivActionSubmitRequestMethod.put;
        case 'patch':
          return DivActionSubmitRequestMethod.patch;
        case 'delete':
          return DivActionSubmitRequestMethod.delete;
        case 'head':
          return DivActionSubmitRequestMethod.head;
        case 'options':
          return DivActionSubmitRequestMethod.options;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivActionSubmitRequestMethod: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivActionSubmitRequestMethod resolve(DivVariableContext context) => this;
}
