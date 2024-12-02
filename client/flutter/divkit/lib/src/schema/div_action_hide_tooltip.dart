// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Hides the tooltip.
class DivActionHideTooltip extends Resolvable with EquatableMixin {
  const DivActionHideTooltip({
    required this.id,
  });

  static const type = "hide_tooltip";

  /// Tooltip ID.
  final Expression<String> id;

  @override
  List<Object?> get props => [
        id,
      ];

  DivActionHideTooltip copyWith({
    Expression<String>? id,
  }) =>
      DivActionHideTooltip(
        id: id ?? this.id,
      );

  static DivActionHideTooltip? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionHideTooltip(
        id: reqVProp<String>(
          safeParseStrExpr(
            json['id'],
          ),
          name: 'id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionHideTooltip resolve(DivVariableContext context) {
    id.resolve(context);
    return this;
  }
}
