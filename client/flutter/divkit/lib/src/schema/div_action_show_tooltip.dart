// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Shows tooltip.
class DivActionShowTooltip extends Preloadable with EquatableMixin {
  const DivActionShowTooltip({
    required this.id,
    this.multiple,
  });

  static const type = "show_tooltip";

  /// Tooltip identifier.
  final Expression<String> id;

  /// Defines whether tooltip can be shown again after being closed.
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

  static Future<DivActionShowTooltip?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionShowTooltip(
        id: (await safeParseStrExprAsync(
          json['id']?.toString(),
        ))!,
        multiple: await safeParseBoolExprAsync(
          json['multiple'],
        ),
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
      await id.preload(context);
      await multiple?.preload(context);
    } catch (e) {
      return;
    }
  }
}
