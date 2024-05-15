// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivExtension with EquatableMixin {
  const DivExtension({
    required this.id,
    this.params,
  });

  final String id;

  final Map<String, dynamic>? params;

  @override
  List<Object?> get props => [
        id,
        params,
      ];

  static DivExtension? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivExtension(
      id: safeParseStr(
        json['id']?.toString(),
      )!,
      params: safeParseMap(
        json,
      ),
    );
  }
}
