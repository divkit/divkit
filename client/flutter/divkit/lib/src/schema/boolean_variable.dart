// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class BooleanVariable extends Preloadable with EquatableMixin {
  const BooleanVariable({
    required this.name,
    required this.value,
  });

  static const type = "boolean";

  final String name;

  final bool value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  BooleanVariable copyWith({
    String? name,
    bool? value,
  }) =>
      BooleanVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static BooleanVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return BooleanVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseBool(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<BooleanVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return BooleanVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseBoolAsync(
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
