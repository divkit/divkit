// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// An arbitrary array in JSON format.
class ArrayVariable extends Preloadable with EquatableMixin {
  const ArrayVariable({
    required this.name,
    required this.value,
  });

  static const type = "array";

  /// Variable name.
  final String name;

  /// Value.
  final List<dynamic> value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  ArrayVariable copyWith({
    String? name,
    List<dynamic>? value,
  }) =>
      ArrayVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static ArrayVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ArrayVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseList(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<ArrayVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return ArrayVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseListAsync(
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
