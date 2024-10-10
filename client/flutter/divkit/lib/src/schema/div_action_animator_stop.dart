// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Stops the specified animator.
class DivActionAnimatorStop extends Preloadable with EquatableMixin {
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

  static Future<DivActionAnimatorStop?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionAnimatorStop(
        animatorId: (await safeParseStrAsync(
          json['animator_id']?.toString(),
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
    try {} catch (e) {
      return;
    }
  }
}
