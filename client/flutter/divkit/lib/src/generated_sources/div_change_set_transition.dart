// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_change_transition.dart';

class DivChangeSetTransition with EquatableMixin {
  const DivChangeSetTransition({
    required this.items,
  });

  static const type = "set";
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

  static DivChangeSetTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivChangeSetTransition(
      items: safeParseObj(
        safeListMap(
            json['items'],
            (v) => safeParseObj(
                  DivChangeTransition.fromJson(v),
                )!),
      )!,
    );
  }
}
