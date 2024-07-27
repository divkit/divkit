// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivActionFocusElement with EquatableMixin {
  const DivActionFocusElement({
    required this.elementId,
  });

  static const type = "focus_element";

  final Expression<String> elementId;

  @override
  List<Object?> get props => [
        elementId,
      ];

  DivActionFocusElement copyWith({
    Expression<String>? elementId,
  }) =>
      DivActionFocusElement(
        elementId: elementId ?? this.elementId,
      );

  static DivActionFocusElement? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionFocusElement(
        elementId: safeParseStrExpr(
          json['element_id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
