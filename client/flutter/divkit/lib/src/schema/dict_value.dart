// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DictValue extends Preloadable with EquatableMixin {
  const DictValue({
    required this.value,
  });

  static const type = "dict";
  final Map<String, dynamic> value;

  @override
  List<Object?> get props => [
        value,
      ];

  DictValue copyWith({
    Map<String, dynamic>? value,
  }) =>
      DictValue(
        value: value ?? this.value,
      );

  static DictValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DictValue(
        value: safeParseMap(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DictValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DictValue(
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
