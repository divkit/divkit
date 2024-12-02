// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_typed.dart';
import 'package:divkit/src/schema/div_download_callbacks.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// It defines an action when clicking on an element.
class DivAction extends Resolvable with EquatableMixin {
  const DivAction({
    this.downloadCallbacks,
    this.isEnabled = const ValueExpression(true),
    required this.logId,
    this.logUrl,
    this.menuItems,
    this.payload,
    this.referer,
    this.scopeId,
    this.target,
    this.typed,
    this.url,
  });

  /// Callbacks that are called after [data loading](https://divkit.tech/docs/en/concepts/interaction#loading-data).
  final DivDownloadCallbacks? downloadCallbacks;

  /// The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
  // default value: true
  final Expression<bool> isEnabled;

  /// Logging ID.
  final Expression<String> logId;

  /// URL for logging.
  final Expression<Uri>? logUrl;

  /// Context menu.
  final Arr<DivActionMenuItem>? menuItems;

  /// Additional parameters, passed to the host application.
  final Obj? payload;

  /// Referer URL for logging.
  final Expression<Uri>? referer;

  /// The ID of the element within which the specified action will be performed.
  final String? scopeId;

  /// The tab in which the URL must be opened.
  final Expression<DivActionTarget>? target;
  final DivActionTyped? typed;

  /// URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](https://divkit.tech/docs/en/concepts/interaction).
  final Expression<Uri>? url;

  @override
  List<Object?> get props => [
        downloadCallbacks,
        isEnabled,
        logId,
        logUrl,
        menuItems,
        payload,
        referer,
        scopeId,
        target,
        typed,
        url,
      ];

  DivAction copyWith({
    DivDownloadCallbacks? Function()? downloadCallbacks,
    Expression<bool>? isEnabled,
    Expression<String>? logId,
    Expression<Uri>? Function()? logUrl,
    Arr<DivActionMenuItem>? Function()? menuItems,
    Obj? Function()? payload,
    Expression<Uri>? Function()? referer,
    String? Function()? scopeId,
    Expression<DivActionTarget>? Function()? target,
    DivActionTyped? Function()? typed,
    Expression<Uri>? Function()? url,
  }) =>
      DivAction(
        downloadCallbacks: downloadCallbacks != null
            ? downloadCallbacks.call()
            : this.downloadCallbacks,
        isEnabled: isEnabled ?? this.isEnabled,
        logId: logId ?? this.logId,
        logUrl: logUrl != null ? logUrl.call() : this.logUrl,
        menuItems: menuItems != null ? menuItems.call() : this.menuItems,
        payload: payload != null ? payload.call() : this.payload,
        referer: referer != null ? referer.call() : this.referer,
        scopeId: scopeId != null ? scopeId.call() : this.scopeId,
        target: target != null ? target.call() : this.target,
        typed: typed != null ? typed.call() : this.typed,
        url: url != null ? url.call() : this.url,
      );

  static DivAction? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAction(
        downloadCallbacks: safeParseObject(
          json['download_callbacks'],
          parse: DivDownloadCallbacks.fromJson,
        ),
        isEnabled: reqVProp<bool>(
          safeParseBoolExpr(
            json['is_enabled'],
            fallback: true,
          ),
          name: 'is_enabled',
        ),
        logId: reqVProp<String>(
          safeParseStrExpr(
            json['log_id'],
          ),
          name: 'log_id',
        ),
        logUrl: safeParseUriExpr(
          json['log_url'],
        ),
        menuItems: safeParseObjects(
          json['menu_items'],
          (v) => reqProp<DivActionMenuItem>(
            safeParseObject(
              v,
              parse: DivActionMenuItem.fromJson,
            ),
          ),
        ),
        payload: safeParseMap(
          json['payload'],
        ),
        referer: safeParseUriExpr(
          json['referer'],
        ),
        scopeId: safeParseStr(
          json['scope_id'],
        ),
        target: safeParseStrEnumExpr(
          json['target'],
          parse: DivActionTarget.fromJson,
        ),
        typed: safeParseObject(
          json['typed'],
          parse: DivActionTyped.fromJson,
        ),
        url: safeParseUriExpr(
          json['url'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivAction resolve(DivVariableContext context) {
    downloadCallbacks?.resolve(context);
    isEnabled.resolve(context);
    logId.resolve(context);
    logUrl?.resolve(context);
    tryResolveList(menuItems, (v) => v.resolve(context));
    referer?.resolve(context);
    target?.resolve(context);
    typed?.resolve(context);
    url?.resolve(context);
    return this;
  }
}

class DivActionMenuItem extends Resolvable with EquatableMixin {
  const DivActionMenuItem({
    this.action,
    this.actions,
    required this.text,
  });

  /// One action when clicking on a menu item. Not used if the `actions` parameter is set.
  final DivAction? action;

  /// Multiple actions when clicking on a menu item.
  final Arr<DivAction>? actions;

  /// Menu item title.
  final Expression<String> text;

  @override
  List<Object?> get props => [
        action,
        actions,
        text,
      ];

  DivActionMenuItem copyWith({
    DivAction? Function()? action,
    Arr<DivAction>? Function()? actions,
    Expression<String>? text,
  }) =>
      DivActionMenuItem(
        action: action != null ? action.call() : this.action,
        actions: actions != null ? actions.call() : this.actions,
        text: text ?? this.text,
      );

  static DivActionMenuItem? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionMenuItem(
        action: safeParseObject(
          json['action'],
          parse: DivAction.fromJson,
        ),
        actions: safeParseObjects(
          json['actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        text: reqVProp<String>(
          safeParseStrExpr(
            json['text'],
          ),
          name: 'text',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionMenuItem resolve(DivVariableContext context) {
    action?.resolve(context);
    tryResolveList(actions, (v) => v.resolve(context));
    text.resolve(context);
    return this;
  }
}

enum DivActionTarget implements Resolvable {
  self('_self'),
  blank('_blank');

  final String value;

  const DivActionTarget(this.value);
  bool get isSelf => this == self;

  bool get isBlank => this == blank;

  T map<T>({
    required T Function() self,
    required T Function() blank,
  }) {
    switch (this) {
      case DivActionTarget.self:
        return self();
      case DivActionTarget.blank:
        return blank();
    }
  }

  T maybeMap<T>({
    T Function()? self,
    T Function()? blank,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionTarget.self:
        return self?.call() ?? orElse();
      case DivActionTarget.blank:
        return blank?.call() ?? orElse();
    }
  }

  static DivActionTarget? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case '_self':
          return DivActionTarget.self;
        case '_blank':
          return DivActionTarget.blank;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivActionTarget: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivActionTarget resolve(DivVariableContext context) => this;
}
