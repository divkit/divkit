// Generated code. Do not modify.

import 'package:divkit/src/schema/div_appearance_transition.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A set of animations to be applied simultaneously.
class DivAppearanceSetTransition extends Resolvable with EquatableMixin {
  const DivAppearanceSetTransition({
    required this.items,
  });

  static const type = "set";

  /// An array of animations.
  // at least 1 elements
  final List<DivAppearanceTransition> items;

  @override
  List<Object?> get props => [
        items,
      ];

  DivAppearanceSetTransition copyWith({
    List<DivAppearanceTransition>? items,
  }) =>
      DivAppearanceSetTransition(
        items: items ?? this.items,
      );

  static DivAppearanceSetTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivAppearanceSetTransition(
        items: safeParseObj(
          safeListMap(
            json['items'],
            (v) => safeParseObj(
              DivAppearanceTransition.fromJson(v),
            )!,
          ),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivAppearanceSetTransition resolve(DivVariableContext context) {
    return this;
  }
}
