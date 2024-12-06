// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Requests focus for an element. May require a user action on the web.
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

  static DivActionFocusElement? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionFocusElement(
        elementId: reqVProp<String>(
          safeParseStrExpr(
            json['element_id'],
          ),
          name: 'element_id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
