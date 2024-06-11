// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action.dart';

class DivDownloadCallbacks with EquatableMixin {
  const DivDownloadCallbacks({
    this.onFailActions,
    this.onSuccessActions,
  });

  final List<DivAction>? onFailActions;

  final List<DivAction>? onSuccessActions;

  @override
  List<Object?> get props => [
        onFailActions,
        onSuccessActions,
      ];

  static DivDownloadCallbacks? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivDownloadCallbacks(
      onFailActions: safeParseObj(
        safeListMap(
            json['on_fail_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      onSuccessActions: safeParseObj(
        safeListMap(
            json['on_success_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
    );
  }
}
