// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Extension that affects an element.
class DivExtension extends Resolvable with EquatableMixin {
  const DivExtension({
    required this.id,
    this.params,
  });

  /// Extension ID.
  final String id;

  /// Additional extension parameters.
  final Obj? params;

  @override
  List<Object?> get props => [
        id,
        params,
      ];

  DivExtension copyWith({
    String? id,
    Obj? Function()? params,
  }) =>
      DivExtension(
        id: id ?? this.id,
        params: params != null ? params.call() : this.params,
      );

  static DivExtension? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivExtension(
        id: reqProp<String>(
          safeParseStr(
            json['id'],
          ),
          name: 'id',
        ),
        params: safeParseMap(
          json['params'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivExtension resolve(DivVariableContext context) {
    return this;
  }
}
