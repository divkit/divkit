// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action_typed.dart';
import 'div_download_callbacks.dart';
import 'div_sight_action.dart';

class DivVisibilityAction with EquatableMixin implements DivSightAction {
  const DivVisibilityAction({
    this.downloadCallbacks,
    this.isEnabled = const ValueExpression(true),
    required this.logId,
    this.logLimit = const ValueExpression(1),
    this.payload,
    this.referer,
    this.typed,
    this.url,
    this.visibilityDuration = const ValueExpression(800),
    this.visibilityPercentage = const ValueExpression(50),
  });

  @override
  final DivDownloadCallbacks? downloadCallbacks;
  // default value: true
  @override
  final Expression<bool> isEnabled;

  @override
  final Expression<String> logId;
  // constraint: number >= 0; default value: 1
  @override
  final Expression<int> logLimit;

  @override
  final Map<String, dynamic>? payload;

  @override
  final Expression<Uri>? referer;

  @override
  final DivActionTyped? typed;

  @override
  final Expression<Uri>? url;
  // constraint: number >= 0; default value: 800
  final Expression<int> visibilityDuration;
  // constraint: number > 0 && number <= 100; default value: 50
  final Expression<int> visibilityPercentage;

  @override
  List<Object?> get props => [
        downloadCallbacks,
        isEnabled,
        logId,
        logLimit,
        payload,
        referer,
        typed,
        url,
        visibilityDuration,
        visibilityPercentage,
      ];

  static DivVisibilityAction? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivVisibilityAction(
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
        json,
      ),
      referer: safeParseUriExpr(json['referer']),
      typed: safeParseObj(
        DivActionTyped.fromJson(json['typed']),
      ),
      url: safeParseUriExpr(json['url']),
      visibilityDuration: safeParseIntExpr(
        json['visibility_duration'],
        fallback: 800,
      )!,
      visibilityPercentage: safeParseIntExpr(
        json['visibility_percentage'],
        fallback: 50,
      )!,
    );
  }
}
