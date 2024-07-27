// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_appearance_transition.dart';

class DivAppearanceSetTransition with EquatableMixin {
  const DivAppearanceSetTransition({
    required this.items,
  });

  static const type = "set";
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

  static DivAppearanceSetTransition? fromJson(Map<String, dynamic>? json) {
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
}
