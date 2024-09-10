// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Requests focus for an element. May require a user action on the web.
class DivActionFocusElement extends Preloadable with EquatableMixin {
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

  static DivActionFocusElement? fromJson(
    Map<String, dynamic>? json,
  ) {
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

  static Future<DivActionFocusElement?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionFocusElement(
        elementId: (await safeParseStrExprAsync(
          json['element_id']?.toString(),
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
    try {
      await elementId.preload(context);
    } catch (e) {
      return;
    }
  }
}
