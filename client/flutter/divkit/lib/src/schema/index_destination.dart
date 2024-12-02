// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Specifies the element with the given index as the scrolling end position.
class IndexDestination extends Resolvable with EquatableMixin {
  const IndexDestination({
    required this.value,
  });

  static const type = "index";

  /// Container element index.
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
        value: reqVProp<int>(
          safeParseIntExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  IndexDestination resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
