// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Callbacks that are called after [data loading](https://divkit.tech/docs/en/concepts/interaction#loading-data).
class DivDownloadCallbacks extends Resolvable with EquatableMixin {
  const DivDownloadCallbacks({
    this.onFailActions,
    this.onSuccessActions,
  });

  /// Actions in case of unsuccessful loading if the host reported it or the waiting time expired.
  final Arr<DivAction>? onFailActions;

  /// Actions in case of successful loading.
  final Arr<DivAction>? onSuccessActions;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
      ];

  DivDownloadCallbacks copyWith({
    Arr<DivAction>? Function()? onFailActions,
    Arr<DivAction>? Function()? onSuccessActions,
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
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivDownloadCallbacks resolve(DivVariableContext context) {
    tryResolveList(onFailActions, (v) => v.resolve(context));
    tryResolveList(onSuccessActions, (v) => v.resolve(context));
    return this;
  }
}
