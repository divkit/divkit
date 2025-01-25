// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies element with provided index as scroll destination.
class IndexDestination extends Resolvable with EquatableMixin {
  const IndexDestination({
    required this.value,
  });

  static const type = "index";

  /// Index of contaner's item.
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  IndexDestination copyWith({
    Expression<int>? value,
  }) =>
      IndexDestination(
        value: value ?? this.value,
      );

  static IndexDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return IndexDestination(
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  IndexDestination resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
