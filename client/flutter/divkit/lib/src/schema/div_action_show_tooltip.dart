// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Shows the tooltip.
class DivActionShowTooltip with EquatableMixin {
  const DivActionShowTooltip({
    required this.id,
    this.multiple,
  });

  static const type = "show_tooltip";

  /// Tooltip ID.
  final Expression<String> id;

  /// Sets whether the tooltip can be shown again after it’s closed.
  final Expression<bool>? multiple;

  @override
  List<Object?> get props => [
        id,
        multiple,
      ];

  DivActionShowTooltip copyWith({
    Expression<String>? id,
    Expression<bool>? Function()? multiple,
  }) =>
      DivActionShowTooltip(
        id: id ?? this.id,
        multiple: multiple != null ? multiple.call() : this.multiple,
      );

  static DivActionShowTooltip? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionShowTooltip(
        id: reqVProp<String>(
          safeParseStrExpr(
            json['id'],
          ),
          name: 'id',
        ),
        multiple: safeParseBoolExpr(
          json['multiple'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
