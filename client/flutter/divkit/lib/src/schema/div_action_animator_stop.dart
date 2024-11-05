// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Stops the specified animator.
class DivActionAnimatorStop extends Resolvable with EquatableMixin {
  const DivActionAnimatorStop({
    required this.animatorId,
  });

  static const type = "animator_stop";

  /// The identifier of the animator being stopped.
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
        animatorId: safeParseStr(
          json['animator_id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivActionAnimatorStop resolve(DivVariableContext context) {
    return this;
  }
}
