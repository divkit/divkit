// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Stops the specified animator.
class DivActionAnimatorStop with EquatableMixin {
  const DivActionAnimatorStop({
    required this.animatorId,
  });

  static const type = "animator_stop";

  /// ID of the animator to be stopped.
  final String animatorId;

  @override
  List<Object?> get props => [
        animatorId,
      ];

  DivActionAnimatorStop copyWith({
    String? animatorId,
  }) =>
      DivActionAnimatorStop(
        animatorId: animatorId ?? this.animatorId,
      );

  static DivActionAnimatorStop? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionAnimatorStop(
        animatorId: reqProp<String>(
          safeParseStr(
            json['animator_id'],
          ),
          name: 'animator_id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
