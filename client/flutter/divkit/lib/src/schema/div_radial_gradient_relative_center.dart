// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Location of the central point of the gradient relative to the element borders.
class DivRadialGradientRelativeCenter extends Preloadable with EquatableMixin {
  const DivRadialGradientRelativeCenter({
    required this.value,
  });

  static const type = "relative";

  /// Coordinate value in the range "0...1".
  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DivRadialGradientRelativeCenter copyWith({
    Expression<double>? value,
  }) =>
      DivRadialGradientRelativeCenter(
        value: value ?? this.value,
      );

  static DivRadialGradientRelativeCenter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeCenter(
        value: safeParseDoubleExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivRadialGradientRelativeCenter?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivRadialGradientRelativeCenter(
        value: (await safeParseDoubleExprAsync(
          json['value'],
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
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
