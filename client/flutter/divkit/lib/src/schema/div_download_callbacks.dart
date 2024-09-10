// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Callbacks that are called after [data loading](https://divkit.tech/docs/en/concepts/interaction#loading-data).
class DivDownloadCallbacks extends Preloadable with EquatableMixin {
  const DivDownloadCallbacks({
    this.onFailActions,
    this.onSuccessActions,
  });

  /// Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
  final List<DivAction>? onFailActions;

  /// Actions in case of successful loading.
  final List<DivAction>? onSuccessActions;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
      ];

  DivDownloadCallbacks copyWith({
    List<DivAction>? Function()? onFailActions,
    List<DivAction>? Function()? onSuccessActions,
  }) =>
      DivDownloadCallbacks(
        onFailActions:
            onFailActions != null ? onFailActions.call() : this.onFailActions,
        onSuccessActions: onSuccessActions != null
            ? onSuccessActions.call()
            : this.onSuccessActions,
      );

  static DivDownloadCallbacks? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivDownloadCallbacks(
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
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivDownloadCallbacks?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivDownloadCallbacks(
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
      await safeFuturesWait(onFailActions, (v) => v.preload(context));
      await safeFuturesWait(onSuccessActions, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}
