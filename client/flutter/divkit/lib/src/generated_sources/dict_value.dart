// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DictValue with EquatableMixin {
  const DictValue({
    required this.value,
  });

  static const type = "dict";

  final Map<String, dynamic> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static DictValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DictValue(
      value: safeParseMap(
        json,
      )!,
    );
  }
}
