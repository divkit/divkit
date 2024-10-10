// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Hides the tooltip.
class DivActionHideTooltip extends Preloadable with EquatableMixin {
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
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionHideTooltip?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionHideTooltip(
        id: (await safeParseStrExprAsync(
          json['id']?.toString(),
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
      await id.preload(context);
    } catch (e) {
      return;
    }
  }
}
