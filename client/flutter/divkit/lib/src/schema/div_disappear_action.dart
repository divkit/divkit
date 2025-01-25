// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_typed.dart';
import 'package:divkit/src/schema/div_download_callbacks.dart';
import 'package:divkit/src/schema/div_sight_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Actions performed when an element is no longer visible.
class DivDisappearAction extends Resolvable
    with EquatableMixin
    implements DivSightAction {
  const DivDisappearAction({
    this.disappearDuration = const ValueExpression(800),
    this.downloadCallbacks,
    this.isEnabled = const ValueExpression(true),
    required this.logId,
    this.logLimit = const ValueExpression(1),
    this.payload,
    this.referer,
    this.scopeId,
    this.typed,
    this.url,
    this.visibilityPercentage = const ValueExpression(0),
  });

  /// Time in milliseconds during which an element must be outside the visible area to trigger `disappear-action`.
  // constraint: number >= 0; default value: 800
  final Expression<int> disappearDuration;

  /// Callbacks that are called after [data loading](https://divkit.tech/docs/en/concepts/interaction#loading-data).
  @override
  final DivDownloadCallbacks? downloadCallbacks;

  /// The parameter disables the action. Disabled actions stop listening to their associated event (clicks, changes in visibility, and so on).
  // default value: true
  @override
  final Expression<bool> isEnabled;

  /// Logging ID.
  @override
  final Expression<String> logId;

  /// Limit on the number of loggings. If `0`, the limit is removed.
  // constraint: number >= 0; default value: 1
  @override
  final Expression<int> logLimit;

  /// Additional parameters, passed to the host application.
  @override
  final Map<String, dynamic>? payload;

  /// Referer URL for logging.
  @override
  final Expression<Uri>? referer;

  /// The ID of the element within which the specified action will be performed.
  @override
  final String? scopeId;
  @override
  final DivActionTyped? typed;

  /// URL. Possible values: `url` or `div-action://`. To learn more, see [Interaction with elements](https://divkit.tech/docs/en/concepts/interaction).
  @override
  final Expression<Uri>? url;

  /// Percentage of the visible part of an element that triggers `disappear-action`.
  // constraint: number >= 0 && number < 100; default value: 0
  final Expression<int> visibilityPercentage;

  @override
  List<Object?> get props => [
        disappearDuration,
        downloadCallbacks,
        isEnabled,
        logId,
        logLimit,
        payload,
        referer,
        scopeId,
        typed,
        url,
        visibilityPercentage,
      ];

  DivDisappearAction copyWith({
    Expression<int>? disappearDuration,
    DivDownloadCallbacks? Function()? downloadCallbacks,
    Expression<bool>? isEnabled,
    Expression<String>? logId,
    Expression<int>? logLimit,
    Map<String, dynamic>? Function()? payload,
    Expression<Uri>? Function()? referer,
    String? Function()? scopeId,
    DivActionTyped? Function()? typed,
    Expression<Uri>? Function()? url,
    Expression<int>? visibilityPercentage,
  }) =>
      DivDisappearAction(
        disappearDuration: disappearDuration ?? this.disappearDuration,
        downloadCallbacks: downloadCallbacks != null
            ? downloadCallbacks.call()
            : this.downloadCallbacks,
        isEnabled: isEnabled ?? this.isEnabled,
        logId: logId ?? this.logId,
        logLimit: logLimit ?? this.logLimit,
        payload: payload != null ? payload.call() : this.payload,
        referer: referer != null ? referer.call() : this.referer,
        scopeId: scopeId != null ? scopeId.call() : this.scopeId,
        typed: typed != null ? typed.call() : this.typed,
        url: url != null ? url.call() : this.url,
        visibilityPercentage: visibilityPercentage ?? this.visibilityPercentage,
      );

  static DivDisappearAction? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivDisappearAction(
        disappearDuration: safeParseIntExpr(
          json['disappear_duration'],
          fallback: 800,
        )!,
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
        logLimit: safeParseIntExpr(
          json['log_limit'],
          fallback: 1,
        )!,
        payload: safeParseMap(
          json['payload'],
        ),
        referer: safeParseUriExpr(json['referer']),
        scopeId: safeParseStr(
          json['scope_id']?.toString(),
        ),
        typed: safeParseObj(
          DivActionTyped.fromJson(json['typed']),
        ),
        url: safeParseUriExpr(json['url']),
        visibilityPercentage: safeParseIntExpr(
          json['visibility_percentage'],
          fallback: 0,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivDisappearAction resolve(DivVariableContext context) {
    disappearDuration.resolve(context);
    downloadCallbacks?.resolve(context);
    isEnabled.resolve(context);
    logId.resolve(context);
    logLimit.resolve(context);
    referer?.resolve(context);
    typed?.resolve(context);
    url?.resolve(context);
    visibilityPercentage.resolve(context);
    return this;
  }
}
