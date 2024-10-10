// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Sends variables from the container via a url. The data sending configuration can be determined by the host application. By default, variables are passed in body in json format, the request method is POST.
class DivActionSubmit extends Preloadable with EquatableMixin {
  const DivActionSubmit({
    required this.containerId,
    this.onFailActions,
    this.onSuccessActions,
    required this.request,
  });

  static const type = "submit";

  /// The identifier of the container that contains variables to submit.
  final Expression<String> containerId;

  /// Actions in case of unsuccessful submit.
  final List<DivAction>? onFailActions;

  /// Actions in case of successful submit.
  final List<DivAction>? onSuccessActions;

  /// The HTTP request parameters that are used to configure how data is sent.
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
    List<DivAction>? Function()? onFailActions,
    List<DivAction>? Function()? onSuccessActions,
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
        containerId: safeParseStrExpr(
          json['container_id']?.toString(),
        )!,
        onFailActions: safeParseObj(
          safeListMap(
            json['on_fail_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        onSuccessActions: safeParseObj(
          safeListMap(
            json['on_success_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        request: safeParseObj(
          DivActionSubmitRequest.fromJson(json['request']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSubmit?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmit(
        containerId: (await safeParseStrExprAsync(
          json['container_id']?.toString(),
        ))!,
        onFailActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['on_fail_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        onSuccessActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['on_success_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        request: (await safeParseObjAsync(
          DivActionSubmitRequest.fromJson(json['request']),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await containerId.preload(context);
      await safeFuturesWait(onFailActions, (v) => v.preload(context));
      await safeFuturesWait(onSuccessActions, (v) => v.preload(context));
      await request.preload(context);
    } catch (e) {
      return;
    }
  }
}

/// The HTTP request parameters that are used to configure how data is sent.
class DivActionSubmitRequest extends Preloadable with EquatableMixin {
  const DivActionSubmitRequest({
    this.headers,
    this.method = const ValueExpression(DivActionSubmitRequestMethod.pOST),
    required this.url,
  });

  /// The HTTP request headers.
  final List<DivActionSubmitRequestHeader>? headers;

  /// The HTTP request method.
  // default value: DivActionSubmitRequestMethod.pOST
  final Expression<DivActionSubmitRequestMethod> method;

  /// The url to which data from the container is sent.
  final Expression<Uri> url;

  @override
  List<Object?> get props => [
        headers,
        method,
        url,
      ];

  DivActionSubmitRequest copyWith({
    List<DivActionSubmitRequestHeader>? Function()? headers,
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
        headers: safeParseObj(
          safeListMap(
            json['headers'],
            (v) => safeParseObj(
              DivActionSubmitRequestHeader.fromJson(v),
            )!,
          ),
        ),
        method: safeParseStrEnumExpr(
          json['method'],
          parse: DivActionSubmitRequestMethod.fromJson,
          fallback: DivActionSubmitRequestMethod.pOST,
        )!,
        url: safeParseUriExpr(json['url'])!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSubmitRequest?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmitRequest(
        headers: await safeParseObjAsync(
          await safeListMapAsync(
            json['headers'],
            (v) => safeParseObj(
              DivActionSubmitRequestHeader.fromJson(v),
            )!,
          ),
        ),
        method: (await safeParseStrEnumExprAsync(
          json['method'],
          parse: DivActionSubmitRequestMethod.fromJson,
          fallback: DivActionSubmitRequestMethod.pOST,
        ))!,
        url: (await safeParseUriExprAsync(json['url']))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await safeFuturesWait(headers, (v) => v.preload(context));
      await method.preload(context);
      await url.preload(context);
    } catch (e) {
      return;
    }
  }
}

class DivActionSubmitRequestHeader extends Preloadable with EquatableMixin {
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
        name: safeParseStrExpr(
          json['name']?.toString(),
        )!,
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSubmitRequestHeader?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSubmitRequestHeader(
        name: (await safeParseStrExprAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseStrExprAsync(
          json['value']?.toString(),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await name.preload(context);
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivActionSubmitRequestMethod implements Preloadable {
  gET('GET'),
  pOST('POST'),
  pUT('PUT'),
  pATCH('PATCH'),
  dELETE('DELETE'),
  hEAD('HEAD'),
  oPTIONS('OPTIONS');

  final String value;

  const DivActionSubmitRequestMethod(this.value);
  bool get isGET => this == gET;

  bool get isPOST => this == pOST;

  bool get isPUT => this == pUT;

  bool get isPATCH => this == pATCH;

  bool get isDELETE => this == dELETE;

  bool get isHEAD => this == hEAD;

  bool get isOPTIONS => this == oPTIONS;

  T map<T>({
    required T Function() gET,
    required T Function() pOST,
    required T Function() pUT,
    required T Function() pATCH,
    required T Function() dELETE,
    required T Function() hEAD,
    required T Function() oPTIONS,
  }) {
    switch (this) {
      case DivActionSubmitRequestMethod.gET:
        return gET();
      case DivActionSubmitRequestMethod.pOST:
        return pOST();
      case DivActionSubmitRequestMethod.pUT:
        return pUT();
      case DivActionSubmitRequestMethod.pATCH:
        return pATCH();
      case DivActionSubmitRequestMethod.dELETE:
        return dELETE();
      case DivActionSubmitRequestMethod.hEAD:
        return hEAD();
      case DivActionSubmitRequestMethod.oPTIONS:
        return oPTIONS();
    }
  }

  T maybeMap<T>({
    T Function()? gET,
    T Function()? pOST,
    T Function()? pUT,
    T Function()? pATCH,
    T Function()? dELETE,
    T Function()? hEAD,
    T Function()? oPTIONS,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionSubmitRequestMethod.gET:
        return gET?.call() ?? orElse();
      case DivActionSubmitRequestMethod.pOST:
        return pOST?.call() ?? orElse();
      case DivActionSubmitRequestMethod.pUT:
        return pUT?.call() ?? orElse();
      case DivActionSubmitRequestMethod.pATCH:
        return pATCH?.call() ?? orElse();
      case DivActionSubmitRequestMethod.dELETE:
        return dELETE?.call() ?? orElse();
      case DivActionSubmitRequestMethod.hEAD:
        return hEAD?.call() ?? orElse();
      case DivActionSubmitRequestMethod.oPTIONS:
        return oPTIONS?.call() ?? orElse();
    }
  }

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

  static DivActionSubmitRequestMethod? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'GET':
          return DivActionSubmitRequestMethod.gET;
        case 'POST':
          return DivActionSubmitRequestMethod.pOST;
        case 'PUT':
          return DivActionSubmitRequestMethod.pUT;
        case 'PATCH':
          return DivActionSubmitRequestMethod.pATCH;
        case 'DELETE':
          return DivActionSubmitRequestMethod.dELETE;
        case 'HEAD':
          return DivActionSubmitRequestMethod.hEAD;
        case 'OPTIONS':
          return DivActionSubmitRequestMethod.oPTIONS;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSubmitRequestMethod?> parse(
    String? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'GET':
          return DivActionSubmitRequestMethod.gET;
        case 'POST':
          return DivActionSubmitRequestMethod.pOST;
        case 'PUT':
          return DivActionSubmitRequestMethod.pUT;
        case 'PATCH':
          return DivActionSubmitRequestMethod.pATCH;
        case 'DELETE':
          return DivActionSubmitRequestMethod.dELETE;
        case 'HEAD':
          return DivActionSubmitRequestMethod.hEAD;
        case 'OPTIONS':
          return DivActionSubmitRequestMethod.oPTIONS;
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
