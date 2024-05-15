// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_fixed_size.dart';

class DivStretchIndicatorItemPlacement with EquatableMixin {
  const DivStretchIndicatorItemPlacement({
    this.itemSpacing = const DivFixedSize(
      value: ValueExpression(5),
    ),
    this.maxVisibleItems = const ValueExpression(10),
  });

  static const type = "stretch";
  // default value: const DivFixedSize(value: ValueExpression(5),)
  final DivFixedSize itemSpacing;
  // constraint: number > 0; default value: 10
  final Expression<int> maxVisibleItems;

  @override
  List<Object?> get props => [
        itemSpacing,
        maxVisibleItems,
      ];

  static DivStretchIndicatorItemPlacement? fromJson(
      Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivStretchIndicatorItemPlacement(
      itemSpacing: safeParseObj(
        DivFixedSize.fromJson(json['item_spacing']),
        fallback: const DivFixedSize(
          value: ValueExpression(5),
        ),
      )!,
      maxVisibleItems: safeParseIntExpr(
        json['max_visible_items'],
        fallback: 10,
      )!,
    );
  }
}
