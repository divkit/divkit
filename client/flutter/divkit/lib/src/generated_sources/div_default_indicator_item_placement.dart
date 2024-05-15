// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_fixed_size.dart';

class DivDefaultIndicatorItemPlacement with EquatableMixin {
  const DivDefaultIndicatorItemPlacement({
    this.spaceBetweenCenters = const DivFixedSize(
      value: ValueExpression(15),
    ),
  });

  static const type = "default";
  // default value: const DivFixedSize(value: ValueExpression(15),)
  final DivFixedSize spaceBetweenCenters;

  @override
  List<Object?> get props => [
        spaceBetweenCenters,
      ];

  static DivDefaultIndicatorItemPlacement? fromJson(
      Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivDefaultIndicatorItemPlacement(
      spaceBetweenCenters: safeParseObj(
        DivFixedSize.fromJson(json['space_between_centers']),
        fallback: const DivFixedSize(
          value: ValueExpression(15),
        ),
      )!,
    );
  }
}
