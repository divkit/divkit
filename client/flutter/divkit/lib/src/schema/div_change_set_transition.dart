// Generated code. Do not modify.

import 'package:divkit/src/schema/div_change_transition.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Animations.
class DivChangeSetTransition extends Resolvable with EquatableMixin {
  const DivChangeSetTransition({
    required this.items,
  });

  static const type = "set";

  /// List of animations.
  // at least 1 elements
  final List<DivChangeTransition> items;

  @override
  List<Object?> get props => [
        items,
      ];

  DivChangeSetTransition copyWith({
    List<DivChangeTransition>? items,
  }) =>
      DivChangeSetTransition(
        items: items ?? this.items,
      );

  static DivChangeSetTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivChangeSetTransition(
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              DivChangeTransition.fromJson(v),
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivChangeSetTransition resolve(DivVariableContext context) {
    return this;
  }
}
