// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Shows the tooltip.
class DivActionShowTooltip extends Resolvable with EquatableMixin {
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
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
        multiple: safeParseBoolExpr(
          json['multiple'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivActionShowTooltip resolve(DivVariableContext context) {
    id.resolve(context);
    multiple?.resolve(context);
    return this;
  }
}
