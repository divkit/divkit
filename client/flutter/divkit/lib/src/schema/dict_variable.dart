// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DictVariable extends Preloadable with EquatableMixin {
  const DictVariable({
    required this.name,
    required this.value,
  });

  static const type = "dict";

  final String name;

  final Map<String, dynamic> value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  DictVariable copyWith({
    String? name,
    Map<String, dynamic>? value,
  }) =>
      DictVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static DictVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DictVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseMap(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DictVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DictVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseMapAsync(
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
