// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A floating-point variable.
class NumberVariable extends Preloadable with EquatableMixin {
  const NumberVariable({
    required this.name,
    required this.value,
  });

  static const type = "number";

  /// Variable name.
  final String name;

  /// Value.
  final double value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  NumberVariable copyWith({
    String? name,
    double? value,
  }) =>
      NumberVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static NumberVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return NumberVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseDouble(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<NumberVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return NumberVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseDoubleAsync(
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
    try {} catch (e) {
      return;
    }
  }
}
