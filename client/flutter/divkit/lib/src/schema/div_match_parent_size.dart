// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Element size adjusts to a parent element.
class DivMatchParentSize extends Resolvable with EquatableMixin {
  const DivMatchParentSize({
    this.weight,
  });

  static const type = "match_parent";

  /// Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally.
  // constraint: number > 0
  final Expression<double>? weight;

  @override
  List<Object?> get props => [
        weight,
      ];

  DivMatchParentSize copyWith({
    Expression<double>? Function()? weight,
  }) =>
      DivMatchParentSize(
        weight: weight != null ? weight.call() : this.weight,
      );

  static DivMatchParentSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivMatchParentSize(
        weight: safeParseDoubleExpr(
          json['weight'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivMatchParentSize resolve(DivVariableContext context) {
    weight?.resolve(context);
    return this;
  }
}
