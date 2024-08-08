// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_typed.dart';
import 'package:divkit/src/schema/div_download_callbacks.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivAction extends Preloadable with EquatableMixin {
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

  DivAction copyWith({
    DivDownloadCallbacks? Function()? downloadCallbacks,
    Expression<bool>? isEnabled,
    Expression<String>? logId,
    Expression<Uri>? Function()? logUrl,
    List<DivActionMenuItem>? Function()? menuItems,
    Map<String, dynamic>? Function()? payload,
    Expression<Uri>? Function()? referer,
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
            )!,
          ),
        ),
        payload: safeParseMap(
          json['payload'],
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
    } catch (e) {
      return null;
    }
  }

  static Future<DivAction?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivAction(
        downloadCallbacks: await safeParseObjAsync(
          DivDownloadCallbacks.fromJson(json['download_callbacks']),
        ),
        isEnabled: (await safeParseBoolExprAsync(
          json['is_enabled'],
          fallback: true,
        ))!,
        logId: (await safeParseStrExprAsync(
          json['log_id']?.toString(),
        ))!,
        logUrl: await safeParseUriExprAsync(json['log_url']),
        menuItems: await safeParseObjAsync(
          await safeListMapAsync(
            json['menu_items'],
            (v) => safeParseObj(
              DivActionMenuItem.fromJson(v),
            )!,
          ),
        ),
        payload: await safeParseMapAsync(
          json['payload'],
        ),
        referer: await safeParseUriExprAsync(json['referer']),
        target: await safeParseStrEnumExprAsync(
          json['target'],
          parse: DivActionTarget.fromJson,
        ),
        typed: await safeParseObjAsync(
          DivActionTyped.fromJson(json['typed']),
        ),
        url: await safeParseUriExprAsync(json['url']),
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
      await downloadCallbacks?.preload(context);
      await isEnabled.preload(context);
      await logId.preload(context);
      await logUrl?.preload(context);
      await safeFuturesWait(menuItems, (v) => v.preload(context));
      await referer?.preload(context);
      await target?.preload(context);
      await typed?.preload(context);
      await url?.preload(context);
    } catch (e) {
      return;
    }
  }
}

class DivActionMenuItem extends Preloadable with EquatableMixin {
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

  DivActionMenuItem copyWith({
    DivAction? Function()? action,
    List<DivAction>? Function()? actions,
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
        action: safeParseObj(
          DivAction.fromJson(json['action']),
        ),
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        text: safeParseStrExpr(
          json['text']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionMenuItem?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionMenuItem(
        action: await safeParseObjAsync(
          DivAction.fromJson(json['action']),
        ),
        actions: await safeParseObjAsync(
          await safeListMapAsync(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        text: (await safeParseStrExprAsync(
          json['text']?.toString(),
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
      await action?.preload(context);
      await safeFuturesWait(actions, (v) => v.preload(context));
      await text.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivActionTarget implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionTarget?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}
