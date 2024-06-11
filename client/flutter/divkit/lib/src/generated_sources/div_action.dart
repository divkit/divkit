// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action_typed.dart';
import 'div_download_callbacks.dart';

class DivAction with EquatableMixin {
  const DivAction({
    this.downloadCallbacks,
    this.isEnabled = const ValueExpression(true),
    required this.logId,
    this.logUrl,
    this.menuItems,
    this.payload,
    this.referer,
    this.target,
    this.typed,
    this.url,
  });

  final DivDownloadCallbacks? downloadCallbacks;
  // default value: true
  final Expression<bool> isEnabled;

  final Expression<String> logId;

  final Expression<Uri>? logUrl;

  final List<DivActionMenuItem>? menuItems;

  final Map<String, dynamic>? payload;

  final Expression<Uri>? referer;

  final Expression<DivActionTarget>? target;

  final DivActionTyped? typed;

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
        target,
        typed,
        url,
      ];

  static DivAction? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivAction(
      downloadCallbacks: safeParseObj(
        DivDownloadCallbacks.fromJson(json['download_callbacks']),
      ),
      isEnabled: safeParseBoolExpr(
        json['is_enabled'],
        fallback: true,
      )!,
      logId: safeParseStrExpr(
        json['log_id']?.toString(),
      )!,
      logUrl: safeParseUriExpr(json['log_url']),
      menuItems: safeParseObj(
        safeListMap(
            json['menu_items'],
            (v) => safeParseObj(
                  DivActionMenuItem.fromJson(v),
                )!),
      ),
      payload: safeParseMap(
        json,
      ),
      referer: safeParseUriExpr(json['referer']),
      target: safeParseStrEnumExpr(
        json['target'],
        parse: DivActionTarget.fromJson,
      ),
      typed: safeParseObj(
        DivActionTyped.fromJson(json['typed']),
      ),
      url: safeParseUriExpr(json['url']),
    );
  }
}

class DivActionMenuItem with EquatableMixin {
  const DivActionMenuItem({
    this.action,
    this.actions,
    required this.text,
  });

  final DivAction? action;

  final List<DivAction>? actions;

  final Expression<String> text;

  @override
  List<Object?> get props => [
        action,
        actions,
        text,
      ];

  static DivActionMenuItem? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivActionMenuItem(
      action: safeParseObj(
        DivAction.fromJson(json['action']),
      ),
      actions: safeParseObj(
        safeListMap(
            json['actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      text: safeParseStrExpr(
        json['text']?.toString(),
      )!,
    );
  }
}

enum DivActionTarget {
  self('_self'),
  blank('_blank');

  final String value;

  const DivActionTarget(this.value);

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

  static DivActionTarget? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case '_self':
        return DivActionTarget.self;
      case '_blank':
        return DivActionTarget.blank;
    }
    return null;
  }
}
