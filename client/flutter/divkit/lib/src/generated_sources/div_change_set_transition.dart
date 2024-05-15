// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_change_transition.dart';

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

  static DivChangeSetTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivChangeSetTransition(
      items: safeParseObj(
        (json['items'] as List<dynamic>)
            .map(
              (v) => safeParseObj(
                DivChangeTransition.fromJson(v),
              )!,
            )
            .toList(),
      )!,
    );
  }
}
